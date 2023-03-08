package com.cornApp.view.historial;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.cornApp.LoginActivity;
import com.cornApp.R;
import com.cornApp.databinding.HistorialFragmentBinding;
import com.cornApp.utils.MyPagerAdapter;
import com.cornApp.utils.Utils;
import com.cornApp.utils.UtilsHTTP;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class HistorialFragment extends Fragment {

    private HistorialFragmentBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = HistorialFragmentBinding.inflate(inflater, container, false);

        checkDB();

        SharedPreferences sharedUser = getActivity().getSharedPreferences("sessionUser",Context.MODE_PRIVATE);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("sessionToken", Context.MODE_PRIVATE);

        try {
            JSONObject obj = new JSONObject("{}");


            obj.put("session_token", sharedPref.getString("sessionToken",""));
            obj.put("userId", sharedUser.getString("phone",""));

            UtilsHTTP.sendPOST(Utils.apiUrl + "/api/get_transactions", obj.toString(), this::chargeTransactions);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    public void checkDB() {
        try {
            JSONObject obj = new JSONObject("{}");
            SharedPreferences sharedPref = getActivity().getSharedPreferences("sessionUser", Context.MODE_PRIVATE);
            String sToken = sharedPref.getString("sessionToken","");
            obj.put("sessionToken", sToken);

            UtilsHTTP.sendPOST(Utils.apiUrl + "/api/get_profile", obj.toString(), (response) -> {
                getActivity().runOnUiThread(() -> {
                    try {
                        JSONObject objResponse = new JSONObject(response);

                        if (objResponse.getString("status").equals("OK")) {
                            JSONArray dataServer = objResponse.getJSONArray("message");
                            JSONObject userData = dataServer.getJSONObject(0);
                            binding.userBalance.setText(userData.getString("userBalance") + "€");
                            sharedPref.edit().putString("userBalance",userData.getString("userBalance")).apply();
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    public void chargeTransactions(String response){
        try {
            JSONObject objResponse = new JSONObject(response);
            Object message = objResponse.get("message");

            if (message instanceof String) {
                Utils.toast(getActivity(), "No se ha podido obtener del servidor la información de las transferencias de ");
            } else if (message instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) message;
                if(jsonArray.length() ==0){
                    Utils.toast(getActivity(),"No se han encontrado transferencias asociadas a este usuario en la base de datos");
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String userDestiny = jsonObject.getString("userDestiny");
                        String userOrigin = jsonObject.getString("userOrigin");
                        double ammount = jsonObject.getDouble("ammount");
                        Date timeSetup = null;
                        if (!jsonObject.isNull("timeSetup")) {
                            timeSetup = new Date(jsonObject.getLong("timeSetup"));
                        }
                        Date timeStart = new Date(jsonObject.getLong("timeStart"));
                        Date timeFinish = new Date(jsonObject.getLong("timeFinish"));

                    }
                }

            }
        } catch (JSONException e) {
            // throw new RuntimeException(e);
        }
    }

}