package com.cornApp.view.historial;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.cornApp.R;
import com.cornApp.databinding.HistorialFragmentBinding;
import com.cornApp.utils.Utils;
import com.cornApp.utils.UtilsHTTP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistorialFragment extends Fragment {

    private HistorialFragmentBinding binding;
    private ArrayList<Transaccio> transaccions;
    private ArrayAdapter<Transaccio> adapter;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = HistorialFragmentBinding.inflate(inflater, container, false);

        checkDB();

        SharedPreferences sharedUser = getActivity().getSharedPreferences("sessionUser",Context.MODE_PRIVATE);
        SharedPreferences sharedPref = getActivity().getSharedPreferences("sessionToken", Context.MODE_PRIVATE);

        try {
            JSONObject obj = new JSONObject("{}");


            obj.put("session_token", sharedPref.getString("sessionToken",""));
            obj.put("userId", sharedUser.getString("phone",""));

            UtilsHTTP.sendPOST(Utils.apiUrl + "/api/get_transactions", obj.toString(), this::chargeTransactions);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    public void checkDB() {
        try {
            JSONObject obj = new JSONObject("{}");
            SharedPreferences sharedPref = getActivity().getSharedPreferences("sessionUser", Context.MODE_PRIVATE);
            String sToken = sharedPref.getString("sessionToken","");
            obj.put("sessionToken", sToken);

            UtilsHTTP.sendPOST(Utils.apiUrl + "/api/get_profile", obj.toString(), (response) -> {
                getActivity().runOnUiThread(() -> {
                    try {
                        JSONObject objResponse = new JSONObject(response);

                        if (objResponse.getString("status").equals("OK")) {
                            JSONArray dataServer = objResponse.getJSONArray("message");
                            JSONObject userData = dataServer.getJSONObject(0);
                            binding.userBalance.setText(userData.getString("userBalance") + "€");
                            sharedPref.edit().putString("userBalance",userData.getString("userBalance")).apply();
                        }
                    } catch (JSONException e) {
                        // throw new RuntimeException(e);
                    }
                });
            });
        } catch (JSONException e) {
            // throw new RuntimeException(e);
        }
    }
    public void chargeTransactions(String response){
        getActivity().runOnUiThread(() -> {
        try {
            transaccions = new ArrayList<>();

            JSONObject objResponse = new JSONObject(response);
            Object message = objResponse.get("message");

            if (message instanceof String) {
                Utils.toast(getActivity(), "No se ha podido obtener del servidor la información de las transferencias de ");
            } else if (message instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) message;
                if(jsonArray.length() ==0){
                    Utils.toast(getActivity(),"No se han encontrado transferencias asociadas a este usuario en la base de datos");
                } else {


                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            transaccions.add(new Transaccio(
                                    jsonObject.getString("destinyName"),
                                    jsonObject.getString("originName"),
                                    jsonObject.getDouble("ammount"),
                                    jsonObject.getString("accepted"),
                                    jsonObject.getString("timeFinish")
                            ));

                        }
                        adapter = new ArrayAdapter<Transaccio>(getActivity(), R.layout.list_item, transaccions)
                        {
                            @Override
                            public View getView(int pos, View convertView, ViewGroup container)
                            {
                                if( convertView==null ) {
                                    convertView = getLayoutInflater().inflate(R.layout.list_item, container, false);
                                }
                                ((TextView) convertView.findViewById(R.id.payerName)).setText(getItem(pos).originName);
                                ((TextView) convertView.findViewById(R.id.receiverName)).setText(getItem(pos).destinyName);
                                ((TextView) convertView.findViewById(R.id.amountQuantity)).setText(String.valueOf((int) getItem(pos).ammount));
                                ((TextView) convertView.findViewById(R.id.statusPlaceholder)).setText(getItem(pos).accepted);
                                ((TextView) convertView.findViewById(R.id.timePlaceholder)).setText(getItem(pos).timeFinish);
                                return convertView;
                            }
                        };
                        binding.transactionList.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                }

            }
        } catch (JSONException e) {
            // throw new RuntimeException(e);
        }
        });
    }

    class Transaccio {
        private String destinyName;
        private String originName;
        private double ammount;
        private String accepted;
        private String timeFinish;

        public Transaccio(String destinyName, String originName, double ammount, String accepted, String timeFinish) {
            this.destinyName = destinyName;
            this.originName = originName;
            this.accepted = accepted;
            this.ammount = ammount;
            this.timeFinish = timeFinish;
        }
    }

}