package com.cornApp.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Handler;
import android.widget.Toast;

public class Utils {
    //public static final String apiUrl = "https://server-production-e914.up.railway.app:443";
    public static final String apiUrl = "http://10.0.2.2:3001";
    public static void toast(Activity activity, String message){
        activity.runOnUiThread(() -> Toast.makeText(activity, message, Toast.LENGTH_SHORT).show());
    }
}
