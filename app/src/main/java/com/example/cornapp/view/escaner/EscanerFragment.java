package com.example.cornapp.view.escaner;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.budiyev.android.codescanner.CodeScannerView;
import com.example.cornapp.R;
import com.example.cornapp.databinding.EscanerFragmentBinding;
import com.budiyev.android.codescanner.CodeScanner;
import com.example.cornapp.utils.Utils;
import com.example.cornapp.utils.UtilsHTTP;

import org.json.JSONException;
import org.json.JSONObject;

public class EscanerFragment extends Fragment {
    private EscanerFragmentBinding binding;
    private CodeScanner mCodeScanner;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = EscanerFragmentBinding.inflate(inflater, container, false);
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.CAMERA }, 1888);
        }

        CodeScannerView scannerView = binding.scannerView;
        mCodeScanner = new CodeScanner(getActivity(), scannerView);
        mCodeScanner.setDecodeCallback(result -> {
            Utils.toast(getActivity(),result.getText());
            try {
                JSONObject obj = new JSONObject("{}");
                obj.put("user_id", "623045380");
                obj.put("transaction_token",result.getText());
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
            TextView amount = getActivity().findViewById(R.id.cantidad);
            amount.setText(objResponse.getString("amount"));
            onButtonShowPopupWindowClick(getView());
        }
    }

    public void onButtonShowPopupWindowClick(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_payment, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener((v, event) -> {
            popupWindow.dismiss();
            return true;
        });
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
