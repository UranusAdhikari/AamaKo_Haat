package com.food.aamakohaat.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.food.aamakohaat.R;
import com.food.aamakohaat.ReusableCode.ReusableCodeForAll;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyPhone extends AppCompatActivity {

    String verificationId;
    FirebaseAuth FAuth;
    Button verify;
    Button Resend;
    TextView txt;
    TextInputEditText entercode;
    String phonenumber;
    boolean isFirstAttempt = true; // Add this flag
    boolean isResendButtonEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        // Initialize Firebase Authentication
        FAuth = FirebaseAuth.getInstance();

        // for dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        // end

        phonenumber = getIntent().getStringExtra("phonenumber").trim();

        sendverificationcode(phonenumber);
        entercode = findViewById(R.id.phoneno);
        txt = findViewById(R.id.text);
        Resend = findViewById(R.id.Resendotp);
        Resend.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.dim_red));
        verify = findViewById(R.id.Verify);
        verify.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
        Resend.setVisibility(View.VISIBLE);
        txt.setVisibility(View.INVISIBLE);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = entercode.getText().toString().trim();

                if (code.isEmpty() || code.length() < 6) {
                    // Update the TextInputLayout to show an error
                    TextInputLayout textInputLayout = findViewById(R.id.Firstname);
                    textInputLayout.setError("Enter a valid code");
                    textInputLayout.requestFocus();
                    return;
                }

                // Call verifyCode and remove Resend.setVisibility(View.INVISIBLE)
                verifyCode(code);
            }
        });

        // Use the isFirstAttempt flag to set the initial timer duration
        long initialTimerDuration = isFirstAttempt ? 10000 : 60000; // Updated to 10 seconds for the first attempt

        new CountDownTimer(initialTimerDuration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                txt.setVisibility(View.VISIBLE);
                txt.setText("Resend Code again in " + millisUntilFinished / 1000 + " Seconds");

                // Update the 'text' TextView
                TextView timerTextView = findViewById(R.id.text);
                timerTextView.setText("Resend Code again in " + millisUntilFinished / 1000 + " seconds");
            }

            @Override
            public void onFinish() {
                txt.setVisibility(View.INVISIBLE);

                // Enable the 'Resend' button and change its background color to red
                Resend.setEnabled(true);
                Resend.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));

                // Clear the 'text' TextView when the countdown is finished
                TextView timerTextView = findViewById(R.id.text);
                timerTextView.setText("");
            }
        }.start();

        // Modify the 'onClick' listener for the 'Resend' button
        Resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Disable the button and change its background color while sending the code
                Resend.setEnabled(false);
                Resend.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.dim_red));

                Resendotp(phonenumber);

                new CountDownTimer(60000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        txt.setVisibility(View.VISIBLE);
                        txt.setText("Resend Code within " + millisUntilFinished / 1000 + " Seconds");
                    }

                    @Override
                    public void onFinish() {
                        txt.setVisibility(View.INVISIBLE);

                        // Enable the 'Resend' button and change its background color to red
                        Resend.setEnabled(true);
                        Resend.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
                    }
                }.start();
            }
        });
    }

    private void Resendotp(String phonenumber) {
        sendverificationcode(phonenumber);
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        linkCredential(credential);
    }

    private void linkCredential(PhoneAuthCredential credential) {
        FAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(VerifyPhone.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                                            FAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(VerifyPhone.this);
                                                        builder.setMessage("Registered Successfully. A link has been send to your email. Please Verify your Email there.");
                                                        builder.setCancelable(false);
                                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();

                                                                // Navigate to MainActivity or wherever needed
                                                                Intent intent = new Intent(VerifyPhone.this, LoginActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        });
                                                        AlertDialog alert = builder.create();
                                                        alert.show();
                                                    } else {
                                                        ReusableCodeForAll.ShowAlert(VerifyPhone.this, "Error", task.getException().getMessage());
                                                    }
                                                }
                                            });
                        } else {
                            ReusableCodeForAll.ShowAlert(VerifyPhone.this, "Error", task.getException().getMessage());
                        }
                    }
                });
    }

    private void sendverificationcode(String number) {
        PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(FAuth)
                .setPhoneNumber(number)
                .setActivity(this)
                .setCallbacks(mCallBack);

        // Set the timeout based on isFirstAttempt flag
        if (isFirstAttempt) {
            builder.setTimeout(10L, TimeUnit.SECONDS); // Updated to 10 seconds for the first attempt
        } else {
            builder.setTimeout(60L, TimeUnit.SECONDS);
        }

        PhoneAuthOptions options = builder.build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                entercode.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyPhone.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };
}