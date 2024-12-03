package com.example.asm.expenses;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asm.MenuMainActivity;
import com.example.asm.R;
import com.example.asm.WalletFragment;
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
        edtAmount.addTextChangedListener(new TextWatcher() {
            private String currentText = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(currentText)) {
                    edtAmount.removeTextChangedListener(this);

                    // Xóa ký tự không cần thiết
                    String cleanString = s.toString().replaceAll("[^\\d]", ""); // Chỉ giữ lại số
                    if (!cleanString.isEmpty()) {
                        // Chuyển sang dạng có dấu phẩy và thêm " VND"
                        long parsed = Long.parseLong(cleanString);
                        String formatted = String.format("%,d VND", parsed);
                        currentText = formatted;
                        edtAmount.setText(formatted);
                        edtAmount.setSelection(formatted.length() - 4); // Đặt con trỏ trước "VND"
                    }

                    edtAmount.addTextChangedListener(this);
                }
            }
        });

        // Kiểm tra nếu đang chỉnh sửa giao dịch
        Intent intent = getIntent();
        transactionId = intent.getIntExtra("transactionId", -1);
        if (transactionId != -1) {
            loadTransactionData(intent);
        }
    }
    public void onBackClick(View view) {
        finish(); // Đóng Activity hiện tại và quay lại trang trước
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

    private double parseAmount(String amountText) throws NumberFormatException {
        if (amountText.isEmpty()) {
            throw new NumberFormatException("Amount is empty");
        }

        // Loại bỏ các ký tự không cần thiết
        amountText = amountText.replace(",", ""); // Bỏ dấu phẩy
        amountText = amountText.replace("VND", "").trim(); // Bỏ "VND" và khoảng trắng

        return Double.parseDouble(amountText);
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
            amount = parseAmount(amountText); // Chuẩn hóa số tiền
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount format! Example: '1,000 VND'", Toast.LENGTH_SHORT).show();
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

            // Chuyển sang MenuMainActivity và mở WalletFragment
            Intent intent = new Intent(AddExpenseActivity.this, MenuMainActivity.class);
            intent.putExtra("open_wallet", true); // Gửi tín hiệu để mở WalletFragment
            startActivity(intent);
            finish(); // Đóng Activity hiện tại
        } else {
            Toast.makeText(this, transactionId == -1 ? "Failed to add expense. Try again!" : "Failed to update expense. Try again!", Toast.LENGTH_SHORT).show();
        }

    }

    private void loadTransactionData(Intent intent) {
        double amount = intent.getDoubleExtra("amount", 0.0);
        String description = intent.getStringExtra("description");
        String date = intent.getStringExtra("date");
        String budgetName = intent.getStringExtra("budgetName");

        edtAmount.setText(String.format("%.2f VND", amount).replace(".00", "")); // Hiển thị định dạng có "VND"
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
