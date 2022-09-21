package com.example.synchronizedclock;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.IOException;
import java.net.InetAddress;
import java.text.DateFormat;
import java.util.Date;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

public class MainActivity extends AppCompatActivity {        // NTP server address

    private boolean running = true;                          // Thread running flag
    private TextView UITxt;                                  // UI text view
    private UIHandler UIhandler;                             // UI handler

    private static final String SERVER_NAME = "pool.ntp.org";//Address to NTP server

    @Override
    protected void onCreate(Bundle savedInstanceState) {     //Create the activity
        Date date = new Date();                              //Get the current date
        super.onCreate(savedInstanceState);                  //Create the activity
        setContentView(R.layout.activity_main);              //Set the layout
        UITxt = (TextView) findViewById(R.id.Clock);         //Get the text view
        final Button updateUIBtn = findViewById(R.id.button);//Get the button
        updateUIBtn.setText("Connect");                      //Set the button text
        UIhandler = new UIHandler();                         //Create the handler

        updateUIBtn.setOnClickListener(new View.OnClickListener() {     //Button to start the clock

            public void onClick(View v) {                     //Start the clock
                if (updateUIBtn.getText() == "Connect") {     //If the button is pressed
                    UIThread thread = new UIThread();         //Start the thread
                    thread.start();                           //Start the thread
                    updateUIBtn.setText("Disconnect");        //Change the button text
                    running = true;                           //Set the running variable to true
                } else {
                    updateUIBtn.setText("Connect");           //Change the button text
                    running = false;                          //Set the running variable to false
                }
            }
        });
    }
        public Date getCurrentNetworkTime() {                //Get the current time from the NTP server
            System.out.println("trying to set NTPClient");   //Print to the console
            NTPUDPClient timeClient = new NTPUDPClient();    //Create a new NTP client
            timeClient.setDefaultTimeout(4000);              //Set the timeout to 4 seconds
            TimeInfo timeInfo;                               //Create a new TimeInfo object
            try {
                InetAddress inetAddress = InetAddress.getByName(SERVER_NAME);   //Get the address of the NTP server
                timeInfo = timeClient.getTime(inetAddress);  //Get the time from the NTP server
                long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();//Get the time from the NTP server
                Date date = new Date(returnTime);           //Create a new date object
                System.out.println("NTPTime ");             //Print to the console
                return date;                                //Return the date
            } catch (IOException e) {                       //If there is an error
                System.out.println("SystemTime ");          //Print to the console
                return new Date();                          //Return the current system time
            }
        }
        private class UIHandler extends Handler {          //Create a new UIHandler
            @Override
            public void handleMessage(Message msg) {       //Handle the message
                super.handleMessage(msg);                  //Handle the message
                Bundle bundle = msg.getData();             //Get the bundle
                String Time = bundle.getString("Time");  //Get the time from the bundle
                UITxt.setText(Time);                       //Set the text of the text view to the time
            }
        }
        protected class UIThread extends Thread {         //Create a new UIThread
            @Override
            public void run() {                           //Run the thread
                while (running) {                         //While the running variable is true
                    try {                                 //Try to run the code
                        Thread.sleep(1000);         //Sleep for 1 second
                    } catch (InterruptedException e) {    //If there is an error
                        e.printStackTrace();              //Print the error
                    }
                    Message msg = new Message();          //Create a new message
                    Bundle bundle = new Bundle();         //Create a new bundle
                    String date = DateFormat.getTimeInstance(DateFormat.MEDIUM).format(getCurrentNetworkTime());//Get the current time
                    bundle.putString("Time", date);       //Put the time in the bundle
                    msg.setData(bundle);                  //Put the bundle in the message
                    MainActivity.this.UIhandler.sendMessage(msg);//Send the message to the UIHandler
                }
            }
        }
    }
