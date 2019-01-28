package ru.alexfitness.testwakelock;

import android.content.Intent;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private final static int TO = 120000;

    private PowerManager.WakeLock wl;

    private TextView mainTextView;

    private long uptime = SystemClock.uptimeMillis();
    private long uptimeAndSleep = SystemClock.elapsedRealtime();

    private Handler handler = new Handler();

    private Runnable authEndRun = new Runnable() {
        @Override
        public void run() {
            mainTextView.append("Auth_end " + allTimeDif(uptimeAndSleep) + ", " + uptimeDif(uptime)+ "\n");
            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
            startActivity(intent);
            if(wl!=null){
                if(wl.isHeld()){
                    wl.release();
                    mainTextView.append("Wake lock realeased\n");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainTextView = findViewById(R.id.mainTextView);
    }

    private void startTimer(){
        //mainTextView.append("Start timer\n");
        if(handler!=null) {
            handler.removeCallbacks(authEndRun);
            handler.postDelayed(authEndRun, TO);
            uptime = SystemClock.uptimeMillis();
            uptimeAndSleep = SystemClock.elapsedRealtime();
            mainTextView.append("Start timer\n");
        }
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getLocalClassName());
        wl.acquire();
        mainTextView.append("Wake lock acquire\n");
    }

    private void stopTimer(){
        //mainTextView.append("Stop timer\n");
        if(handler!=null){
            handler.removeCallbacks(authEndRun);
            mainTextView.append("Stop timer " + allTimeDif(uptimeAndSleep) + ", " + uptimeDif(uptime)+ "\n");
        }if(wl!=null){
            if(wl.isHeld()){
                wl.release();
                mainTextView.append("Wake lock realeased\n");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        stopTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        startTimer();
    }

    private long allTimeDif(long lastTS){
        return (SystemClock.elapsedRealtime() - lastTS)/1000;
    }

    private long uptimeDif(long lastTS){
        return (SystemClock.uptimeMillis() - lastTS)/1000;
    }
}
