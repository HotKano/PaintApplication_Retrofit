package com.kimjongwoo.paintapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class LauncherActivity extends AppCompatActivity {

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logo_timer();
    }

    // timer setting
    private void logo_timer() {
        timer = new Timer();

        TimerTask logo_Task = new TimerTask() {
            int time = 0;
            int time_range = 3;

            @Override
            public void run() {
                time++;
                if (time >= time_range) {
                    Intent intent = new Intent(LauncherActivity.this, PaintActivity.class);
                    startActivity(intent);
                    timer.cancel();
                    finish();
                }
            }
        };
        timer.schedule(logo_Task, 0, 1000);
    }
}
