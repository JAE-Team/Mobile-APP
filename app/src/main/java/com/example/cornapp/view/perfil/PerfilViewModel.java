package com.example.cornapp.view.perfil;

import android.text.Editable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PerfilViewModel extends ViewModel {
    private MutableLiveData<Usuari> user;

    public void updateUser(Editable text, Editable text1, Editable text2, Editable text3) {
    }
    public LiveData<Usuari> getUser(){
        return user;
    }

}