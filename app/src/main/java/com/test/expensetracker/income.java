package com.test.expensetracker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class income extends AppCompatActivity {

    private EditText incomeAmountEditText;
    private DatePicker incomeDatePicker;
    private Button saveIncomeButton;

    private DatabaseReference incomeRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        incomeAmountEditText = findViewById(R.id.incomeAmountEditText);
        incomeDatePicker = findViewById(R.id.incomeDatePicker);
        saveIncomeButton = findViewById(R.id.saveIncomeButton);

        // Get the current logged-in user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize Firebase Realtime Database reference
        incomeRef = FirebaseDatabase.getInstance().getReference().child("users")
                .child(currentUser.getUid()).child("income");

        // Set the current date as the initial selection in DatePicker
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        incomeDatePicker.init(year, month, day, null);

        saveIncomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveIncome();
            }
        });
    }

    private void saveIncome() {
        String amount = incomeAmountEditText.getText().toString().trim();

        if (amount.isEmpty()) {
            Toast.makeText(this, "Please enter the income amount", Toast.LENGTH_SHORT).show();
            return;
        }

        int day = incomeDatePicker.getDayOfMonth();
        int month = incomeDatePicker.getMonth();
        int year = incomeDatePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        Date incomeDate = calendar.getTime();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
        String monthName = monthFormat.format(incomeDate);

        String incomeId = monthName;

        incomeRef.child(incomeId).setValue(amount);

        Toast.makeText(this, "Income saved successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
