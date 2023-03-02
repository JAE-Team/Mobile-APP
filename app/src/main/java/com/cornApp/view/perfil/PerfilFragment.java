package com.cornApp.view.perfil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.cornApp.MainActivity;
import com.cornApp.databinding.PerfilFragmentBinding;
import com.cornApp.utils.Utils;
import com.cornApp.utils.UtilsHTTP;

import org.json.JSONException;
import org.json.JSONObject;

public class PerfilFragment extends Fragment {
    private PerfilFragmentBinding binding;

    public static Usuari user = new Usuari();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = PerfilFragmentBinding.inflate(inflater, container, false);
        binding.logout.setOnClickListener(v -> {
            try {
                JSONObject obj = new JSONObject("{}");
                obj.put("session_token", MainActivity.getSessionToken());

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
                Utils.toast(getActivity(),msg);

            } else if(msg.equals("Logout failed, session token not found")){
                Utils.toast(getActivity(),msg);
            }

        }
    }
}