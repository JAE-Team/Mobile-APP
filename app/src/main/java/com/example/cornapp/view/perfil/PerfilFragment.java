package com.example.cornapp.view.perfil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cornapp.databinding.PerfilFragmentBinding;

public class PerfilFragment extends Fragment {
    private PerfilFragmentBinding binding;
    private PerfilViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = PerfilFragmentBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(PerfilViewModel.class);
        return binding.getRoot();
    }
}