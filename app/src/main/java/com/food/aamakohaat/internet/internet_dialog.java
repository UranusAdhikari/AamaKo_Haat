package com.food.aamakohaat.internet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.food.aamakohaat.R;

public class internet_dialog {

    public static void show(Context context, DialogInterface.OnClickListener tryAgainClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.internet_dialog, null);
        builder.setView(dialogView);

        // Find views in the custom dialog layout
        //TextView titleTextView = dialogView.findViewById(R.id.dialog_title);
       // TextView messageTextView = dialogView.findViewById(R.id.dialog_message);

        Button tryAgainButton = dialogView.findViewById(R.id.try_again_button);

        // Customize the text and appearance of the dialog
       // titleTextView.setText("Custom Title");
        //messageTextView.setText("Custom Message");

        // Declare the dialog variable at a higher scope
        AlertDialog dialog = builder.create();

        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for internet connectivity
                if (InternetUtils.isInternetConnected(context)) {
                    // If connected, dismiss the dialog
                    dialog.dismiss();
                } else {
                    // If not connected, trigger the try again action
                    tryAgainClickListener.onClick(null, DialogInterface.BUTTON_POSITIVE);
                }
            }
        });

        builder.setPositiveButton("Try Again", tryAgainClickListener);

        // Show the dialog
        dialog.show();
    }
}
