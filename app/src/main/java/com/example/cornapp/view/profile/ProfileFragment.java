package com.example.cornapp.view.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.cornapp.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;

    private boolean userEditing = false;
    private boolean contactEditing = false;
    private boolean emailEditing = false;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        setupProfileUI();
        setupListeners();
        setupObservers();
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        return binding.getRoot();
    }

    private void setupProfileUI() {
        binding.profileUserNameValue.setFocusable(false);
        binding.profileUserSurnameValue.setFocusable(false);
        binding.profileEmailEdit.setFocusable(false);
        binding.profileContactEdit.setFocusable(false);
    }

    public void setupListeners(){
        binding.profileUserEdit.setOnClickListener(view -> {
            setUserEditable(!userEditing);
        });
        binding.profileContactEdit.setOnClickListener(view -> {
            setContactEditable(!contactEditing);
        });
        binding.profileEmailCardview.setOnClickListener(view -> {
            setEmailEditable(!emailEditing);
        });
        binding.sync.setOnClickListener(view -> {
            viewModel.updateUser(
                    binding.profileUserNameValue.getText(),
                    binding.profileUserSurnameValue.getText(),
                    binding.profileContactTelfValue.getText(),
                    binding.profileEmailEditValue.getText()
            );
        });
    }

    public void setupObservers(){
        final Observer<User> nameObserver = _user -> {
            Log.d("5cos", _user.toString());
        };
    }

    private void setEmailEditable(boolean state) {
        binding.profileEmailEdit.setFocusable(state);
        emailEditing = state;
    }

    private void setContactEditable(boolean state) {
        binding.profileContactEdit.setFocusable(state);
        contactEditing = state;
    }

    private void setUserEditable(boolean state) {
        binding.profileUserNameValue.setFocusable(state);
        binding.profileUserSurnameValue.setFocusable(state);
        userEditing = state;
    }

}