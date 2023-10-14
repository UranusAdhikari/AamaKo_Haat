package com.food.aamakohaat.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.food.aamakohaat.R;
import com.food.aamakohaat.ReusableCode.ReusableCodeForAll;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    CountryCodePicker Cpp;
    FirebaseAuth FAuth;
    Button signup;

    TextInputLayout Fname, Lname, Email, Pass, cfpass, mobileno;
    String fname, emailid, lname, password, confirmpassword, mobile, role = "Users";
    String emailpattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // for dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Fname = findViewById(R.id.Firstname);
        Lname = findViewById(R.id.Lastname);
        Email = findViewById(R.id.Email);
        Pass = findViewById(R.id.Pwd);
        cfpass = findViewById(R.id.Cpass);
        mobileno = findViewById(R.id.Mobileno);
        signup = findViewById(R.id.Signup);
        signup.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
        Cpp = findViewById(R.id.CountryCode);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        FAuth = FirebaseAuth.getInstance();

        Button signupButton = findViewById(R.id.button5);
        signupButton.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fname = Fname.getEditText().getText().toString().trim();
                lname = Lname.getEditText().getText().toString().trim();
                emailid = Email.getEditText().getText().toString().trim();
                mobile = mobileno.getEditText().getText().toString().trim();
                password = Pass.getEditText().getText().toString().trim();
                confirmpassword = cfpass.getEditText().getText().toString().trim();

                if (isValid()) {
                    final ProgressDialog mDialog = new ProgressDialog(SignUpActivity.this);
                    mDialog.setCancelable(false);
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.setMessage("Registering, please wait...");
                    mDialog.show();

                    FAuth.createUserWithEmailAndPassword(emailid, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sanitize the email for use as a key
                                String sanitizedEmail = emailid.replace(".", ",");

                                // Store user data
                                databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(sanitizedEmail);
                                final HashMap<String, String> userMap = new HashMap<>();
                                userMap.put("Fname", fname);
                                userMap.put("Lname", lname);
                                userMap.put("EmailID", emailid);
                                userMap.put("Mobile", mobile);
                                userMap.put("Password", password);
                                userMap.put("ConfirmPassword", confirmpassword);

                                databaseReference.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        mDialog.dismiss();

                                        // Store the user's role
                                        databaseReference = FirebaseDatabase.getInstance().getReference("RoleOfUsers").child(sanitizedEmail);
                                        final HashMap<String, String> roleMap = new HashMap<>();
                                        roleMap.put("Role", role);

                                        databaseReference.setValue(roleMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    String phonenumber = Cpp.getSelectedCountryCodeWithPlus() + mobile;
                                                    Intent b = new Intent(SignUpActivity.this, VerifyPhone.class);
                                                    b.putExtra("phonenumber", phonenumber);
                                                    startActivity(b);
                                                } else {
                                                    mDialog.dismiss();
                                                    ReusableCodeForAll.ShowAlert(SignUpActivity.this, "Error", task.getException().getMessage());
                                                }
                                            }
                                        });
                                    }
                                });
                            } else {
                                mDialog.dismiss();
                                ReusableCodeForAll.ShowAlert(SignUpActivity.this, "Error", task.getException().getMessage());
                            }
                        }
                    });
                }
            }
        });
    }

    public boolean isValid() {
        Email.setErrorEnabled(false);
        Email.setError("");
        Fname.setErrorEnabled(false);
        Fname.setError("");
        Lname.setErrorEnabled(false);
        Lname.setError("");
        Pass.setErrorEnabled(false);
        Pass.setError("");
        mobileno.setErrorEnabled(false);
        mobileno.setError("");
        cfpass.setErrorEnabled(false);
        cfpass.setError("");

        boolean isValidname = false, isValidemail = false, isvalidpassword = false, isvalidconfirmpassword = false, isvalid = false, isvalidmobileno = false, isvalidlname = false, isvalidhousestreetno = false, isvalidarea = false, isvalidpostcode = false;
        if (TextUtils.isEmpty(fname)) {
            Fname.setErrorEnabled(true);
            Fname.setError("Firstname is required");
        } else {
            isValidname = true;
        }
        if (TextUtils.isEmpty(lname)) {
            Lname.setErrorEnabled(true);
            Lname.setError("Lastname is required");
        } else {
            isvalidlname = true;
        }
        if (TextUtils.isEmpty(emailid)) {
            Email.setErrorEnabled(true);
            Email.setError("Email is required");
        } else {
            if (emailid.matches(emailpattern)) {
                isValidemail = true;
            } else {
                Email.setErrorEnabled(true);
                Email.setError("Enter a valid Email Address");
            }
        }
        if (TextUtils.isEmpty(password)) {
            Pass.setErrorEnabled(true);
            Pass.setError("Password is required");
        } else {
            if (password.length() < 6) {
                Pass.setErrorEnabled(true);
                Pass.setError("Password too weak");
            } else {
                isvalidpassword = true;
            }
        }
        if (TextUtils.isEmpty(confirmpassword)) {
            cfpass.setErrorEnabled(true);
            cfpass.setError("Confirm Password is required");
        } else {
            if (!password.equals(confirmpassword)) {
                cfpass.setErrorEnabled(true);
                cfpass.setError("Password doesn't match");
            } else {
                isvalidconfirmpassword = true;
            }
        }
        if (TextUtils.isEmpty(mobile)) {
            mobileno.setErrorEnabled(true);
            mobileno.setError("Mobile number is required");
        } else {
            if (mobile.length() < 10) {
                mobileno.setErrorEnabled(true);
                mobileno.setError("Invalid mobile number");
            } else {
                isvalidmobileno = true;
            }
        }

        isvalid = (isValidname && isvalidlname && isValidemail && isvalidconfirmpassword && isvalidpassword && isvalidmobileno) ? true : false;
        return isvalid;
    }
}
