package com.cornApp.view.inici;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.cornApp.databinding.IniciFragmentBinding;

public class IniciFragment extends Fragment {

    private IniciFragmentBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = IniciFragmentBinding.inflate(inflater, container, false);
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.message.setVisibility(View.INVISIBLE);
            }
        });
        return binding.getRoot();
    }

}