package com.test.expensetracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class profile extends AppCompatActivity {

    private TextView textViewName, textViewEmail, textChangePassword, textviewstatus;
    private EditText editTextPassword, editTextCurrentPassword;
    private Button buttonChangePassword;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase Authentication
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        textViewName = findViewById(R.id.textViewName);
        textViewEmail = findViewById(R.id.textViewEmail);
        textChangePassword = findViewById(R.id.textchangepassword);
        editTextPassword = findViewById(R.id.editTextPassword);
        textviewstatus=findViewById(R.id.textViewStatus);
        editTextCurrentPassword=findViewById(R.id.editTextCurrentPassword);
        buttonChangePassword = findViewById(R.id.buttonChangePassword);

        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        DataSnapshot dataSnapshot = task.getResult();
                        if (dataSnapshot.exists()) {
                            String name = dataSnapshot.child("Name").getValue(String.class);
                            String email = dataSnapshot.child("email").getValue(String.class);

                            textViewName.setText("Name: " + name);
                            textViewEmail.setText("Email: " + email);
                        }
                    }
                }
            });
        }

        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = editTextPassword.getText().toString();
                String currentPassword = editTextCurrentPassword.getText().toString();

                if (currentUser != null) {
                    // Get the user's current email
                    String email = currentUser.getEmail();

                    // Create the credential for re-authentication
                    AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);

                    // Prompt the user to re-authenticate
                    currentUser.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Re-authentication successful, now update the password
                                        currentUser.updatePassword(newPassword)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            textviewstatus.setText("Password Updated Successfully");
                                                        } else {
                                                            textviewstatus.setText("Password Updation Failed");
                                                        }
                                                    }
                                                });
                                    } else {
                                        textviewstatus.setText("Re Authentication Failed");
                                    }
                                }
                            });
                }
            }
        });
    }
}