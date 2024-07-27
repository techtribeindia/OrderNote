package com.project.ordernote.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.firestore.QueryDocumentSnapshot;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_NAME = "name";
    private static final String KEY_MOBILE_NUMBER = "mobileno";
    private static final String KEY_ROLE = "role";
    // Add other keys as needed

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void saveUserData(QueryDocumentSnapshot userDocument) {
        editor.putString(KEY_NAME, userDocument.getString(KEY_NAME));
        editor.putString(KEY_MOBILE_NUMBER, userDocument.getString(KEY_MOBILE_NUMBER));
        editor.putString(KEY_ROLE, userDocument.getString(KEY_ROLE));
        // Add other fields as needed
        editor.apply();
    }
}
