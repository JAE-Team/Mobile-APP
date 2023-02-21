package com.example.cornapp.view.cobrament;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.cornapp.R;
import com.example.cornapp.databinding.CobramentFragmentBinding;
import com.example.cornapp.utils.Utils;
import com.example.cornapp.utils.UtilsHTTP;
import com.example.cornapp.view.perfil.PerfilFragment;
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
                Utils.toast(getActivity(),"El campo de cantidad no puede estar vacía");
            } else {
                try {
                    JSONObject obj = new JSONObject("{}");
                    obj.put("user_id", "623045380");
                    obj.put("amount",binding.cantidadCobro.getText().toString());
                    UtilsHTTP.sendPOST("http://10.0.2.2:3001/api/setup_payment", obj.toString(), (response) -> {
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
            Utils.toast(getActivity(), objResponse.getString("message"));
            Log.d("TOKEN",token);
        }
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