package com.view.historial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.cornApp.databinding.HistorialFragmentBinding;

public class HistorialFragment extends Fragment {

    private HistorialFragmentBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = HistorialFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

}