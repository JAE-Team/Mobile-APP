package com.cornApp.view.perfil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.cornApp.LoginActivity;
import com.cornApp.R;
import com.cornApp.databinding.PerfilFragmentBinding;
import com.cornApp.utils.FileUtil;
import com.cornApp.utils.Utils;
import com.cornApp.utils.UtilsHTTP;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

public class PerfilFragment extends Fragment {
    private PerfilFragmentBinding binding;
    ActivityResultLauncher<Intent> launcher;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = PerfilFragmentBinding.inflate(inflater, container, false);

        getProfileInfo();

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                ClipData clipData = data.getClipData();
                if (clipData != null && clipData.getItemCount() >= 2) {
                    Uri uri1 = clipData.getItemAt(0).getUri();
                    Uri uri2 = clipData.getItemAt(1).getUri();

                    try {
                        File anvers = FileUtil.from(getActivity(),uri1);
                        File revers = FileUtil.from(getActivity(),uri2);
                        uploadFile(anvers,revers);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            }
        });

        binding.dni.setOnClickListener(v -> {
            startGallery();

        });

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

    private void uploadFile (File anversDNI, File reversDNI) {

        try {
            // Read image from File and convert to Base64
            byte[] anvers = new byte[0];
            byte[] revers = new byte[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                anvers = Files.readAllBytes(Paths.get(anversDNI.getPath()));
                revers = Files.readAllBytes(Paths.get(reversDNI.getPath()));
            }

            String anversBase64 = null;
            String reversBase64 = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                anversBase64 = Base64.getEncoder().encodeToString(anvers);
                reversBase64 = Base64.getEncoder().encodeToString(revers);
            }

            JSONObject obj = new JSONObject("{}");
            obj.put("type", "uploadFile");
            SharedPreferences sharedPref = getActivity().getSharedPreferences("sessionUser",Context.MODE_PRIVATE);
            obj.put("user_id", sharedPref.getString("phone",""));
            obj.put("anvers", anversBase64);
            obj.put("revers", reversBase64);

            UtilsHTTP.sendPOST(Utils.apiUrl + "/api/send_id", obj.toString(), (response) -> {
                try {
                    JSONObject objResponse = new JSONObject(response);
                    if (objResponse.getString("status").equals("OK")) {
                        popupMessage(objResponse.getString("message"));

                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("status", objResponse.getString("statusDNI"));
                        editor.commit();
                        changeStatus();

                    } else if(objResponse.getString("status").equals("KO")){
                        popupMessage(objResponse.getString("message"));
                    }
                } catch (JSONException e) {
                    // throw new RuntimeException(e);
                }
            });

        } catch (IOException e) { e.printStackTrace(); } catch (JSONException e) {
            throw new RuntimeException(e);
        }
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

    public void startGallery(){
        //Create Intent
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        launcher.launch(Intent.createChooser(intent,"Escull dos fotos: "));;
    }

    public void getProfileInfo(){
        SharedPreferences sharedPref = getActivity().getSharedPreferences("sessionUser",Context.MODE_PRIVATE);

        String strName = sharedPref.getString("name", "");
        String strSurname = sharedPref.getString("surname", "");
        String strPhone = sharedPref.getString("phone", "");
        String strEmail = sharedPref.getString("email", "");

        binding.nom.setText(strName);
        binding.cognoms.setText(strSurname);
        binding.telefon.setText(strPhone);
        binding.email.setText(strEmail);

        changeStatus();
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

    public void changeStatus(){
        SharedPreferences sharedPref = getActivity().getSharedPreferences("sessionUser",Context.MODE_PRIVATE);
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

        binding.statusText.setText(strStatus);
    }
}