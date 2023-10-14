package com.food.aamakohaat.customer_activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.food.aamakohaat.R;

public class CardDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_details_layout);


        ImageButton backButton = findViewById(R.id.imageView2);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate back to the previous fragment (HomeFragment)
                onBackPressed();
            }
        });

        // Retrieve the card type passed from the MoreFragment
        String cardType = getIntent().getStringExtra("cardType");

        // Initialize UI elements
        TextView cardTop = findViewById(R.id.cardDetailsTopTextView);
        ImageView cardImage = findViewById(R.id.cardImage);
        TextView cardDescription = findViewById(R.id.cardDetailsDescriptionTextView);
        TextView cardDescription1 = findViewById(R.id.cardDetailsDescriptionTextView1);// Use the correct ID

        // Update content based on the cardType
        if (cardType != null) {
            if (cardType.equals("about")) {
                cardTop.setText("About Us");
                cardImage.setImageResource(R.drawable.img_about);
                // Set the text using the strings from strings.xml
                // Set the text using the strings from strings.xml
                cardDescription.setText(getString(R.string.app_description_part1));
                cardDescription1.setText(getString(R.string.app_description_part2));
            } else if (cardType.equals("terms")) {
                cardTop.setText("Terms and Condition");
                cardImage.setImageResource(R.drawable.img_terms);
                cardDescription.setText("These are the Terms and Conditions.");
            } else if (cardType.equals("privacy")) {
                cardTop.setText("Privacy Policy");
                cardImage.setImageResource(R.drawable.img_privacy);
                cardDescription.setText("This is the Privacy Policy card.");
            }
            } else {
                // Handle other card types or invalid input
            }
        }
    }
