package com.example.cornapp.view.perfil;

import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cornapp.databinding.PerfilFragmentBinding;
import com.example.cornapp.utils.Utils;
import com.example.cornapp.utils.UtilsHTTP;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class PerfilFragment extends Fragment {
    private PerfilFragmentBinding binding;

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
                    JSONObject obj = new JSONObject("{}");
                    obj.put("name",name);
                    obj.put("surname",surname);
                    obj.put("phone",phone);
                    obj.put("email",email);

                    UtilsHTTP.sendPOST("http://10.0.2.2:3001/api/signup", obj.toString(), (response) -> {
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
            Log.d("User",msg);

            if(msg.equalsIgnoreCase("User created correctly")){
                Utils.toast(getActivity(),msg);
            } else if(msg.equalsIgnoreCase("User already exists, new user can't be created")){
                Utils.toast(getActivity(),msg);
            }

        }
    }
}