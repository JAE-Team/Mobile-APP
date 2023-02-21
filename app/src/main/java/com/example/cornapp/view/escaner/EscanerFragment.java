package com.example.cornapp.view.escaner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.cornapp.databinding.EscanerFragmentBinding;

public class EscanerFragment extends Fragment {

    private EscanerFragmentBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = EscanerFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

}