package com.cornApp.view.cobrament;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.cornApp.R;
import com.cornApp.databinding.CobramentFragmentBinding;
import com.cornApp.utils.Utils;
import com.cornApp.utils.UtilsHTTP;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONException;
import org.json.JSONObject;

public class CobramentFragment extends Fragment {
    private CobramentFragmentBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = CobramentFragmentBinding.inflate(inflater, container, false);

        binding.setupCobro.setOnClickListener(v -> {
            if(binding.cantidadCobro.getText().toString().isEmpty()){
                popupMessage("Transaction","The quantity field cannot be empty");
            } else {
                try {
                    SharedPreferences sharedPref = getActivity().getSharedPreferences("sessionUser", Context.MODE_PRIVATE);

                    JSONObject obj = new JSONObject("{}");
                    obj.put("amount",binding.cantidadCobro.getText().toString());
                    obj.put("user_id",sharedPref.getString("phone", ""));

                    UtilsHTTP.sendPOST(Utils.apiUrl + "/api/setup_payment", obj.toString(), (response) -> {
                        try {
                            setupPayment(response);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return binding.getRoot();
    }

    public void setupPayment(String response) throws JSONException {
        JSONObject objResponse = new JSONObject(response);

        if (objResponse.getString("status").equals("OK")) {
            String token = objResponse.getString("transaction_token");
            generateQR(token);
            popupMessage("Transaction",objResponse.getString("message"));
        }
    }

    public void popupMessage(String title, String message) {
        getActivity().runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(title);
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

    private void generateQR(String content) {
        getActivity().runOnUiThread(() -> {

            QRCodeWriter writer = new QRCodeWriter();
            try {
                BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512);
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        bitmap.setPixel(x, y, bitMatrix.get(x, y) ? getResources().getColor(R.color.black) : getResources().getColor(R.color.white));
                    }
                }
                binding.qrImage.setImageBitmap(bitmap);
                binding.textBefore.setVisibility(View.GONE);
            } catch (WriterException e) {
                e.printStackTrace();
            }

        });
    }
}