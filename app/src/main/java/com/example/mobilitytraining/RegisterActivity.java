package com.example.mobilitytraining;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    EditText edUsername, edEmail, edPassword, edConfirm;
    Button btnRegister;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edUsername = findViewById(R.id.editTextRegisterUsername);
        edEmail = findViewById(R.id.editTextRegisterEmail);
        edPassword = findViewById(R.id.editTextRegisterPassword);
        edConfirm = findViewById(R.id.editTextRegisterConfirmPassword);
        btnRegister = findViewById(R.id.buttonRegister);
        textView = findViewById(R.id.textViewExistingUser);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = edUsername.getText().toString();
                String email = edEmail.getText().toString();
                String password = edPassword.getText().toString();
                String confirm = edConfirm.getText().toString();
                UserDatabase database = new UserDatabase(getApplicationContext(), "MobilityTraining", null, 1);

                if (username.length() == 0 || email.length() == 0 || password.length() == 0 || confirm.length() == 0) {

                    Toast.makeText(getApplicationContext(), "Please fill in all details!", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (isValidEmailAddress(email)) {

                        if (password.compareTo(confirm) == 0) {

                            if (isValidPassword(password)) {

                                database.register(username, email, password);
                                Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            } else {

                                Toast.makeText(getApplicationContext(), "Password must contain at least 8 characters and must have letter, digit and special symbol in it!", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            Toast.makeText(getApplicationContext(), "Password and Confirm Password did not match!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {

                        Toast.makeText(getApplicationContext(), "The e-mail address is not valid!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public static boolean isValidPassword(String password) {

        int f1 = 0, f2 = 0, f3 = 0;

        if (password.length() < 8) {

            return false;
        }
        for (int p = 0; p < password.length(); p++) {

            if (Character.isLetter(password.charAt(p))) {

                f1 = 1;
                break;
            }
        }
        for (int p = 0; p < password.length(); p++) {

            if (Character.isDigit(password.charAt(p))) {

                f2 = 1;
                break;
            }
        }
        for (int p = 0; p < password.length(); p++) {

            char c = password.charAt(p);
            if (c >= 33 && c <= 64) {

                f3 = 1;
                break;
            }
        }
        if (f1 == 1 && f2 == 1 && f3 == 1) {

            return true;
        }
        return false;
    }

    public static boolean isValidEmailAddress(String email) {

        int f1 = 0, f2 = 0, f3 = 0;

        if (email.length() < 3) {

            return false;
        }
        for (int p = 0; p < email.length(); p++) {

            char c = email.charAt(p);
            if (c == 64) {

                return true;
            }
        }
        return false;
    }
}