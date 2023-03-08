package com.cornApp.view.historial;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.cornApp.databinding.HistorialFragmentBinding;
import com.cornApp.utils.Utils;
import com.cornApp.utils.UtilsHTTP;

import org.json.JSONException;
import org.json.JSONObject;

public class HistorialFragment extends Fragment {

    private HistorialFragmentBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = HistorialFragmentBinding.inflate(inflater, container, false);

        try {
            JSONObject obj = new JSONObject("{}");

            SharedPreferences sharedPref = getActivity().getSharedPreferences("sessionToken", Context.MODE_PRIVATE);
            SharedPreferences sharedUser = getActivity().getSharedPreferences("sessionUser",Context.MODE_PRIVATE);

            obj.put("session_token", sharedPref.getString("sessionToken",""));
            obj.put("userId", sharedUser.getString("phone",""));

            UtilsHTTP.sendPOST(Utils.apiUrl + "/api/get_transactions", obj.toString(), (response) -> {

            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return binding.getRoot();
    }

}