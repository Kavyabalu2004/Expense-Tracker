package com.test.expensetracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class editexpense extends AppCompatActivity {

    private DatabaseReference expensesRef;
    private FirebaseUser currentUser;
    private LinearLayout expensesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editexpense);

        expensesContainer = findViewById(R.id.expensesContainer);

        // Get the current logged-in user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize Firebase Realtime Database reference
        expensesRef = FirebaseDatabase.getInstance().getReference().child("users");

        // Load the expenses
        loadExpenses();
    }

    private void loadExpenses() {
        expensesRef.child(currentUser.getUid()).child("expenses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                expensesContainer.removeAllViews();

                for (final DataSnapshot expenseSnapshot : dataSnapshot.getChildren()) {
                    final Expense expense = expenseSnapshot.getValue(Expense.class);

                    if (expense != null) {
                        // Create a new layout for each expense
                        View expenseView = LayoutInflater.from(editexpense.this).inflate(R.layout.item_expense, expensesContainer, false);

                        TextView dateTextView = expenseView.findViewById(R.id.dateTextView);
                        TextView descriptionTextView = expenseView.findViewById(R.id.descriptionTextView);
                        TextView amountTextView = expenseView.findViewById(R.id.amountTextView);
                        Button editButton = expenseView.findViewById(R.id.editButton);
                        Button deleteButton = expenseView.findViewById(R.id.deleteButton);

                        dateTextView.setText(expense.getDateTime());
                        descriptionTextView.setText(expense.getDescription());
                        amountTextView.setText(expense.getAmount());

                        editButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                editExpense(expenseSnapshot.getKey(), expense);
                            }
                        });

                        deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteExpense(expenseSnapshot.getKey());
                            }
                        });

                        expensesContainer.addView(expenseView);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void editExpense(final String expenseId, final Expense expense) {
        AlertDialog.Builder builder = new AlertDialog.Builder(editexpense.this);
        builder.setTitle("Edit Expense");

        // Create the layout for the dialog
        LinearLayout layout = new LinearLayout(editexpense.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 0, 40, 0);

        // Create EditText fields for description and amount
        final EditText editdateEditText = new EditText(editexpense.this);
        editdateEditText.setHint("Date");
        layout.addView(editdateEditText);

        final EditText editDescriptionEditText = new EditText(editexpense.this);
        editDescriptionEditText.setHint("Description");
        layout.addView(editDescriptionEditText);

        final EditText editAmountEditText = new EditText(editexpense.this);
        editAmountEditText.setHint("Amount");
        layout.addView(editAmountEditText);

        // Set the current expense details in the EditText fields
        editdateEditText.setText(expense.getDateTime());
        editDescriptionEditText.setText(expense.getDescription());
        editAmountEditText.setText(expense.getAmount());

        // Set the current date and time as default values
        String currentDateTime = getCurrentDateTime();
        editdateEditText.setText(currentDateTime);

        builder.setView(layout);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String date = editdateEditText.getText().toString().trim();
                String description = editDescriptionEditText.getText().toString().trim();
                String amount = editAmountEditText.getText().toString().trim();

                if (!description.isEmpty() && !amount.isEmpty()) {
                    String userId = currentUser.getUid();

                    Expense updatedExpense = new Expense(description, amount, date);
                    expensesRef.child(userId).child("expenses").child(expenseId).setValue(updatedExpense);

                    Toast.makeText(editexpense.this, "Expense updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(editexpense.this, "Please enter description and amount", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void deleteExpense(String expenseId) {
        String userId = currentUser.getUid();
        expensesRef.child(userId).child("expenses").child(expenseId).removeValue();

        Toast.makeText(this, "Expense deleted", Toast.LENGTH_SHORT).show();
    }
}
