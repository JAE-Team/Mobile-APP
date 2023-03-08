package com.cornApp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.cornApp.databinding.ActivityMainBinding;
import com.cornApp.utils.Utils;
import com.cornApp.utils.UtilsHTTP;
import com.cornApp.view.perfil.PerfilFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        try {
            JSONObject obj = new JSONObject("{}");

            SharedPreferences sharedPref = this.getSharedPreferences("sessionUser", Context.MODE_PRIVATE);
            String sToken = sharedPref.getString("sessionToken","");
            obj.put("sessionToken", sToken);

            Log.d("token",sToken);

            UtilsHTTP.sendPOST(Utils.apiUrl + "/api/get_profile", obj.toString(), (response) -> {
                try {
                    JSONObject objResponse = new JSONObject(response);

                    if (objResponse.getString("status").equals("OK")) {
                        JSONArray dataServer = objResponse.getJSONArray("message");
                        JSONObject userData = dataServer.getJSONObject(0);

                        sendDataToFragment(userData.getString("userName"),
                                userData.getString("userSurname"),
                                userData.getString("userId"),
                                userData.getString("userEmail"),
                                userData.getString("verificationStatus"),
                                userData.getString("userBalance")
                                );

                        this.runOnUiThread(() -> {
                            setContentView(binding.getRoot());
                            setupNavController();
                            ActionBar actionBar = getSupportActionBar();
                            actionBar.hide();
                        });

                    } else if (objResponse.getString("status").equals("Error")){
                        Intent intent = new Intent(this, LoginActivity.class);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupNavController(){
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.iniciFragment,
                R.id.historialFragment,
                R.id.cobramentFragment,
                R.id.perfilFragment,
                R.id.EscanerFragment
        ).build();

        NavController navController = Navigation.findNavController(this, R.id.fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    public void popupMessage(String message) {
        this.runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Registre d'usuari");
            builder.setMessage(message + "!!");

            AlertDialog dialog = builder.create();
            dialog.show();

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

    public void sendDataToFragment(String name, String surname, String phone, String email, String status, String balance){
        SharedPreferences sharedPref = getSharedPreferences("sessionUser",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("name", name);
        editor.putString("surname", surname);
        editor.putString("phone", phone);
        editor.putString("email", email);
        editor.putString("balance", balance);
        editor.putString("status", status);
        editor.commit();
    }
}