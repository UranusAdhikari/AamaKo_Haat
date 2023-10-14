package com.food.aamakohaat.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.food.aamakohaat.MainActivity;
import com.food.aamakohaat.R;
import com.food.aamakohaat.ReusableCode.ReusableCodeForAll;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout email, pass;
    Button Signin, signup;
    TextView Forgotpassword;
    TextView txt;
    FirebaseAuth FAuth;
    String em;
    String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // for dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        // end

        email = findViewById(R.id.Lemail);
        pass = findViewById(R.id.Lpassword);
        Signin = findViewById(R.id.button4);  // Changed button name to Signin
        Signin.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
        signup = findViewById(R.id.button5);  // Changed button name to Signin
        signup.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
        txt = findViewById(R.id.textView3);
        Forgotpassword = findViewById(R.id.forgotpass);
        FAuth = FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the SignUpActivity when the "Sign Up" button is clicked
                Intent signUpIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(signUpIntent);
                finish(); // Finish the LoginActivity so that you don't return to it when pressing back from SignUpActivity
            }
        });

        Signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                em = email.getEditText().getText().toString().trim();
                pwd = pass.getEditText().getText().toString().trim();
                if (isValid()) {
                    final ProgressDialog mDialog = new ProgressDialog(LoginActivity.this);
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.setCancelable(false);
                    mDialog.setMessage("Logging in...");
                    mDialog.show();

                    // Properly sign in the user
                    FAuth.signInWithEmailAndPassword(em, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            mDialog.dismiss();
                            if (task.isSuccessful()) {
                                if (FAuth.getCurrentUser().isEmailVerified()) {
                                   // Toast.makeText(LoginActivity.this, "You are logged in", Toast.LENGTH_SHORT).show();

                                    // Set a flag in SharedPreferences indicating the user is logged in
                                    SharedPreferences sharedPreferences = getSharedPreferences("AamaKo Haat", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("isLoggedIn", true);
                                    editor.apply();

                                    Intent z = new Intent(LoginActivity.this, MainActivity.class);  // Replace YourMainActivity with the actual main activity
                                    startActivity(z);
                                    finish();
                                } else {
                                    ReusableCodeForAll.ShowAlert(LoginActivity.this, "", "Please Verify your Email");
                                }
                            } else {
                                ReusableCodeForAll.ShowAlert(LoginActivity.this, "Error", task.getException().getMessage());
                            }
                        }
                    });
                }
            }
        });

        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Register = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(Register);
                finish();
            }
        });

        Forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(LoginActivity.this, ForgotPassActivity.class);
                startActivity(a);
                finish();
            }
        });
    }

    String emailpattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    public boolean isValid() {
        email.setErrorEnabled(false);
        email.setError("");
        pass.setErrorEnabled(false);
        pass.setError("");

        boolean isvalidemail = false, isvalidpassword = false, isvalid = false;
        if (TextUtils.isEmpty(em)) {
            email.setErrorEnabled(true);
            email.setError("Email is required");
        } else {
            if (em.matches(emailpattern)) {
                isvalidemail = true;
            } else {
                email.setErrorEnabled(true);
                email.setError("Enter a valid Email Address");
            }
        }
        if (TextUtils.isEmpty(pwd)) {
            pass.setErrorEnabled(true);
            pass.setError("Password is required");
        } else {
            isvalidpassword = true;
        }
        isvalid = (isvalidemail && isvalidpassword) ? true : false;
        return isvalid;
    }
}
