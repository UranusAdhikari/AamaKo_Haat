package com.food.aamakohaat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.food.aamakohaat.customer_activity.CartFragment;
import com.food.aamakohaat.customer_activity.ChefFragment;
import com.food.aamakohaat.customer_activity.HomeFragment;
import com.food.aamakohaat.customer_activity.MoreFragment;
import com.food.aamakohaat.customer_activity.ProfileFragment;
import com.food.aamakohaat.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private AlertDialog.Builder exitDialogBuilder;

    @Override
    public void onBackPressed() {
        // Show the exit confirmation dialog
        AlertDialog dialog = exitDialogBuilder
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit the app?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Exit the app when the user selects "Yes"
                        finishAffinity(); // This will close all activities in the app
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog when the user selects "No"
                        dialog.dismiss();
                    }
                })
                .show();
        // Change the color of the positive button
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        int colorRed = ContextCompat.getColor(getApplicationContext(), R.color.red);
        positiveButton.setTextColor(colorRed);
        negativeButton.setTextColor(colorRed);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check for the user's login status
        if (!isUserLoggedIn()) {
            // If the user is not logged in, redirect to the LoginActivity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish(); // Finish the MainActivity
            return;
        }

        // for dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        // end

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new HomeFragment()).commit();

        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        exitDialogBuilder = new AlertDialog.Builder(this);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.nav_chef:
                        fragment = new ChefFragment();
                        break;
                    case R.id.nav_cart:
                        fragment = new CartFragment();
                        break;
                    case R.id.nav_more:
                        fragment = new MoreFragment();
                        break;
                    case R.id.nav_profile:
                        fragment = new ProfileFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
                return true;
            }
        });

        // Check for internet connectivity
        if (!isInternetConnected()) {
            showNoInternetConnectionDialog();
        }
    }

    // Define a static method to create an Intent for starting MainActivity
    public static Intent createIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
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
                .setPositiveButton("Exit App", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("Try Again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // You can add any retry logic here if needed
                        dialog.dismiss();
                    }
                })
                .show();
    }

    // Check if the user is logged in
    private boolean isUserLoggedIn() {
        // Replace this with your own logic to check if the user is logged in
        // For example, you can use SharedPreferences or Firebase Authentication
        // Return true if the user is logged in, otherwise return false
        // Example code for checking in SharedPreferences:
        SharedPreferences sharedPreferences = getSharedPreferences("AamaKo Haat", MODE_PRIVATE);
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }
}
