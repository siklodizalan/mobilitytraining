package com.example.mobilitytraining;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class StretchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stretch);

        CardView dailyRoutine = findViewById(R.id.cardDailyRoutine);
        dailyRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StretchActivity.this, DailyRoutineActivity.class));
            }
        });
    }
}