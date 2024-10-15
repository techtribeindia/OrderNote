package com.project.ordernote.utils;

import android.text.InputFilter;
import android.text.Spanned;


public class DecimalInputFilter implements InputFilter {
    private int decimalDigits;

    public DecimalInputFilter(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        // Prevent more than one dot in the input
        if (String.valueOf(source).equals(".") && String.valueOf(dest).toString().contains(".")) {
            return "";
        }
        if (dest.toString().contains(".")) {
            int totalLengthOfString = dest.toString().length();
            int indexOfDecimal = dest.toString().indexOf('.') + 1 ;
            int noOfDecimals = totalLengthOfString - indexOfDecimal ;

            if(dstart<indexOfDecimal){
                return  null;

            }
            if( noOfDecimals < decimalDigits){
               return  null;
            }
            else{
                return "";

            }

        }


        // Check if input exceeds the allowed decimal digits
        if (dest.toString().contains(".")) {
            int index = dest.toString().indexOf(".");
            int decimalCount = dend - index;

            if (decimalCount > decimalDigits) {
                return "";
            }
        }


        // Allow input
        return null;
    }
}



/*public class DecimalInputFilter implements InputFilter {

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        // Prevent more than one dot in the input
        if (source.equals(".") && dest.toString().contains(".")) {
            return "";
        }
        return null;
    }
}


 */
