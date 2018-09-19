package com.example.thesis.yummy.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hong Hanh on 4/9/2018.
 */

public class PermissionUtils {
    public static boolean checkPermission(Context context, String[] permission) {
        for (String per: permission) {
            if(ContextCompat.checkSelfPermission(context, per) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static void requestPermission(Activity activity, String[] permission, int requestCode) {
        List<String> notGrantedPermission = new ArrayList<>();
        for (String per : permission) {
            if(ContextCompat.checkSelfPermission(activity, per) != PackageManager.PERMISSION_GRANTED) {
                notGrantedPermission.add(per);
            }
        }
        if(notGrantedPermission.size() > 0) {
            ActivityCompat.requestPermissions(activity, notGrantedPermission.toArray(new String[notGrantedPermission.size()]), requestCode);
        }

    }
}
