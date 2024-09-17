package com.project.ordernote.utils;

public interface CheckPermissionForManageStorageInterface {

    void onPermissionRejected(String result , boolean isJustCheckForPermission);

    void onPermissionGranted(String result , boolean isJustCheckForPermission);

    void onPermissionRequired(String result , boolean isJustCheckForPermission);


}
