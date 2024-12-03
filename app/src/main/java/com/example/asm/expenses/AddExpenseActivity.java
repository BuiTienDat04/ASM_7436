package com.example.asm.expenses;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asm.R;
import com.example.asm.budget.BudgetDatabaseHelper;

import java.util.ArrayList;
import java.util.Calendar;

public class AddExpenseActivity extends AppCompatActivity {

    private EditText edtAmount, edtDescription;
    private TextView tvDate;
    private Spinner spinnerBudget;
    private Button btnSave;

    private BudgetDatabaseHelper budgetDatabaseHelper;
    private ExpenseDatabaseHelper expenseDatabaseHelper;
    private int transactionId = -1; // ID của giao dịch nếu đang chỉnh sửa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        edtAmount = findViewById(R.id.edtAmount);
        edtDescription = findViewById(R.id.edtDescription);
        tvDate = findViewById(R.id.tvDate);
        spinnerBudget = findViewById(R.id.spinnerBudget);
        btnSave = findViewById(R.id.btnSave);

        budgetDatabaseHelper = new BudgetDatabaseHelper(this);
        expenseDatabaseHelper = new ExpenseDatabaseHelper(this);

        populateBudgetSpinner();
        tvDate.setOnClickListener(this::onSelectDate);
        btnSave.setOnClickListener(this::onSaveExpense);

        // Kiểm tra nếu đang chỉnh sửa giao dịch
        Intent intent = getIntent();
        transactionId = intent.getIntExtra("transactionId", -1);
        if (transactionId != -1) {
            loadTransactionData(intent);
        }
    }

    private void populateBudgetSpinner() {
        Cursor cursor = budgetDatabaseHelper.getAllBudgetGroups();
        ArrayList<String> budgetGroups = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                budgetGroups.add(cursor.getString(cursor.getColumnIndexOrThrow("group_name")));
            }
            cursor.close();
        }

        if (budgetGroups.isEmpty()) {
            budgetGroups.add("No Budget Available");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, budgetGroups);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBudget.setAdapter(adapter);
    }

    public void onSelectDate(View view) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    tvDate.setText(selectedDate);
                }, year, month, day);

        datePickerDialog.show();
    }

    public void onSaveExpense(View view) {
        String amountText = edtAmount.getText().toString();
        String description = edtDescription.getText().toString();
        String date = tvDate.getText().toString();
        String budgetName = spinnerBudget.getSelectedItem().toString();

        if (amountText.isEmpty() || description.isEmpty() || date.equals("Select Date") || budgetName.equals("No Budget Available")) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount!", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("ExpenseData", "Amount: " + amount + ", Description: " + description + ", Date: " + date + ", Budget: " + budgetName);

        boolean isSuccess;
        if (transactionId == -1) {
            // Insert new expense
            isSuccess = expenseDatabaseHelper.insertExpense(amount, description, date, budgetName);
            Log.d("InsertExpense", "Expense inserted: " + isSuccess);
        } else {
            // Update existing expense
            isSuccess = expenseDatabaseHelper.updateExpense(transactionId, amount, description, date, budgetName);
            Log.d("UpdateExpense", "Expense updated: " + isSuccess);
        }

        if (isSuccess) {
            Toast.makeText(this, transactionId == -1 ? "Expense added successfully!" : "Expense updated successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AddExpenseActivity.this, TransactionListActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, transactionId == -1 ? "Failed to add expense. Try again!" : "Failed to update expense. Try again!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadTransactionData(Intent intent) {
        double amount = intent.getDoubleExtra("amount", 0.0);
        String description = intent.getStringExtra("description");
        String date = intent.getStringExtra("date");
        String budgetName = intent.getStringExtra("budgetName");

        edtAmount.setText(String.valueOf(amount));
        edtDescription.setText(description);
        tvDate.setText(date);
        spinnerBudget.setSelection(getBudgetPosition(budgetName));
    }

    private int getBudgetPosition(String budgetName) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerBudget.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(budgetName)) {
                return i;
            }
        }
        return 0;
    }
}
