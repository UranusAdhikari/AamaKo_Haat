package com.food.aamakohaat.chef_activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.food.aamakohaat.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class ChefMainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_main);

        // For dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        // End

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new ChefHomeFragment()).commit();
        }

        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() != bottomNavigationView.getSelectedItemId()) {
                    Fragment fragment = null;
                    switch (item.getItemId()) {

                        case R.id.nav_home:
                            fragment = new ChefHomeFragment();
                            break;
                        case R.id.nav_chef:
                            fragment = new PendingOrderFragment();
                            break;
                        case R.id.nav_cart:
                            fragment = new ChefOrderFragment();
                            break;
                        case R.id.nav_profile:
                            fragment = new PostDishFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
                }
                return true;
            }
        });
    }
}
