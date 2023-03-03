package com.cornApp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cornApp.utils.Utils;
import com.cornApp.utils.UtilsHTTP;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    public Button login;
    public EditText email, pwd;
    public TextView createAccount;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = findViewById(R.id.register);
        email = findViewById(R.id.profile_email_input);
        pwd = findViewById(R.id.input_pwd);
        createAccount = (TextView) findViewById(R.id.createAccount);

        login.setOnClickListener(v -> {
            login();
        });

        createAccount.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void login(){
        if (email.getText().toString().isEmpty() || pwd.getText().toString().isEmpty()){
            popupMessage("Tots els camps son obligatoris");
        } else {
            try {
                JSONObject obj = new JSONObject("{}");
                obj.put("userEmail",email.getText());
                obj.put("userPassword",pwd.getText());

                UtilsHTTP.sendPOST(Utils.apiUrl + "/api/login", obj.toString(), (response) -> {
                    try {
                        checkIfUserExists(response);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void checkIfUserExists(String response) throws JSONException {
        JSONObject objResponse = new JSONObject(response);

        if (objResponse.getString("status").equals("OK")) {
            popupMessage(objResponse.getString("message"));
            popupMessage(objResponse.getString("token"));

            // Create session token
            SharedPreferences sharedPref = getSharedPreferences("sessionToken",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("sessionToken", objResponse.getString("token"));
            editor.commit();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (objResponse.getString("status").equals("Error")){
            popupMessage(objResponse.getString("message"));
        }
    }

    public void popupMessage(String message) {
        this.runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Inici de sessió");
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

}