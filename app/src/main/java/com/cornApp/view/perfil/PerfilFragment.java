package com.cornApp.view.perfil;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.cornApp.LoginActivity;
import com.cornApp.R;
import com.cornApp.databinding.PerfilFragmentBinding;
import com.cornApp.utils.Utils;
import com.cornApp.utils.UtilsHTTP;

import org.json.JSONException;
import org.json.JSONObject;

public class PerfilFragment extends Fragment {
    private PerfilFragmentBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = PerfilFragmentBinding.inflate(inflater, container, false);

        getProfileInfo();



        binding.logout.setOnClickListener(v -> {
            try {
                JSONObject obj = new JSONObject("{}");

                SharedPreferences sharedPref = getActivity().getSharedPreferences("sessionToken",Context.MODE_PRIVATE);
                SharedPreferences sharedUser = getActivity().getSharedPreferences("sessionUser",Context.MODE_PRIVATE);

                obj.put("session_token", sharedPref.getString("sessionToken",""));
                obj.put("userId", sharedUser.getString("phone",""));

                UtilsHTTP.sendPOST(Utils.apiUrl + "/api/logout", obj.toString(), (response) -> {
                    try {
                        logout(response);
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

    public void logout(String response) throws JSONException {

        JSONObject objResponse = new JSONObject(response);

        if (objResponse.getString("status").equals("OK")) {
            String msg = objResponse.getString("message");

            if(msg.equalsIgnoreCase("Logout correct")){
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        }
    }

    public void getProfileInfo(){
        SharedPreferences sharedPref = getActivity().getSharedPreferences("sessionUser",Context.MODE_PRIVATE);

        String strName = sharedPref.getString("name", "");
        String strSurname = sharedPref.getString("surname", "");
        String strPhone = sharedPref.getString("phone", "");
        String strEmail = sharedPref.getString("email", "");
        String strStatus = sharedPref.getString("status", "");

        switch (strStatus){
            case "NOT_VERIFIED":
                popupMessage("Per verificar l'usuari polsi el botó que está situat al l'esquina superior dreta.");
                strStatus = "No verificat";
                binding.status.setCardBackgroundColor(ResourcesCompat.getColor(getResources(),R.color.teal_200,null));
                break;

            case "WAITING_VERIFICATION":
                strStatus = "Esperant verificació...";
                binding.status.setCardBackgroundColor(ResourcesCompat.getColor(getResources(),R.color.alert_orange,null));
                break;

            case "ACCEPTED":
                strStatus = "Acceptat";
                binding.status.setCardBackgroundColor(ResourcesCompat.getColor(getResources(),R.color.alert_green,null));
                break;

            case "REJECTED":
                strStatus = "Rebutjat";
                binding.status.setCardBackgroundColor(ResourcesCompat.getColor(getResources(),R.color.alert_red,null));
                break;

            default:
                strStatus = "NOT_VERIFIED";
                binding.status.setCardBackgroundColor(ResourcesCompat.getColor(getResources(),R.color.teal_200,null));
                break;
        }

        binding.nom.setText(strName);
        binding.cognoms.setText(strSurname);
        binding.telefon.setText(strPhone);
        binding.email.setText(strEmail);
        binding.statusText.setText(strStatus);
    }

    public void popupMessage(String message) {
        getActivity().runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Perfil d'usuari");
            builder.setMessage(message + "!!");
            builder.setNeutralButton("OK", null);

            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

}