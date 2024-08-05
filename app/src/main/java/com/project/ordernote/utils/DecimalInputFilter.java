package com.project.ordernote.utils;

import android.text.InputFilter;
import android.text.Spanned;

public class DecimalInputFilter implements InputFilter {

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        // Prevent more than one dot in the input
        if (source.equals(".") && dest.toString().contains(".")) {
            return "";
        }
        return null;
    }
}

