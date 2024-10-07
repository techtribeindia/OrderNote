package com.project.ordernote.utils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;
import android.window.SplashScreen;

import com.project.ordernote.R;
import com.project.ordernote.data.local.LocalDataManager;
import com.project.ordernote.ui.activity.SplashScreenActivity;


public class BaseActivity extends AppCompatActivity {
    private boolean activityfinished = false;
    private BaseUtil settingsUtil;
    public static boolean  isAdding_Or_UpdatingEntriesInDB_Service = false ;
    public static boolean  isDeviceIsMobilePhone = false ;
    public static BaseActivity baseActivity ;
    private boolean isDeviceOnLandscape = false;
    private double DeviceResolutionLevel = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        baseActivity = this;
        settingsUtil = new BaseUtil(this);


    }



    public boolean showNavigationBar(Resources resources)
    {
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        return id > 0 && resources.getBoolean(id);
    }





    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
      //  Log.i("isOnline ","splash screen netInfo "+ String.valueOf(netInfo));
        if (netInfo != null) {
       ///     Log.i("isOnline ","splash screen netInfo.isConnectedOrConnecting() : "+ String.valueOf(netInfo.isConnectedOrConnecting()));
       //     Log.i("isOnline ","splash screen netInfo.isConnected  : "+ String.valueOf(netInfo.isConnected()));
          //  Log.i("isOnline ","splash screen netInfo.getState() : "+ String.valueOf(netInfo.getState()));
          //  Log.i("isOnline ","splash screen netInfo.getDetailedState() : "+ String.valueOf(netInfo.getDetailedState()));
          //  Log.i("isOnline ","splash screen netInfo.getExtraInfo() : "+ String.valueOf(netInfo.getExtraInfo()));


        }

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    @Override
    protected void onPause() {
        super.onPause();
        recordLastAccessedTime();

    }

    public void recordLastAccessedTime() {
        if (settingsUtil == null) {
            settingsUtil = new BaseUtil(this);
        }
        if (!activityfinished) {
            settingsUtil.setLastAccessedTime(System.currentTimeMillis());
        } else {
            settingsUtil.setLastAccessedTime(0);
        }
    }

    @Override
    public void finish() {
        super.finish();
        activityfinished = true;
        overridePendingTransition(0, 0);
    }

    public void startHomeActivity() {
        isAppStartedManually = false;
        Log.i("Lala ","splash screen called in startHomeActivity");

        Intent intent = new Intent(BaseActivity.this, SplashScreenActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
       // startActivity(intent);
       // Toast.makeText(BaseActivity.this, "startHomeActivity", Toast.LENGTH_SHORT).show();

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0);

        finish();


    }

    private static final int APP_IDLE_TIME = 200000;//3.3 mins
    public static  boolean isAppStartedManually = false;
    public static  String  isAppTimedOut = "";
    @Override
    protected void onResume() {
        super.onResume();


        isAppTimedOut = calculateAppIdleTime();


    }

    public String calculateAppIdleTime() {
        String response = "";
        if (settingsUtil == null) {
            settingsUtil = new BaseUtil(this);
        }
        long lastAccessedTime = settingsUtil.getLastAccessedTime();
        long idletime = System.currentTimeMillis() - lastAccessedTime;
        long appidletime = APP_IDLE_TIME;


        if ((lastAccessedTime > 0) && (idletime > appidletime)) {
            //Log.d("BaseActivity", "cart and menu items cleared");
            // TMCMenuItemCatalog.getInstance().clearCartTMCMenuItems();
            //TMCMenuItemCatalog.getInstance().clear();

            startHomeActivity();
            return  "true";
        }
        else{
            return  "false";
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Toast.makeText(this, "Name " + Build.DEVICE, Toast.LENGTH_SHORT).show();
            } else {
                //not granted
            }
        }

    }



}