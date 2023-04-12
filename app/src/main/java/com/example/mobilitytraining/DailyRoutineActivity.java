package com.example.mobilitytraining;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

public class DailyRoutineActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private Button buttonDuration;
    boolean clicked;
    private int duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_routine);

        buttonDuration = findViewById(R.id.buttonDuration);
        Button buttonStart = findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!clicked) {

                    Toast.makeText(getApplicationContext(), "Please choose a duration!", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences sharedPreferences = getSharedPreferences("shared_duration", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("duration", duration);
                    editor.apply();
                    startActivity(new Intent(DailyRoutineActivity.this, StretchMainActivity.class));
                }
            }
        });
    }

    public void showPopup(View v) {

        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.stretch_duration_menu);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.min5:
                duration = 5;
                buttonDuration.setText("5 minutes");
                clicked = true;
                return true;
            case R.id.min10:
                duration = 10;
                buttonDuration.setText("10 minutes");
                clicked = true;
                return true;
            case R.id.min15:
                duration = 15;
                buttonDuration.setText("15 minutes");
                clicked = true;
                return true;
            case R.id.min20:
                duration = 20;
                buttonDuration.setText("20 minutes");
                clicked = true;
                return true;
            case R.id.min25:
                duration = 25;
                buttonDuration.setText("25 minutes");
                clicked = true;
                return true;
            case R.id.min30:
                duration = 30;
                buttonDuration.setText("30 minutes");
                clicked = true;
                return true;
            default:
                clicked = false;
                return false;
        }
    }
}