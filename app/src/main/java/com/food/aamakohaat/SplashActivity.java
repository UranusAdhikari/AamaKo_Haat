package com.food.aamakohaat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.food.aamakohaat.internet.NetworkMonitorService;
import com.food.aamakohaat.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private boolean isInternetConnected = false;
    private ProgressBar loadingProgressBar;
    private ImageView logoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        logoImageView = findViewById(R.id.logohome);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.popup_animation);
        logoImageView.startAnimation(animation);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.splash));
        }



            // Start the NetworkMonitorService to continuously check for internet connectivity
            startService(new Intent(this, NetworkMonitorService.class));

            // Check for location permission and request it if necessary
            if (hasLocationPermission()) {
                // If location permission is granted, proceed to check for internet connection
                checkInternetConnection();
            } else {
                requestLocationPermission();
            }
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted, proceed to check internet connection
                checkInternetConnection();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        showLocationPermissionRationaleDialog();
                    } else {
                        startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null)));
                        finish();
                    }
                }
            }
        }
    }

    private void showLocationPermissionRationaleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Location Permission Required")
                .setMessage("We need your location to provide you with the best experience. Please grant location permission.")
                .setPositiveButton("Try Again", (dialog, which) -> requestLocationPermission())
                .setNegativeButton("Exit App", (dialog, which) -> finish())
                .setOnCancelListener((dialog) -> finish())
                .show();
    }

    private void checkInternetConnection() {
        // Show the loading indicator
        loadingProgressBar.setVisibility(View.VISIBLE);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Check internet connection
            isInternetConnected = isInternetConnected();
            if (isInternetConnected) {
                // If internet is connected, proceed to check user login status
                checkUserLoginStatus();
            } else {
                // If there is no internet connection, show a dialogue box
                showNoInternetConnectionDialog();
            }
        }, 2000); // 2000 milliseconds = 2 seconds delay for the splash screen
    }
    private void checkUserLoginStatus() {
        // Check if the user is signed in
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            // User is not signed in, redirect to the login activity
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            // User is signed in, redirect to the main activity
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
        }
        finish();
    }

    private boolean isInternetConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private void showNoInternetConnectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Internet Connection")
                .setMessage("You need an internet connection to use this app. Please check your internet connection and try again.")
                .setNegativeButton("Try Again", (dialog, which) -> checkInternetConnection())
                .show();
    }
}
