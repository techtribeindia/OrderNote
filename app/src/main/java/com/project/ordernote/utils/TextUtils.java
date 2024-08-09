package com.project.ordernote.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class TextUtils {


    public static String getInitials(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }

        String[] words = name.trim().split("\\s+");
        StringBuilder initials = new StringBuilder();

        if (words.length > 0) {
            // Always take the first letter of the first word
            initials.append(Character.toUpperCase(words[0].charAt(0)));

            // Check if there is more than one word
            if (words.length > 1) {
                // Always take the first letter of the last word
                initials.append(Character.toUpperCase(words[words.length - 1].charAt(0)));
            }
        }

        return initials.toString();
    }




    public static String capitalizeWords(String input) {
        StringBuilder capitalized = new StringBuilder();
        boolean capitalizeNext = true;

        for (char c : input.toCharArray()) {
            if (Character.isWhitespace(c)) {
                capitalizeNext = true;
                capitalized.append(c);
            } else if (capitalizeNext) {
                capitalized.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                capitalized.append(Character.toLowerCase(c));
            }
        }

        return capitalized.toString();
    }



    public static void capitalizeFirstLetterFromEditText(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            boolean isUpdating = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isUpdating) {
                    isUpdating = true;
                    String input = s.toString();
                    String capitalizedText = capitalizeWords(input);
                    editText.setText(capitalizedText);
                    editText.setSelection(editText.getText().length()); // Set cursor to the end
                    isUpdating = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed here
            }


        });
    }
}