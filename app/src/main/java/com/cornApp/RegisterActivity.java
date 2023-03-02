package com.cornApp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;

import com.cornApp.databinding.ActivityMainBinding;
import com.cornApp.utils.Utils;
import com.cornApp.utils.UtilsHTTP;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private RegisterActivity binding;

    private EditText username, surnames, email, phone, pwd, pwd2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    private void register(){
        if (username.getText().toString().isEmpty() || surnames.getText().toString().isEmpty() || email.getText().toString().isEmpty() || phone.getText().toString().isEmpty() || pwd.getText().toString().isEmpty() || pwd2.getText().toString().isEmpty()){
            popupMessage("Tots els camps son obligatoris");
        } else if(checkIfPasswordIsEqual(pwd.getText().toString(),pwd2.getText().toString())){
            popupMessage("Les contraseñes no son iguals");
            pwd.setText("");
            pwd2.setText("");
        } else {
            try {
                JSONObject obj = new JSONObject("{}");
                obj.put("userId",phone.getText().toString());
                obj.put("userPassword",pwd);
                obj.put("userEmail",email.getText().toString());
                obj.put("userName",username.getText().toString());
                obj.put("userSurname",surnames.getText().toString());
                obj.put("userBalance",100);

                UtilsHTTP.sendPOST(Utils.apiUrl + "/api/signup", obj.toString(), (response) -> {
                    try {
                        registerUser(response);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (JSONException e) {
                throw new RuntimeException(e);
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
            popupMessage(objResponse.getString("message"));
            MainActivity.setSessionToken(objResponse.getString("token"));
        } else if (objResponse.getString("status").equals("Error")){
            popupMessage(objResponse.getString("message"));
        }
    }

    private boolean checkIfPasswordIsEqual(String pwd1, String pwd2){
        if(pwd1.equals(pwd2)){
            return true;
        } else {
            return false;
        }
    }
}