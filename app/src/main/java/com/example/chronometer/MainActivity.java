package com.example.chronometer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView textViewTimer;
    private Button btnStart, btnStop;

    private Handler handler;
    private Runnable runnable;
    private long startTime = 0L;
    private long elapsedTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewTimer = findViewById(R.id.textViewTimer);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);

        handler = new Handler();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
            }
        });
    }

    private void startTimer() {
        startTime = System.currentTimeMillis();
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                elapsedTime = System.currentTimeMillis() - startTime;
                updateTimerText(elapsedTime);
                handler.postDelayed(this, 1000); // Actualiza cada segundo
            }
        }, 1000);
        btnStart.setEnabled(false);
        btnStop.setEnabled(true);
    }

    private void stopTimer() {
        handler.removeCallbacks(runnable);
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
    }

    private void updateTimerText(long timeInMillis) {
        int seconds = (int) (timeInMillis / 1000);
        int minutes = seconds / 60;
        int hours = minutes / 60;

        seconds = seconds % 60;
        minutes = minutes % 60;

        String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        textViewTimer.setText(time);
    }
}