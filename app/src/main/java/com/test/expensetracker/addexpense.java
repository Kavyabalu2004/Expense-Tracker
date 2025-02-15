package com.test.expensetracker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.test.expensetracker.Expense;
import com.test.expensetracker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class addexpense extends AppCompatActivity {

    private EditText descriptionEditText;
    private EditText amountEditText;
    private EditText dateEditText;
    private Button saveExpenseButton;
    private DatabaseReference expensesRef;
    private FirebaseUser currentUser;

    private static final String API_KEY = "9iW8iPkwIJvQcBPkbfKLN6RxooKxoHYmXbqhHMOVS5I6ddJ9xyfbvnPJgFoh";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addexpense);

        descriptionEditText = findViewById(R.id.descriptionEditText);
        amountEditText = findViewById(R.id.amountEditText);
        dateEditText = findViewById(R.id.dateEditText);
        saveExpenseButton = findViewById(R.id.saveExpenseButton);

        // Initialize Firebase Realtime Database reference
        expensesRef = FirebaseDatabase.getInstance().getReference().child("users");

        // Get the current logged-in user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Set current date and time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());
        dateEditText.setText(currentDateTime);

        saveExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExpenseToDatabase();
            }
        });
    }

    private void addExpenseToDatabase() {
        String description = descriptionEditText.getText().toString().trim();
        String amount = amountEditText.getText().toString().trim();
        String dateTime = dateEditText.getText().toString().trim();

        if (!description.isEmpty() && !amount.isEmpty()) {
            String userId = currentUser.getUid();

            Expense expense = new Expense(description, amount, dateTime);
            DatabaseReference userExpensesRef = expensesRef.child(userId).child("expenses");
            userExpensesRef.push().setValue(expense);

            descriptionEditText.setText("");
            amountEditText.setText("");
            Toast.makeText(this, "Congrats You've earned a Badge: Expense Manager", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please enter description and amount", Toast.LENGTH_SHORT).show();
        }
    }
}
