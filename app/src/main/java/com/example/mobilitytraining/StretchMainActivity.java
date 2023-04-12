package com.example.mobilitytraining;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.squareup.picasso.Picasso;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class StretchMainActivity extends AppCompatActivity {

    private static final long ONE_MINUTE_IN_MILLIS = 60000;
    private static final long TWO_MINUTES_IN_MILLIS = 120000;
    private static final long THREE_MINUTES_IN_MILLIS = 180000;
    private static final long FIVE_SECONDS_IN_MILLIS = 5000;
    private int minute = 1;
    private int currentLength = 0;
    private int stretchLength;

    private int currentStretchID;
    private String currentStretchIDFormatted;
    private Random random;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private int numberOfStretches = 46;
    private ArrayList<Stretches> stretches = new ArrayList<Stretches>();
    private ArrayList<Integer> occupied = new ArrayList<Integer>();

    private String stretchName;
    private String description;
    private int duration;
    private String imageLink;
    private ArrayList<String> musclesInvolved;
    private boolean twoSided = false;
    private int next;
    private int twoSidesInARow;

    private TextView mTextViewCountDown;
    private TextView textViewStretchName;
    private TextView textViewDescription;
    private ImageView imageViewStretchImage;
    private Button mButtonStartPause;
    private Button mButtonReset;
    private Button mButtonNext;
    private Button mButtonPrevious;

    private CountDownTimer mCountDownTimer;

    private boolean mTimerRunning = false;
    private boolean mTimerFinished;

    private long mTimeLeftInMillis;

    private SoundPool soundPool;
    private int nextStretchSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        loadSound();

        SharedPreferences sharedPreferences = getSharedPreferences("shared_duration", Context.MODE_PRIVATE);
        stretchLength = sharedPreferences.getInt("duration", 5);
        Toast.makeText(getApplicationContext(), "Duration: " + stretchLength + " minutes", Toast.LENGTH_SHORT).show();

        if (stretchLength > numberOfStretches) {

            stretchLength = numberOfStretches;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stretch_main);

        readData(new DataReceivedListener() {

            @Override
            public void onDataReceived(ArrayList<Stretches> stretches) {

                mTextViewCountDown = findViewById(R.id.textViewCountdown);
                mButtonStartPause = findViewById(R.id.buttonStartPause);
                mButtonReset = findViewById(R.id.buttonReset);
                mButtonNext = findViewById(R.id.buttonNext);
                mButtonPrevious = findViewById(R.id.buttonPrevious);
                textViewStretchName = findViewById(R.id.stretchName);
                textViewDescription = findViewById(R.id.textViewDescription);
                imageViewStretchImage = findViewById(R.id.stretchImage);

                Picasso.get().load(stretches.get(minute - 1).imageLink).into(imageViewStretchImage);
                textViewStretchName.setText(stretches.get(minute - 1).stretchName);
                textViewDescription.setText(stretches.get(minute - 1).description);

                resetTimerToFiveSeconds();
                startFiveSecondTimer();

                mButtonStartPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mTimerRunning) {

                            pauseTimer();
                            mTimerFinished = false;
                        } else {

                            if (minute < stretchLength || !mTimerFinished) {

                                startTimer();
                            } else {

                                startActivity(new Intent(StretchMainActivity.this, HomeActivity.class));
                            }
                        }
                    }
                });

                mButtonNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        minute++;

                        Picasso.get().load(stretches.get(minute - 1).imageLink).into(imageViewStretchImage);;
                        textViewStretchName.setText(stretches.get(minute - 1).stretchName);
                        textViewDescription.setText(stretches.get(minute - 1).description);

                        mButtonPrevious.setVisibility(View.VISIBLE);
                        if (minute == stretchLength) {

                            mButtonNext.setVisibility(View.INVISIBLE);
                        }
                        pauseTimer();
                        resetTimerToFiveSeconds();
                        startFiveSecondTimer();
                    }
                });

                mButtonPrevious.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        minute--;

                        Picasso.get().load(stretches.get(minute - 1).imageLink).into(imageViewStretchImage);;
                        textViewStretchName.setText(stretches.get(minute - 1).stretchName);
                        textViewDescription.setText(stretches.get(minute - 1).description);

                        mButtonNext.setVisibility(View.VISIBLE);
                        if (minute == 1) {

                            mButtonPrevious.setVisibility(View.INVISIBLE);
                        }
                        pauseTimer();
                        resetTimerToFiveSeconds();
                        startFiveSecondTimer();
                    }
                });

                mButtonReset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(minute > 1) {

                            mButtonPrevious.performClick();
                            mButtonNext.performClick();
                        } else {

                            mButtonNext.performClick();
                            mButtonPrevious.performClick();
                        }
                    }
                });

                updateCountDownText();
            }
        });
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        soundPool.release();
        soundPool = null;
    }

    private void loadSound() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {

            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }

        nextStretchSound = soundPool.load(this, R.raw.next_stretch_sound, 1);
    }

    private void startTimer() {

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {

                soundPool.play(nextStretchSound, 1, 1, 0, 0, 1);

                mTimerRunning = false;
                if(minute < stretchLength) {

                    mButtonStartPause.setText("start");
                    mButtonStartPause.setVisibility(View.INVISIBLE);
                    mButtonReset.setVisibility(View.VISIBLE);
                    mButtonNext.performClick();
                }
                else {

                    mButtonStartPause.setText("finish");
                    mButtonStartPause.setVisibility(View.VISIBLE);
                    mButtonReset.setVisibility(View.VISIBLE);
                }
            }
        }.start();

        mTimerRunning = true;
        mButtonStartPause.setText("pause");
        mButtonReset.setVisibility(View.INVISIBLE);
    }

    private void startFiveSecondTimer() {

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                mTimerFinished = true;
                pauseTimer();

                if (stretches.get(minute - 1).duration == 1) {

                    resetTimerToOneMinute();
                } else if (stretches.get(minute - 1).duration == 2) {

                    resetTimerToTwoMinutes();
                } else {

                    resetTimerToThreeMinutes();
                }
                startTimer();
            }
        }.start();

        mTimerRunning = true;
        mButtonStartPause.setText("pause");
        mButtonReset.setVisibility(View.INVISIBLE);
    }

    private void pauseTimer() {

        mCountDownTimer.cancel();
        mTimerRunning = false;
        mButtonStartPause.setText("start");
        mButtonReset.setVisibility(View.VISIBLE);
    }
    private void resetTimerToOneMinute() {

        mTimeLeftInMillis = ONE_MINUTE_IN_MILLIS;
        updateCountDownText();
        mButtonReset.setVisibility(View.INVISIBLE);
        mButtonStartPause.setVisibility(View.VISIBLE);
    }

    private void resetTimerToTwoMinutes() {

        mTimeLeftInMillis = TWO_MINUTES_IN_MILLIS;
        updateCountDownText();
        mButtonReset.setVisibility(View.INVISIBLE);
        mButtonStartPause.setVisibility(View.VISIBLE);
    }

    private void resetTimerToThreeMinutes() {

        mTimeLeftInMillis = THREE_MINUTES_IN_MILLIS;
        updateCountDownText();
        mButtonReset.setVisibility(View.INVISIBLE);
        mButtonStartPause.setVisibility(View.VISIBLE);
    }

    private void resetTimerToFiveSeconds() {

        mTimeLeftInMillis = FIVE_SECONDS_IN_MILLIS;
        updateCountDownText();
        mButtonReset.setVisibility(View.INVISIBLE);
        mButtonStartPause.setVisibility(View.VISIBLE);
    }

    private void updateCountDownText() {

        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        mTextViewCountDown.setText(timeLeftFormatted);
    }

    private void readData(final DataReceivedListener listener) {

        random = new Random();

        firebaseDatabase = FirebaseDatabase.getInstance("https://siklodizalan-mobilitytraining-default-rtdb.firebaseio.com/");

        databaseReference = firebaseDatabase.getReference("Stretches"); //.child(currentStretchIDFormatted);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    while (currentLength < stretchLength) {

                        if (!twoSided) {

                            twoSidesInARow = 0;
                            do {
                                currentStretchID = random.nextInt(numberOfStretches) + 1;
                            } while (occupied.contains(currentStretchID));
                        } else {

                            if (twoSidesInARow == 1) {

                                twoSidesInARow = 0;
                                do {
                                    currentStretchID = random.nextInt(numberOfStretches) + 1;
                                } while (occupied.contains(currentStretchID));
                            } else {

                                currentStretchID += next;
                                twoSidesInARow++;
                            }
                        }
                        occupied.add(currentStretchID);
                        currentStretchIDFormatted = String.format("%3s", currentStretchID).replace(' ', '0');

                        stretchName = dataSnapshot.child(currentStretchIDFormatted).child("StretchName").getValue().toString();
                        description = dataSnapshot.child(currentStretchIDFormatted).child("Description").getValue().toString();
                        imageLink = dataSnapshot.child(currentStretchIDFormatted).child("Image").getValue().toString();
                        twoSided = Boolean.parseBoolean(dataSnapshot.child(currentStretchIDFormatted).child("TwoSided").getValue().toString());
                        duration = Integer.parseInt(dataSnapshot.child(currentStretchIDFormatted).child("Duration").getValue().toString());
                        currentLength += duration;
                        next = Integer.parseInt(dataSnapshot.child(currentStretchIDFormatted).child("Next").getValue().toString());

                        if ((currentLength == stretchLength && next == 1) ||
                                (currentLength == stretchLength && next == -1) ||
                                (currentLength > stretchLength)) {

                            currentStretchID += next;
                            occupied.add(currentStretchID);
                            twoSided = false;
                            currentLength -= duration;
                        } else {

                            stretches.add(new Stretches(stretchName, description, duration, imageLink, twoSided));
                        }
                    }
                }
                listener.onDataReceived(stretches);
            }

            @Override
            public void onCancelled (@NonNull DatabaseError databaseError) {

            }
        });
    }
}

interface DataReceivedListener {

    void onDataReceived(ArrayList<Stretches> data);
}