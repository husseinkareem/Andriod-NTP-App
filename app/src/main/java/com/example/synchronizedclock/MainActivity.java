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
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

public class MainActivity extends AppCompatActivity {

    private TextView UITxt;
    private Button updateUIBtn;
    private UIHandler UIhandler;
    private static final String SERVER_NAME = "pool.ntp.org"; //Address to NTP server

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UITxt = (TextView) findViewById(R.id.Clock);
        final Button updateUIBtn = findViewById(R.id.button);
        updateUIBtn.setText("Connect");
        UIhandler = new UIHandler();
        UIThread thread = new UIThread();
        updateUIBtn.setOnClickListener(new View.OnClickListener() {

        public void onClick(View v) {
            if (updateUIBtn.getText() == "Connect") {
                updateUIBtn.setText("Diconnect");
                thread.start();
                System.out.println("trådstart");
            } else {
                updateUIBtn.setText("Connect");
                thread.interrupt();
                System.out.println("trådstoppp");
            }

        }
    });
}
    public Date getCurrentNetworkTime() {
        System.out.println("trying to set NTPClient");
        NTPUDPClient timeClient = new NTPUDPClient();
        timeClient.setDefaultTimeout(4000);
        TimeInfo timeInfo;
        try {
        InetAddress inetAddress = InetAddress.getByName(SERVER_NAME);
        timeInfo = timeClient.getTime(inetAddress);
        long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
        Date date = new Date(returnTime);
            //System.out.println("NTP tid ");
        return date;

        } catch (IOException e) {
           // System.out.println("Systemtiden ");
            return new Date();
        }
    }
    private class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String Time = bundle.getString("Time");
            UITxt.setText(Time);
        }
    }
    protected class UIThread extends Thread{
        @Override
        public void run() {
            while (true) {
                if(Thread.interrupted()){
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                Bundle bundle = new Bundle();
                String date = DateFormat.getTimeInstance(DateFormat.MEDIUM).format(getCurrentNetworkTime());
                bundle.putString("Time", date);
                msg.setData(bundle);
                MainActivity.this.UIhandler.sendMessage(msg);

          }
        }
    }
}


