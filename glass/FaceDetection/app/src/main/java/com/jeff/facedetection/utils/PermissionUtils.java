package com.jeff.facedetection.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import com.jeff.facedetection.R;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by yanjiatian on 2017/10/31.
 */

public class PermissionUtils {
    private static final String[] LOCATION_AND_CONTACTS =
            {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS};
    private static final String[] ALL_PERMISSIONS =
            {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    public static final int RC_ALL_PERMISSIONS = 123;

    public static void checkPermissions(Activity context) {
        if (hasAllPermission(context)) {

        } else {
            EasyPermissions.requestPermissions(
                    context,
                    context.getResources().getString(R.string.rationale_storage),
                    RC_ALL_PERMISSIONS,
                    ALL_PERMISSIONS);
        }
    }

    private static boolean hasAllPermission(Context context) {
        return EasyPermissions.hasPermissions(context, ALL_PERMISSIONS);
    }

}
