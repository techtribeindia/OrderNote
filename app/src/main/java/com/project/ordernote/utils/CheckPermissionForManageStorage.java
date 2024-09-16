package com.project.ordernote.utils;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

public class CheckPermissionForManageStorage {

    private static int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION = 1;

    public static void justCheckForPermission(Context context, FragmentActivity fragmentActivity, CheckPermissionForManageStorageInterface checkPermissionForManageStorageInterface) {


        if (SDK_INT < 30) {
            int writeExternalStoragePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (writeExternalStoragePermission == PackageManager.PERMISSION_GRANTED) {
                // Permission is already granted
                Log.d("PermissionCheck", "Write External Storage permission granted");
                checkPermissionForManageStorageInterface.onPermissionGranted("Permission Granted" ,true);
            } else {
                // Permission is not granted
                checkPermissionForManageStorageInterface.onPermissionRequired("Permission Not Granted" ,true);

                Log.d("PermissionCheck", "Write External Storage permission NOT granted");
            }
        }
        else{
            if (Environment.isExternalStorageManager()) {
                // Permission is already granted
                Log.d("PermissionCheck", "Manage External Storage permission granted");
                checkPermissionForManageStorageInterface.onPermissionGranted("Permission Granted" ,true);
            } else {
                // Permission is not granted
                Log.d("PermissionCheck", "Manage External Storage permission NOT granted");
                checkPermissionForManageStorageInterface.onPermissionRequired("Permission Not Granted" ,true);
            }
        }


    }
    public static void checkPermissionForManageStorageFromFragment(Context context, FragmentActivity fragmentActivity, CheckPermissionForManageStorageInterface checkPermissionForManageStorageInterface) {

        if (SDK_INT >= 30) {
            try {
                if (!Environment.isExternalStorageManager()) {
                    // Permission is not granted, request permission
                    checkPermissionForManageStorageInterface.onPermissionRequired("Manage storage permission required" , false);
                    Intent getPermissionIntent = new Intent();
                    getPermissionIntent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    context.startActivity(getPermissionIntent);
                } else {
                    // Permission is granted
                    checkPermissionForManageStorageInterface.onPermissionGranted(Constants.success, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (SDK_INT >= Build.VERSION_CODES.R) {
                    // Checking for Manage Storage permission in Android R (API 30+)
                    if (Environment.isExternalStorageManager()) {
                        // Permission is granted
                        checkPermissionForManageStorageInterface.onPermissionGranted(Constants.success, false);
                    } else {
                        // Permission is required
                        checkPermissionForManageStorageInterface.onPermissionRequired("Manage storage permission required", false);
                        try {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                            intent.addCategory("android.intent.category.DEFAULT");
                            intent.setData(Uri.parse(String.format("package:%s", context.getPackageName())));
                            fragmentActivity.startActivityForResult(intent, 2296);
                        } catch (Exception e) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                            fragmentActivity.startActivityForResult(intent, 2296);
                        }
                    }
                } else {
                    // For Android versions below R, use WRITE_EXTERNAL_STORAGE
                    int writeExternalStoragePermission = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE);

                    if (writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                        // Permission is required
                        checkPermissionForManageStorageInterface.onPermissionRequired("Write external storage permission required", false);

                        // Requesting permission
                        ActivityCompat.requestPermissions(fragmentActivity, new String[]{WRITE_EXTERNAL_STORAGE},
                                REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
                    } else {
                        // Permission is granted
                        checkPermissionForManageStorageInterface.onPermissionGranted(Constants.success, false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
