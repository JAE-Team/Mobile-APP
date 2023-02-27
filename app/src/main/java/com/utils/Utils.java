package com.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class Utils {
    public static final String apiUrl = "https://server-production-e914.up.railway.app:443";
    public static void toast(Activity activity, String message){
        activity.runOnUiThread(() -> Toast.makeText(activity, message, Toast.LENGTH_SHORT).show());
    }
}
