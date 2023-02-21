package com.example.cornapp.utils;

import android.app.Activity;
import android.widget.Toast;

public class Utils {
    public static void toast(Activity activity, String message){
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
