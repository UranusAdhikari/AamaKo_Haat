package com.food.aamakohaat.customer_activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.food.aamakohaat.R;
import com.food.aamakohaat.chef_activity.ChefMainActivity;
import com.food.aamakohaat.internet.InternetUtils;
import com.food.aamakohaat.internet.internet_dialog;
import com.food.aamakohaat.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private TextView nameTextView;
    private TextView emailTextView;
    private TextView phoneTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.prof));
        }

        nameTextView = view.findViewById(R.id.name);
        emailTextView = view.findViewById(R.id.email);
        phoneTextView = view.findViewById(R.id.phone);

        Button signOutButton = view.findViewById(R.id.sign_out_button);
        signOutButton.setBackgroundColor(requireContext().getResources().getColor(R.color.red));

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        Button openNewActivityButton = view.findViewById(R.id.openNewActivityButton);
        openNewActivityButton.setBackgroundColor(requireContext().getResources().getColor(R.color.red));

        openNewActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChefMainActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.chef_switch, R.anim.chef_switch);
            }
        });

        if (!InternetUtils.isInternetConnected(requireContext())) {
            showNoInternetConnectionDialog();
        }

        // Retrieve and display user data from Firebase
        retrieveUserData();

        return view;
    }

    private void retrieveUserData() {
        // Get the current user
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            String email = auth.getCurrentUser().getEmail();
            if (email != null) {
                emailTextView.setText(email);

                // Create a reference to the Firebase Realtime Database for the "Users" node
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

                // Replace any periods in the email address with commas to create the database reference
                String sanitizedEmail = email.replace(".", ",");
                DatabaseReference userReference = databaseReference.child(sanitizedEmail);

                userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Retrieve user data and update the TextViews
                            String fname = dataSnapshot.child("Fname").getValue(String.class);
                            String lname = dataSnapshot.child("Lname").getValue(String.class);
                            String phone = dataSnapshot.child("Mobile").getValue(String.class);

                            // Concatenate first name and last name to form a full name
                            String fullName = fname + " " + lname;

                            // Update the TextViews
                            nameTextView.setText(fullName);
                            phoneTextView.setText(phone);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle any database error here if needed
                    }
                });
            }
        }
    }

    private void showNoInternetConnectionDialog() {
        internet_dialog.show(requireContext(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onStart();
            }
        });
    }
}
