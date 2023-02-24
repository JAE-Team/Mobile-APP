package com.view.escaner;


import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.budiyev.android.codescanner.CodeScannerView;
import com.cornApp.databinding.EscanerFragmentBinding;
import com.budiyev.android.codescanner.CodeScanner;
import com.utils.Utils;
import com.utils.UtilsHTTP;

import org.json.JSONException;
import org.json.JSONObject;

public class EscanerFragment extends Fragment {
    private EscanerFragmentBinding binding;
    private CodeScanner mCodeScanner;

    private String token;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = EscanerFragmentBinding.inflate(inflater, container, false);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.CAMERA }, 1888);
        }

        CodeScannerView scannerView = binding.scannerView;
        mCodeScanner = new CodeScanner(getActivity(), scannerView);
        mCodeScanner.setDecodeCallback(result -> {
            try {
                setToken(result.getText());
                JSONObject obj = new JSONObject("{}");
                obj.put("user_id", "623045380");
                obj.put("transaction_token",getToken());

                UtilsHTTP.sendPOST("http://10.0.2.2:3001/api/start_payment", obj.toString(), (response) -> {
                    try {
                        startPayment(response);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        });
        return binding.getRoot();
    }

    private void startPayment(String response) throws JSONException {
        JSONObject objResponse = new JSONObject(response);

        if (objResponse.getString("status").equals("OK")) {
            String amount = objResponse.getString("amount");
            popup(amount);
        }
    }
    public void popup(String amount) {
        getActivity().runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Cobrament");
            builder.setMessage("Cantidad a pagar: " +  amount + " CORN");

            builder.setPositiveButton("Aceptar", (dialog, which) -> {
                finishPayment(true, Integer.parseInt(amount));
            });
            builder.setNegativeButton("Rebutjar", (dialog, which) -> {
                finishPayment(false, Integer.parseInt(amount));
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });

    }

    public void finishPayment(boolean accepted, int amount){
        try {
            JSONObject obj = new JSONObject("{}");
            obj.put("user_id", "623045380");
            obj.put("transaction_token", getToken());
            obj.put("accept", accepted);
            obj.put("amount", amount);

            UtilsHTTP.sendPOST("http://10.0.2.2:3001/api/finish_payment", obj.toString(), (response) -> {
                try {
                    Utils.toast(getActivity(), new JSONObject(response).getString("message"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void setToken(String token){
        this.token = token;
    }

    public String getToken(){
        return token;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

}
