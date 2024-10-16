package com.project.ordernote.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.project.ordernote.data.local.LocalDataManager;
import com.project.ordernote.data.model.Users_Model;

public class SessionManager {
    private String PREF_NAME = "UserSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_NAME = DatabaseReference.name_UserDetails;
    private static final String KEY_MOBILE_NUMBER = DatabaseReference.mobileno_UserDetails;
    private static final String KEY_ROLE = DatabaseReference.role_UserDetails;

    private static final String VENDORKEY = DatabaseReference.vendorkey;
    private static final String VENDORNAME = DatabaseReference.vendorname;



    // Add other keys as needed

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context, String pref_Name) {
        this.context = context;
        this.PREF_NAME = pref_Name;
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
    public String getUserName()
    {
        return pref.getString(KEY_NAME,"");
    }
    public String getUserMobileNumber()
    {
        return pref.getString(KEY_MOBILE_NUMBER,"");
    }
    public String getRole()
    {
        return  pref.getString(KEY_ROLE,"");
    }
    public String getVendorkey()
    {
        return pref.getString(VENDORKEY,"");
    }
    public String getVendorname()
    {
        return pref.getString(VENDORNAME,"");
    }

    public void saveUserData(QueryDocumentSnapshot userDocument) {
        Log.d("SessionManager","AppData updated");
        editor.putString(KEY_NAME, userDocument.getString(KEY_NAME));
        editor.putString(KEY_MOBILE_NUMBER, userDocument.getString(KEY_MOBILE_NUMBER));
        editor.putString(KEY_ROLE, userDocument.getString(KEY_ROLE));

        editor.putString(VENDORKEY, userDocument.getString(VENDORKEY));
        editor.putString(VENDORNAME, userDocument.getString(VENDORNAME));

        // Add other fields as needed
        editor.apply();
    }
    public void logout() {
        editor.clear(); // Clears all session data
        editor.apply();
         new LocalDataManager();


    }

    public void saveUserDataUsingModel(Users_Model usersModel) {
        editor.putString(KEY_NAME, usersModel.getName());
        editor.putString(KEY_MOBILE_NUMBER, usersModel.getMobileno());
        editor.putString(KEY_ROLE, usersModel.getRole());
        editor.putString(VENDORKEY, usersModel.getVendorkey());
        editor.putString(VENDORNAME, usersModel.getVendorname());

        // Add other fields as needed
        editor.apply();


    }
}
