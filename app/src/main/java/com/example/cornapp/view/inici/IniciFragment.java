package com.example.cornapp.view.inici;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.cornapp.databinding.IniciFragmentBinding;

public class IniciFragment extends Fragment {

    private IniciFragmentBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = IniciFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

}