package com.food.aamakohaat.customer_activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.food.aamakohaat.R;
import com.food.aamakohaat.internet.InternetUtils;
import com.food.aamakohaat.internet.internet_dialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private TextView textView;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.setStatusBarColor(Color.TRANSPARENT);
        }



        // Initialize the ImageButton
        ImageButton imageButton = view.findViewById(R.id.imageButton);

        // Set an OnClickListener for the ImageButton
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the InboxActivity when the ImageButton is clicked
                Intent intent = new Intent(requireContext(), InboxActivity.class);
                startActivity(intent);
            }
        });

        // Initialize the TextView
        textView = view.findViewById(R.id.textView);

        // Check for internet connectivity when the fragment starts
        if (!InternetUtils.isInternetConnected(requireContext())) {
            showNoInternetConnectionDialog();
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check for location permission and request updates when the fragment starts
        if (hasLocationPermission()) {
            requestLocationUpdates();
        } else {
            requestLocationPermission();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // Stop location updates when the fragment is stopped
        stopLocationUpdates();
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void requestLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000); // Update interval in milliseconds (e.g., every 10 seconds)
        locationRequest.setFastestInterval(5000); // Fastest update interval
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult.getLastLocation() != null) {
                    Location location = locationResult.getLastLocation();
                    if (location.getAccuracy() < 50.0) { // Filter out less accurate updates
                        // Update the TextView with location data
                        updateLocationText(location.getLatitude(), location.getLongitude());
                        stopLocationUpdates(); // Stop updates after a good fix
                    }
                }
            }
        };

        /*

                                                Here is the correction of the code below
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                     }
                     fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
         */

        // Check for location permissions again before requesting updates
        if (hasLocationPermission()) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void stopLocationUpdates() {
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    private void updateLocationText(double latitude, double longitude) {
        if (isAdded()) { // Check if the Fragment is attached to an Activity
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());

            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (!addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    String street = address.getThoroughfare(); // Get the street name
                    String city = address.getLocality(); // Get the city name
                    String postalCode = address.getPostalCode(); // Get the postal code
                    String district = address.getSubAdminArea(); // Get the district
                    String country = address.getCountryName(); // Get the country name

                    // Create a custom location format
                    StringBuilder locationTextBuilder = new StringBuilder();

                    if (street != null) {
                        locationTextBuilder.append(street).append(", ");
                    }

                    if (city != null) {
                        locationTextBuilder.append(city).append(" ");
                    }

                    if (postalCode != null) {
                        locationTextBuilder.append(postalCode).append(", ");
                    }

                    if (district != null) {
                        locationTextBuilder.append(district).append(", ");
                    }

                    if (country != null) {
                        locationTextBuilder.append(country);
                    }

                    // Update the TextView with the custom location format
                    textView.setText(locationTextBuilder.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showNoInternetConnectionDialog() {
        // Use your custom NoInternetDialog class to display the dialog
        internet_dialog.show(requireContext(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the "Try Again" action here
                onStart(); // Restart the fragment or retry the operation
            }
        });
    }
}
