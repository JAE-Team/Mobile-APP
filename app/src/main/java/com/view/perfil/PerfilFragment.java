package com.view.perfil;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.cornApp.databinding.PerfilFragmentBinding;
import com.utils.Utils;
import com.utils.UtilsHTTP;

import org.json.JSONException;
import org.json.JSONObject;

public class PerfilFragment extends Fragment {
    private PerfilFragmentBinding binding;

    public static Usuari user = new Usuari();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = PerfilFragmentBinding.inflate(inflater, container, false);

        binding.sync.setOnClickListener(v -> {
            String name = binding.profileUserInput.getText().toString();
            String surname = binding.profileSurnameInput.getText().toString();
            String phone = binding.profilePhoneInput.getText().toString();
            String email = binding.profileEmailInput.getText().toString();

            if (name.isEmpty() ||
                    surname.isEmpty() ||
                    phone.isEmpty() ||
                    email.isEmpty()){
                Utils.toast(getActivity(),"Todos los campos son obligatorios");
            } else {
                try {
                    clear();
                    JSONObject obj = new JSONObject("{}");
                    obj.put("name",name);
                    obj.put("surname",surname);
                    obj.put("phone",phone);
                    obj.put("email",email);
                    obj.put("balance",100);

                    user.setUserId(phone);

                    UtilsHTTP.sendPOST(Utils.apiUrl + "/api/signup", obj.toString(), (response) -> {
                        try {
                            checkUser(response);
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

    public void checkUser(String response) throws JSONException {

        JSONObject objResponse = new JSONObject(response);

        if (objResponse.getString("status").equals("OK")) {
            String msg = objResponse.getString("message");

            if(msg.equalsIgnoreCase("User created correctly")){
                Utils.toast(getActivity(),msg);
            } else if(msg.equals("User already exists")){
                Utils.toast(getActivity(),msg);
            }

        }
    }

    public void clear(){
        binding.profilePhoneInput.setText("");
        binding.profileEmailInput.setText("");
        binding.profileUserInput.setText("");
        binding.profileSurnameInput.setText("");
    }
}