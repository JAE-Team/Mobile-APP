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

import com.cornApp.databinding.ActivityMainBinding;
import com.cornApp.utils.Utils;
import com.cornApp.utils.UtilsHTTP;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class RegisterActivity extends AppCompatActivity {
    public Button registrarse;
    private EditText username, surnames, email, phone, pwd, pwd2;
    public TextView haveAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.profile_user_input);
        surnames = findViewById(R.id.profile_surname_input);
        email = findViewById(R.id.profile_email_input);
        phone = findViewById(R.id.profile_phone_input);
        pwd = findViewById(R.id.input_pwd);
        pwd2 = findViewById(R.id.input_repwd);

        registrarse = findViewById(R.id.register);
        haveAccount = findViewById(R.id.haveAccount);

        registrarse.setOnClickListener(v -> {
            register();
        });

        haveAccount.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

    }

    private void register(){
        if (username.getText().toString().isEmpty() || surnames.getText().toString().isEmpty() || email.getText().toString().isEmpty() || phone.getText().toString().isEmpty() || pwd.getText().toString().isEmpty() || pwd2.getText().toString().isEmpty()){
            popupMessage("Tots els camps son obligatoris");

        } else if(checkIfPasswordIsEqual(pwd.getText().toString(), pwd2.getText().toString())){
            popupMessage("Les contrasenyes no son iguals");
            pwd.setText(""); pwd2.setText("");

        } else if(pwd.getText().toString().length() < 8){
            popupMessage("La contrasenya ha de contenir 8 caràcters mínim");
            pwd.setText(""); pwd2.setText("");

        } else {
            try {
                JSONObject obj = new JSONObject("{}");
                obj.put("userId",phone.getText().toString());
                obj.put("userPassword",pwd.getText().toString());
                obj.put("userEmail",email.getText().toString());
                obj.put("userName",username.getText().toString());
                obj.put("userSurname",surnames.getText().toString());
                obj.put("userBalance",100);

                UtilsHTTP.sendPOST(Utils.apiUrl + "/api/signup", obj.toString(), (response) -> {
                    try {
                        registerUser(response);
                    } catch (JSONException e) {
                        // throw new RuntimeException(e);
                    }
                });
            } catch (JSONException e) {
                // throw new RuntimeException(e);
            }

        }
    }

    public void popupMessage(String message) {
        this.runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Registre d'usuari");
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

    private void registerUser(String response) throws JSONException {
        JSONObject objResponse = new JSONObject(response);

        if (objResponse.getString("status").equals("OK")) {
            // Create session token
            SharedPreferences sharedPref = getSharedPreferences("sessionUser",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("sessionToken", objResponse.getString("token"));
            editor.commit();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (objResponse.getString("status").equals("KO")){
            popupMessage(objResponse.getString("message"));
        }
    }

    private boolean checkIfPasswordIsEqual(String pwd1, String pwd2){
        if(pwd1.equals(pwd2)){
            return false;
        } else {
            return true;
        }
    }
}