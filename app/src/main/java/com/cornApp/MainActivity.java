package com.cornApp;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.cornApp.R;
import com.cornApp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static String sessionToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupNavController();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
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

    public static void setSessionToken(String st){
        sessionToken = st;
    }
    public static String getSessionToken() {
        return sessionToken;
    }
}