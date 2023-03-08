package com.cornApp.view.escaner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
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

import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.CodeScanner;
import com.cornApp.databinding.EscanerFragmentBinding;
import com.cornApp.utils.Utils;
import com.cornApp.utils.UtilsHTTP;

import org.json.JSONException;
import org.json.JSONObject;

public class EscanerFragment extends Fragment {
    private EscanerFragmentBinding binding;
    private CodeScanner mCodeScanner;

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
                if(result.getText().contains("P")){
                    // Guardar el token de cobrament
                    SharedPreferences sharedPayments = getActivity().getSharedPreferences("payments",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPayments.edit();
                    editor.putString("paymentToken", result.getText());
                    editor.commit();

                    // Obtenir el id del user
                    SharedPreferences sharedUser = getActivity().getSharedPreferences("sessionUser",Context.MODE_PRIVATE);

                    JSONObject obj = new JSONObject("{}");
                    obj.put("user_id", sharedUser.getString("phone", ""));
                    obj.put("transaction_token",sharedPayments.getString("paymentToken",""));

                    UtilsHTTP.sendPOST(Utils.apiUrl + "/api/start_payment", obj.toString(), (response) -> {
                        try {
                            startPayment(response);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
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
        } else if (objResponse.getString("status").equals("Error")){
            popupMessage(objResponse.getString("message"));
            onResume();
        }
    }
    public void popup(Double amount) {
        getActivity().runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Cobrament");
            builder.setMessage("Quantitat a pagar: " +  amount + " CORN");

            builder.setPositiveButton("Acceptar", (dialog, which) -> {
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
            SharedPreferences sharedPayments = getActivity().getSharedPreferences("payments",Context.MODE_PRIVATE);
            SharedPreferences sharedUser = getActivity().getSharedPreferences("sessionUser",Context.MODE_PRIVATE);

            obj.put("user_id", sharedUser.getString("phone",""));
            obj.put("transaction_token", sharedPayments.getString("paymentToken",""));
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
