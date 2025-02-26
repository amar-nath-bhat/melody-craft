package com.example.melodycraft;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Arrays;
import java.util.List;

public class AuthActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            // already signed in
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            // not signed in
            createSignInIntent();
        }
    }

    private void createSignInIntent() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
                new FirebaseAuthUIActivityResultContract(),
                (result) -> {
                    IdpResponse response = result.getIdpResponse();
                    if (result.getResultCode() == RESULT_OK) {
                        // Successfully signed in
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        // Sign in failed
                        if (response == null) {
                            // User pressed back button
                            finish();
                        }
                    }
                });


        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(false)
                .setTheme(R.style.Theme_MelodyCraft)
                .setAvailableProviders(providers)
                .build();
        //                                                                                                     java.lang.IllegalArgumentException: com.example.melodycraft: Targeting U+ (version 34 and above) disallows creating or retrieving a PendingIntent with FLAG_MUTABLE, an implicit Intent within and without FLAG_NO_CREATE and FLAG_ALLOW_UNSAFE_IMPLICIT_INTENT for security reasons. To retrieve an already existing PendingIntent, use FLAG_NO_CREATE, however, to create a new PendingIntent with an implicit Intent use FLAG_IMMUTABLE.

        // to fix the error above, we need to add the following line of code:
        signInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        signInLauncher.launch(signInIntent);
    }
}