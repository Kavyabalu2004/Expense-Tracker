package com.test.expensetracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class viewexpense extends AppCompatActivity {

    private TableLayout expensesTable;
    private DatabaseReference expensesRef;
    private List<Expense> expensesList;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewexpense);

        expensesTable = findViewById(R.id.expensesTable);

        // Get the current logged-in user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize Firebase Realtime Database reference
        expensesRef = FirebaseDatabase.getInstance().getReference().child("users");

        expensesList = new ArrayList<>();

        expensesRef.child(currentUser.getUid()).child("expenses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                expensesList.clear();
                for (DataSnapshot expenseSnapshot : dataSnapshot.getChildren()) {
                    Expense expense = expenseSnapshot.getValue(Expense.class);
                    expensesList.add(expense);
                }
                displayExpenses();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void displayExpenses() {
        expensesTable.removeAllViews();

        // Sort expensesList in ascending order based on date
        Collections.sort(expensesList, new Comparator<Expense>() {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

            @Override
            public int compare(Expense expense1, Expense expense2) {
                try {
                    Date date1 = dateFormat.parse(expense1.getDateTime());
                    Date date2 = dateFormat.parse(expense2.getDateTime());
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });

        // Create table header
        TableRow headerRow = new TableRow(this);
        TextView dateHeader = new TextView(this);
        TextView descriptionHeader = new TextView(this);
        TextView amountHeader = new TextView(this);
        dateHeader.setText("Date");
        descriptionHeader.setText("Description");
        amountHeader.setText("Amount");
        headerRow.addView(dateHeader);
        headerRow.addView(descriptionHeader);
        headerRow.addView(amountHeader);
        expensesTable.addView(headerRow);

        // Set layout parameters for the header cells
        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        dateHeader.setLayoutParams(params);
        descriptionHeader.setLayoutParams(params);
        amountHeader.setLayoutParams(params);

        // Populate table with expenses
        for (Expense expense : expensesList) {
            TableRow row = new TableRow(this);
            TextView dateTextView = new TextView(this);
            TextView descriptionTextView = new TextView(this);
            TextView amountTextView = new TextView(this);
            dateTextView.setText(expense.getDateTime());
            descriptionTextView.setText(expense.getDescription());
            amountTextView.setText(expense.getAmount());

            // Set layout parameters for the data cells
            dateTextView.setLayoutParams(params);
            descriptionTextView.setLayoutParams(params);
            amountTextView.setLayoutParams(params);

            row.addView(dateTextView);
            row.addView(descriptionTextView);
            row.addView(amountTextView);
            expensesTable.addView(row);
        }
    }
}
