package com.food.aamakohaat.customer_activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.food.aamakohaat.R;
import com.food.aamakohaat.internet.InternetUtils;
import com.food.aamakohaat.internet.internet_dialog;

public class MoreFragment extends Fragment {

    private TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.setStatusBarColor(Color.TRANSPARENT);
        }


        ImageButton imageButton1 = view.findViewById(R.id.imageButton1);
        ImageButton imageButton2 = view.findViewById(R.id.imageButton2);
        ImageButton imageButton3 = view.findViewById(R.id.imageButton3);
        ImageButton imageButton4 = view.findViewById(R.id.imageButton4);
        ImageButton imageButton5 = view.findViewById(R.id.imageButton5);
        ImageButton imageButton6 = view.findViewById(R.id.imageButton6);

        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebsite("https://www.facebook.com");
            }
        });

        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebsite("https://www.instagram.com");
            }
        });

        imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebsite("https://www.twitter.com");
            }
        });

        imageButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebsite("https://www.linkedin.com");
            }
        });

        imageButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebsite("https://www.tiktok.com");
            }
        });

        imageButton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebsite("https://www.youtube.com");
            }

        });


        // Initialize CardViews
        CardView cardViewAbout = view.findViewById(R.id.cardView3);
        CardView cardViewTerms = view.findViewById(R.id.cardView4);
        CardView cardViewPrivacy = view.findViewById(R.id.cardView5);
        CardView cardViewRate = view.findViewById(R.id.cardView6);
        CardView cardViewShare = view.findViewById(R.id.cardView7);

        // Set OnClickListener for each CardView
        cardViewAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCardDetailsActivity("about");
            }
        });

        cardViewTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCardDetailsActivity("terms");
            }
        });

        cardViewPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCardDetailsActivity("privacy");
            }
        });

        cardViewRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCardDetailsActivity("rate");
            }
        });

        cardViewShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCardDetailsActivity("share");
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


    // Helper method to open a website in a web browser
    private void openWebsite(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        getActivity().startActivity(intent);
    }

    // Helper method to open the CardDetailsActivity with the given cardType

    private void openCardDetailsActivity(String cardType) {
        if (cardType.equals("rate")) {
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.notes.pu_notes_app");
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            try {
                startActivity(i);
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Unable to open\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else if (cardType.equals("share")) {
            // Handle the "Share App" card directly here
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Purbanchal University Notes");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.notes.pu_notes_app");
                startActivity(Intent.createChooser(shareIntent, "Share with"));
            } catch (Exception e) {
                // Handle any exceptions related to sharing here
                e.printStackTrace();
                // You can also show a toast message here if needed
                // Toast.makeText(requireContext(), "Unable to share this app", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            // For other card types, open the CardDetailsActivity as before
            Intent intent = new Intent(requireContext(), CardDetailsActivity.class);
            intent.putExtra("cardType", cardType);
            startActivity(intent);
        }
    }

}