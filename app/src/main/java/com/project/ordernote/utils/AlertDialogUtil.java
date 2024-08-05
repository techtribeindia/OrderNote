package com.project.ordernote.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.project.ordernote.R;

public class AlertDialogUtil {



    public static void showCustomDialog(Context context, String title, String message, String positiveButtonText , String negativeButtonText,String positiveButtonColor,
                                                     DialogInterface.OnClickListener positiveClickListener,
                                                     DialogInterface.OnClickListener negativeClickListener ) {
        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.alertdialog_custom, null);

        customView.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Removes default background

        // Set up the dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(customView);

        // Get the dialog elements
        TextView dialogTitle = customView.findViewById(R.id.dialogTitle);
        TextView dialogMessage = customView.findViewById(R.id.dialogMessage);
        TextView dialogPositiveRedButton = customView.findViewById(R.id.dialogPositiveRedButton);
        TextView dialogNegativeButton = customView.findViewById(R.id.dialogNegativeButton);
        TextView dialogPositiveBlackButton = customView.findViewById(R.id.dialogPositiveBlackButton);
        dialogPositiveRedButton.setText(positiveButtonText);
        dialogPositiveBlackButton.setText(positiveButtonText);
        dialogNegativeButton.setText(negativeButtonText);
        // Set the dialog elements' text
        dialogTitle.setText(title);
        dialogMessage.setText(message);


        if(positiveButtonColor == "BLACK"){
            dialogPositiveBlackButton.setVisibility(View.VISIBLE);
            dialogPositiveRedButton.setVisibility(View.GONE);
            dialogTitle.setTextColor(Color.BLACK);
        }
        else if(positiveButtonColor == "RED"){
            dialogPositiveBlackButton.setVisibility(View.GONE);
            dialogPositiveRedButton.setVisibility(View.VISIBLE);

        }


        // Create the dialog
        AlertDialog dialog = builder.create();

        // Set button click listeners
        dialogPositiveRedButton.setOnClickListener(v -> {
            if (positiveClickListener != null) {
                positiveClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
            }
            dialog.dismiss();
        });


        // Set button click listeners
        dialogPositiveBlackButton.setOnClickListener(v -> {
            if (positiveClickListener != null) {
                positiveClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
            }
            dialog.dismiss();
        });

        dialogNegativeButton.setOnClickListener(v -> {
            if (negativeClickListener != null) {
                negativeClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
            }
            dialog.dismiss();
        });

        // Show the dialog
        dialog.show();
    }

}
