package com.example.asm.budget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asm.R;
import com.example.asm.expenses.AddExpenseActivity;
import com.example.asm.expenses.ExpenseDatabaseHelper;

import java.text.DecimalFormat;

public class ItemDetailActivity extends AppCompatActivity {

    private TextView tvCategory, tvAmount, tvStartDate, tvEndDate;
    private Button btnAddExpense, btnDelete, btnEdit, btnBack;
    private BudgetDatabaseHelper databaseHelper;
    private ExpenseDatabaseHelper expenseDatabaseHelper;
    private String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_detail_layout);

        // Khởi tạo cơ sở dữ liệu
        databaseHelper = new BudgetDatabaseHelper(this);
        expenseDatabaseHelper = new ExpenseDatabaseHelper(this);

        // Nhận dữ liệu từ Intent
        groupName = getIntent().getStringExtra("groupName");
        if (groupName == null || groupName.isEmpty()) {
            Toast.makeText(this, "No data received", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ các thành phần giao diện
        tvCategory = findViewById(R.id.tvCategory);
        tvAmount = findViewById(R.id.tvAmount);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        btnAddExpense = findViewById(R.id.btnAddExpense);
        btnDelete = findViewById(R.id.btnDelete);
        btnEdit = findViewById(R.id.btnEdit);
        btnBack = findViewById(R.id.btnBack);

        // Hiển thị dữ liệu ngân sách
        displayBudgetDetails();

        // Xử lý sự kiện các nút
        btnAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(ItemDetailActivity.this, AddExpenseActivity.class);
            startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> deleteBudget());

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(ItemDetailActivity.this, AddBudgetActivity.class);
            intent.putExtra("groupName", groupName);

            // Lấy chính xác số tiền
            String amount = tvAmount.getText().toString()
                    .replace("Amount: ", "") // Loại bỏ tiền tố
                    .replace(" VND", "")     // Loại bỏ hậu tố
                    .replace(",", "");       // Loại bỏ dấu phẩy
            intent.putExtra("amount", amount);

            // Truyền ngày bắt đầu và kết thúc
            String startDate = tvStartDate.getText().toString().replace("Start Date: ", "");
            String endDate = tvEndDate.getText().toString().replace("End Date: ", "");

            intent.putExtra("startDate", startDate);
            intent.putExtra("endDate", endDate);

            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> onBackPressed());
    }

    private void displayBudgetDetails() {
        Cursor cursor = databaseHelper.getBudgetByGroupName(groupName);
        if (cursor != null && cursor.moveToFirst()) {
            // Lấy dữ liệu từ cursor
            String amount = cursor.getString(cursor.getColumnIndex("amount"));
            String startDate = cursor.getString(cursor.getColumnIndex("start_date"));
            String endDate = cursor.getString(cursor.getColumnIndex("end_date"));

            // Định dạng số tiền
            try {
                double amountValue = Double.parseDouble(amount); // Chuyển số tiền từ chuỗi sang double
                DecimalFormat decimalFormat = new DecimalFormat("#,###.##"); // Định dạng với dấu phẩy
                amount = decimalFormat.format(amountValue) + " VND";
            } catch (NumberFormatException e) {
                amount = "Invalid Amount"; // Xử lý lỗi nếu không thể parse số tiền
            }

            // Hiển thị dữ liệu lên TextView
            tvCategory.setText("Category: " + groupName);
            tvAmount.setText("Amount: " + amount);
            tvStartDate.setText("Start Date: " + startDate);
            tvEndDate.setText("End Date: " + endDate);

            cursor.close();
        } else {
            Toast.makeText(this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteBudget() {
        if (budgetHasTransactions(groupName)) {
            Toast.makeText(this, "Cannot delete budget. There are still costs associated with this category..", Toast.LENGTH_SHORT).show();
        } else {
            boolean result = databaseHelper.deleteBudgetByGroupName(groupName);
            if (result) {
                Toast.makeText(this, "Budget deleted successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Delete failed budget.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean budgetHasTransactions(String budgetName) {
        Cursor cursor = expenseDatabaseHelper.getTransactionsByBudgetName(budgetName);
        boolean hasTransactions = cursor != null && cursor.moveToFirst();
        if (cursor != null) {
            cursor.close();
        }
        return hasTransactions;
    }
}

