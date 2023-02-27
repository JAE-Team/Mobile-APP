package com.view.escaner;


import static com.view.perfil.PerfilFragment.user;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.MainActivity;
import com.budiyev.android.codescanner.CodeScannerView;
import com.cornApp.databinding.EscanerFragmentBinding;
import com.budiyev.android.codescanner.CodeScanner;
import com.utils.Utils;
import com.utils.UtilsHTTP;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class EscanerFragment extends Fragment {
    private EscanerFragmentBinding binding;
    private CodeScanner mCodeScanner;

    private String token;

    @SuppressLint("WrongThread")
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
                onPause();
                setToken(result.getText());
                JSONObject obj = new JSONObject("{}");
                obj.put("user_id", "623045381");
                obj.put("transaction_token",getToken());

                UtilsHTTP.sendPOST(Utils.apiUrl + "/api/start_payment", obj.toString(), (response) -> {
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
            Double amount = objResponse.getDouble("amount");
            popup(amount);
        }
    }
    public void popup(Double amount) {
        getActivity().runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Cobrament");
            builder.setMessage("Cantidad a pagar: " +  amount + " CORN");

            builder.setPositiveButton("Aceptar", (dialog, which) -> {
                finishPayment(true, amount);
            });
            builder.setNegativeButton("Rebutjar", (dialog, which) -> {
                finishPayment(false, amount);
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    public void popupMessage(String message) {
        getActivity().runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Transacció");
            builder.setMessage(message + "!!");

            AlertDialog dialog = builder.create();
            dialog.show();

            // Hide after some seconds
            final Handler handler  = new Handler();
            final Runnable runnable = () -> {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            };

            dialog.setOnDismissListener(dialog1 -> handler.removeCallbacks(runnable));

            handler.postDelayed(runnable, 3000);
        });
    }

    public void finishPayment(boolean accepted, double amount){
        try {
            JSONObject obj = new JSONObject("{}");
            obj.put("user_id", user.getUserId());
            obj.put("transaction_token", getToken());
            obj.put("accept", accepted);
            obj.put("amount", amount);

            UtilsHTTP.sendPOST(Utils.apiUrl + "/api/finish_payment", obj.toString(), (response) -> {
                try {
                    JSONObject objResponse = new JSONObject(response);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        try {
                            onResume();
                            popupMessage(objResponse.getString("message"));
                        } catch (JSONException e) {
                            // throw new RuntimeException(e);
                        }
                    });
                } catch (JSONException e) {
                    // throw new RuntimeException(e);
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
