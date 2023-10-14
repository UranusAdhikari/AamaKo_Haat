package com.food.aamakohaat.customer_activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.food.aamakohaat.R;
import com.food.aamakohaat.internet.InternetUtils;
import com.food.aamakohaat.internet.internet_dialog;

public class CartFragment extends Fragment {
    private TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.setStatusBarColor(Color.TRANSPARENT);
        }


        // Initialize the TextView
        textView = view.findViewById(R.id.textView);

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
                onStart(); // You can add code to retry any specific operation or refresh the fragment
            }
        });
    }
}
