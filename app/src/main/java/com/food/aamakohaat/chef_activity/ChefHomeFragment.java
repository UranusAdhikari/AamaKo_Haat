package com.food.aamakohaat.chef_activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.food.aamakohaat.MainActivity;
import com.food.aamakohaat.R;
import com.food.aamakohaat.internet.InternetUtils;
import com.food.aamakohaat.internet.internet_dialog;

public class ChefHomeFragment extends Fragment {
    private TextView textView;
    private Button openNewActivityButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chef_home, container, false);

        // Initialize views
        textView = view.findViewById(R.id.textView);
        openNewActivityButton = view.findViewById(R.id.openProfileButton);



        // Set click listener for the button to open a new activity
        openNewActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the new activity here
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);

                // Apply the animation
                getActivity().overridePendingTransition(R.anim.chef_switch, R.anim.chef_switch);
            }
        });

        // Check for internet connectivity when the fragment starts
        if (!InternetUtils.isInternetConnected(requireContext())) {
            showNoInternetConnectionDialog();
        }

        return view;
    }

    private void showNoInternetConnectionDialog() {
        // Use your custom NoInternetDialog class to display the dialog
        internet_dialog.show(requireContext(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the "Try Again" action here
                // You can add code to retry any specific operation or refresh the fragment
            }
        });
    }
}
