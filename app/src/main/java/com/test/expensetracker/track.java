package com.test.expensetracker;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class track extends AppCompatActivity {

    private DatabaseReference expensesRef;
    private DatabaseReference incomeRef;
    private FirebaseUser currentUser;

    private TextView totalAmountTextView;
    private TextView totalExpensesTextView;
    private TextView balanceAmountTextView;
    private TableLayout highestExpensesTable;
    private PieChart incomeExpensePieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        totalAmountTextView = findViewById(R.id.totalAmountTextView);
        totalExpensesTextView = findViewById(R.id.totalExpensesTextView);
        balanceAmountTextView = findViewById(R.id.balanceAmountTextView);
        highestExpensesTable = findViewById(R.id.highestExpensesTable);
        incomeExpensePieChart = findViewById(R.id.incomeExpensePieChart);

        // Get the current logged-in user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Initialize Firebase Realtime Database references
        expensesRef = FirebaseDatabase.getInstance().getReference().child("users");
        incomeRef = FirebaseDatabase.getInstance().getReference().child("users")
                .child(currentUser.getUid()).child("income");

        expensesRef.child(currentUser.getUid()).child("expenses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                double totalAmount = 0;
                List<Expense> expenses = new ArrayList<>();

                for (DataSnapshot expenseSnapshot : dataSnapshot.getChildren()) {
                    Expense expense = expenseSnapshot.getValue(Expense.class);

                    double amount = Double.parseDouble(expense.getAmount());
                    totalAmount += amount;

                    expenses.add(expense); // Add expense to the list
                }

                // Sort expenses by amount in descending order
                Collections.sort(expenses, new Comparator<Expense>() {
                    @Override
                    public int compare(Expense expense1, Expense expense2) {
                        double amount1 = Double.parseDouble(expense1.getAmount());
                        double amount2 = Double.parseDouble(expense2.getAmount());
                        return Double.compare(amount2, amount1);
                    }
                });

                // Display total amount
                displayTotalAmount(totalAmount);

                // Display highest expenses
                displayHighestExpenses(expenses.subList(0, Math.min(expenses.size(), 3)));

                // Calculate and display total expenses of the month and balance amount
                calculateAndDisplayBalanceAmount(totalAmount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

        // Display the income and expenses chart
        displayIncomeExpensesChart();
    }

    private void displayTotalAmount(double totalAmount) {
        String amountText = "Total Amount: ₹ " + String.format("%.2f", totalAmount);
        totalAmountTextView.setText(amountText);
    }

    private void displayHighestExpenses(List<Expense> expenses) {
        // Clear the existing rows
        highestExpensesTable.removeAllViews();

        // Add the header row
        TableRow headerRow = new TableRow(this);

        TextView descriptionHeader = new TextView(this);
        descriptionHeader.setText("Description");
        descriptionHeader.setTextColor(Color.WHITE);
        descriptionHeader.setTypeface(null, Typeface.BOLD);
        descriptionHeader.setPadding(10, 10, 10, 10);
        descriptionHeader.setBackgroundResource(R.drawable.table_header_cell_bg);
        headerRow.addView(descriptionHeader);

        TextView amountHeader = new TextView(this);
        amountHeader.setText("Amount");
        amountHeader.setTextColor(Color.WHITE);
        amountHeader.setTypeface(null, Typeface.BOLD);
        amountHeader.setPadding(10, 10, 10, 10);
        amountHeader.setBackgroundResource(R.drawable.table_header_cell_bg);
        headerRow.addView(amountHeader);

        highestExpensesTable.addView(headerRow);

        // Add the expense rows
        for (Expense expense : expenses) {
            TableRow expenseRow = new TableRow(this);

            TextView descriptionTextView = new TextView(this);
            descriptionTextView.setText(expense.getDescription());
            descriptionTextView.setTextColor(Color.BLACK);
            descriptionTextView.setPadding(10, 10, 10, 10);
            descriptionTextView.setBackgroundResource(R.drawable.table_data_cell_bg);
            expenseRow.addView(descriptionTextView);

            TextView amountTextView = new TextView(this);
            amountTextView.setText(expense.getAmount());
            amountTextView.setTextColor(Color.BLACK);
            amountTextView.setPadding(10, 10, 10, 10);
            amountTextView.setBackgroundResource(R.drawable.table_data_cell_bg);
            expenseRow.addView(amountTextView);

            highestExpensesTable.addView(expenseRow);
        }
    }

    private void calculateAndDisplayBalanceAmount(final double totalExpenses) {
        final String currentMonth = new SimpleDateFormat("MM", Locale.getDefault()).format(new Date());

        expensesRef.child(currentUser.getUid()).child("expenses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final double[] totalMonthExpenses = {0};

                for (DataSnapshot expenseSnapshot : dataSnapshot.getChildren()) {
                    Expense expense = expenseSnapshot.getValue(Expense.class);
                    String dateTime = expense.getDateTime();

                    if (dateTime != null) {
                        String[] dateTimeParts = dateTime.split(" ");
                        if (dateTimeParts.length >= 2) {
                            String expenseMonth = dateTimeParts[0].split("-")[1];
                            if (expenseMonth.equals(currentMonth)) {
                                double amount = Double.parseDouble(expense.getAmount());
                                totalMonthExpenses[0] += amount;
                            }
                        }
                    }
                }

                incomeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(currentMonth)) {
                            String incomeAmount = dataSnapshot.child(currentMonth).getValue(String.class);
                            if (incomeAmount != null) {
                                double income = Double.parseDouble(incomeAmount);
                                double balanceAmount = income - totalMonthExpenses[0];

                                if (balanceAmount < 0) {
                                    Toast.makeText(track.this, "Income of the month has been fully expensed", Toast.LENGTH_SHORT).show();
                                }

                                String totalExpensesText = "Total Expenses of the Month: ₹ " + String.format("%.2f", totalMonthExpenses[0]);
                                String balanceAmountText = "Balance Amount: ₹ " + String.format("%.2f", balanceAmount);

                                totalExpensesTextView.setText(totalExpensesText);
                                balanceAmountTextView.setText(balanceAmountText);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle error
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void displayIncomeExpensesChart() {
        final String currentMonth = new SimpleDateFormat("MM", Locale.getDefault()).format(new Date());
        final List<PieEntry> entries = new ArrayList<>();

        final float[] income = new float[1];

        incomeRef.child(currentMonth).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String incomeAmount = dataSnapshot.getValue(String.class);
                    if (incomeAmount != null) {
                        income[0] = Float.parseFloat(incomeAmount);
                        entries.add(new PieEntry(income[0], "Total Income of Month"));
                    }
                }

                expensesRef.child(currentUser.getUid()).child("expenses").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        float totalExpenses = 0;
                        for (DataSnapshot expenseSnapshot : dataSnapshot.getChildren()) {
                            Expense expense = expenseSnapshot.getValue(Expense.class);
                            String dateTime = expense.getDateTime();

                            if (dateTime != null) {
                                String[] dateTimeParts = dateTime.split(" ");
                                if (dateTimeParts.length >= 2) {
                                    String expenseMonth = dateTimeParts[0].split("-")[1];
                                    if (expenseMonth.equals(currentMonth)) {
                                        float amount = Float.parseFloat(expense.getAmount());
                                        totalExpenses += amount;
                                    }
                                }
                            }
                        }
                        entries.add(new PieEntry(totalExpenses, "Total Expenses of Month"));
                        entries.add(new PieEntry(income[0] - totalExpenses, "Balance Amount"));

                        PieDataSet dataSet = new PieDataSet(entries, "");
                        dataSet.setColors(getColors());
                        dataSet.setValueTextSize(12f);
                        dataSet.setValueTextColor(Color.WHITE);

                        PieData pieData = new PieData(dataSet);
                        pieData.setValueFormatter(new PercentFormatter(incomeExpensePieChart));
                        pieData.setValueTextSize(12f);
                        pieData.setValueTextColor(Color.WHITE);

                        incomeExpensePieChart.setData(pieData);
                        incomeExpensePieChart.setDrawEntryLabels(false);
                        incomeExpensePieChart.setUsePercentValues(true);
                        incomeExpensePieChart.setHoleColor(Color.TRANSPARENT);
                        incomeExpensePieChart.setTransparentCircleRadius(0f);
                        incomeExpensePieChart.setDrawCenterText(false);
                        incomeExpensePieChart.setDescription(null);

                        // Add custom legend entries
                        Legend legend = incomeExpensePieChart.getLegend();
                        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
                        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                        legend.setDrawInside(false);
                        legend.setFormSize(8f);
                        legend.setFormToTextSpace(4f);
                        legend.setXEntrySpace(8f);
                        legend.setTextColor(Color.BLACK);
                        legend.setEnabled(true);
                        legend.setCustom(createLegendEntries());

                        incomeExpensePieChart.invalidate();
                    }

                    private int[] getColors() {
                        int[] colors = new int[3];
                        colors[0] = Color.BLUE;   // Blue for total income
                        colors[1] = Color.RED;    // Red for total expenses
                        colors[2] = Color.GREEN;  // Green for balance amount
                        return colors;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle error
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }


    private List<LegendEntry> createLegendEntries() {
        List<LegendEntry> legendEntries = new ArrayList<>();

        LegendEntry incomeEntry = new LegendEntry();
        incomeEntry.formColor = Color.BLUE;
        incomeEntry.label = "Income\n\n";
        legendEntries.add(incomeEntry);

        LegendEntry expensesEntry = new LegendEntry();
        expensesEntry.formColor = Color.RED;
        expensesEntry.label = "Total Expenses\n\n";
        legendEntries.add(expensesEntry);

        LegendEntry balanceEntry = new LegendEntry();
        balanceEntry.formColor = Color.GREEN;
        balanceEntry.label = "Balance Amount\n\n";
        legendEntries.add(balanceEntry);

        return legendEntries;
    }

    private String getMonthName(int month) {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month - 1);
        return monthFormat.format(calendar.getTime());
    }
}
