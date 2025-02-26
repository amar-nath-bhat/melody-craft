package com.example.melodycraft.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.melodycraft.AuthActivity;
import com.example.melodycraft.MainActivity;
import com.example.melodycraft.ProfileActivity;
import com.example.melodycraft.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private TextInputEditText emailField, passwordField, nameField;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        emailField = view.findViewById(R.id.email);
        passwordField = view.findViewById(R.id.password);
        nameField = view.findViewById(R.id.name);
        MaterialButton updateEmailButton = view.findViewById(R.id.update_email_button);
        MaterialButton updatePasswordButton = view.findViewById(R.id.update_password_button);
        MaterialButton deleteUserButton = view.findViewById(R.id.delete_user_button);
        MaterialButton logoutButton = view.findViewById(R.id.logout_button);

        // Set current user data
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            emailField.setText(user.getEmail());
            nameField.setText(user.getDisplayName());
        }

        // Set click listeners
        updateEmailButton.setOnClickListener(v -> updateEmail());
        updatePasswordButton.setOnClickListener(v -> updatePassword());
        deleteUserButton.setOnClickListener(v -> deleteUser());
        logoutButton.setOnClickListener(v -> logout());
    }

    private void updateEmail() {
        String newEmail = emailField.getText().toString().trim();
        if (newEmail.isEmpty()) {
            emailField.setError("Email cannot be empty");
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.updateEmail(newEmail).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    showToast("Email updated successfully");
                } else {
                    showToast("Failed to update email: " + task.getException().getMessage());
                }
            });
        }
    }

    private void updatePassword() {
        String newPassword = passwordField.getText().toString().trim();
        if (newPassword.isEmpty()) {
            passwordField.setError("Password cannot be empty");
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    showToast("Password updated successfully");
                    passwordField.setText("");
                } else {
                    showToast("Failed to update password: " + task.getException().getMessage());
                }
            });
        }
    }

    private void deleteUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    showToast("User account deleted");
                    startActivity(new Intent(requireContext(), AuthActivity.class));
                    requireActivity().finish();
                } else {
                    showToast("Failed to delete user: " + task.getException().getMessage());
                }
            });
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}