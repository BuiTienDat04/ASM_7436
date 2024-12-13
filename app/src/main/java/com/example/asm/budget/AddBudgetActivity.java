package com.example.asm.budget;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.asm.R;
import com.example.asm.expenses.ExpenseDatabaseHelper;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

public class AddBudgetActivity extends AppCompatActivity {
    private EditText edtAddGroupName, etAmount;
    private TextView tvDateRange, tvCancel;
    private Button btnSave;
    private String startDate, endDate, oldGroupName;
    private Calendar startCalendar, endCalendar;
    private final NumberFormat numberFormat = new DecimalFormat("#,###");
    private BudgetDatabaseHelper databaseHelper;
    private boolean isEditing = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sudget);

        // Ánh xạ các thành phần giao diện
        edtAddGroupName = findViewById(R.id.edtAddGroupName);
        etAmount = findViewById(R.id.edtAmount);
        tvDateRange = findViewById(R.id.tvDateRange);
        btnSave = findViewById(R.id.btnSave);
        tvCancel = findViewById(R.id.tvCancel);

        // Khởi tạo database helper
        databaseHelper = new BudgetDatabaseHelper(this);

        // Khởi tạo Calendar cho ngày bắt đầu và ngày kết thúc
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();

        // Nhận dữ liệu nếu đang chỉnh sửa
        handleIncomingData();

        // Xử lý sự kiện nút Cancel
        tvCancel.setOnClickListener(view -> finish());

        // Xử lý chọn ngày
        tvDateRange.setOnClickListener(view -> selectDateRange());

        // Ngăn nhập số âm vào trường số tiền
        etAmount.setFilters(new InputFilter[]{(source, start, end, dest, dstart, dend) -> {
            if (source.toString().contains("-")) return "";
            return null;
        }});

        // Đảm bảo định dạng số tiền
        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                etAmount.removeTextChangedListener(this);
                String input = s.toString().replace(",", "").replace(" VND", "").trim();
                if (!input.isEmpty()) {
                    try {

                        long value = Long.parseLong(input);
                        String formatted = numberFormat.format(value) + " VND";
                        etAmount.setText(formatted);
                        etAmount.setSelection(formatted.length() - 4);
                    } catch (NumberFormatException e) {
                        etAmount.setError("Invalid number");
                    }
                }
                etAmount.addTextChangedListener(this);
            }
        });

        // Xử lý lưu dữ liệu
        btnSave.setOnClickListener(view -> {
            if (isEditing) {
                updateBudgetData();
            } else {
                saveBudgetData();
            }
        });
    }

    /**
     * Xử lý dữ liệu khi chuyển sang chế độ chỉnh sửa
     */
    /**
     * Xử lý dữ liệu khi chuyển sang chế độ chỉnh sửa
     */
    private void handleIncomingData() {
        Intent intent = getIntent();
        oldGroupName = intent.getStringExtra("groupName");
        String editAmount = intent.getStringExtra("amount"); // Nhận số tiền từ Intent
        String editStartDate = intent.getStringExtra("startDate");
        String editEndDate = intent.getStringExtra("endDate");

        if (oldGroupName != null) {
            isEditing = true; // Chế độ chỉnh sửa
            edtAddGroupName.setText(oldGroupName);

            // Kiểm tra và hiển thị số tiền
            if (editAmount != null && !editAmount.isEmpty()) {
                try {
                    long amount = Long.parseLong(editAmount); // Chuyển thành số
                    etAmount.setText(numberFormat.format(amount) + " VND"); // Hiển thị có định dạng
                } catch (NumberFormatException e) {
                    etAmount.setText("0 VND"); // Giá trị mặc định khi lỗi
                }
            } else {
                etAmount.setText("0 VND"); // Giá trị mặc định khi không có số tiền
            }

            // Gán ngày tháng
            tvDateRange.setText("From " + (editStartDate != null ? editStartDate : "N/A") +
                    " - To " + (editEndDate != null ? editEndDate : "N/A"));

            startDate = editStartDate;
            endDate = editEndDate;

            // Đổi tên nút Lưu thành Cập nhật
            btnSave.setText("Update");
        }
    }




    /**
     * Chọn khoảng thời gian
     */
    private void selectDateRange() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog startDatePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    startCalendar.set(year, month, dayOfMonth);
                    startDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                    selectEndDate();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        startDatePickerDialog.setTitle("Select Start Date");
        startDatePickerDialog.show();
    }

    private void selectEndDate() {
        DatePickerDialog endDatePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    endCalendar.set(year, month, dayOfMonth);
                    if (endCalendar.before(startCalendar)) {
                        Toast.makeText(this, "End date must be after start date", Toast.LENGTH_SHORT).show();
                    } else {
                        endDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        tvDateRange.setText("From " + startDate + " - To " + endDate);
                    }
                },
                startCalendar.get(Calendar.YEAR),
                startCalendar.get(Calendar.MONTH),
                startCalendar.get(Calendar.DAY_OF_MONTH)
        );
        endDatePickerDialog.setTitle("Select End Date");
        endDatePickerDialog.show();
    }

    /**
     * Lưu dữ liệu mới
     */
    private void saveBudgetData() {
        if (validateInputs()) {
            String groupName = edtAddGroupName.getText().toString().trim();
            String numericAmount = getNumericAmount();

            // Kiểm tra tên danh mục có tồn tại không
            if (databaseHelper.isBudgetGroupExist(groupName)) {
                Toast.makeText(this, "Category name already exists, please choose another name!", Toast.LENGTH_SHORT).show();
            } else {
                // Thêm mới nếu không bị trùng
                databaseHelper.addBudget(groupName, numericAmount, startDate, endDate);
                Toast.makeText(this, "Data saved!", Toast.LENGTH_SHORT).show();

                if (validateInputs()) {
                    databaseHelper.addBudget(groupName, numericAmount, startDate, endDate);
                    Toast.makeText(this, "Data saved to database!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                finish();
            }
        }
    }


    /**
     * Cập nhật dữ liệu đã tồn tại
     */
    private void updateBudgetData() {
        if (validateInputs()) {
            String newGroupName = edtAddGroupName.getText().toString().trim();
            String numericAmount = getNumericAmount();

            boolean result = databaseHelper.updateBudget(oldGroupName, newGroupName, numericAmount, startDate, endDate);

            if (result) {
                Toast.makeText(this, "Budget updated successfully!", Toast.LENGTH_SHORT).show();

                if (validateInputs()) {

                    // Tạo một thể hiện của ExpenseDatabaseHelper
                    ExpenseDatabaseHelper expenseDatabaseHelper = new ExpenseDatabaseHelper(this);

                    // Cập nhật tên danh mục cho tất cả các chi tiêu liên quan
                    if (result) {
                        boolean expenseUpdateResult = expenseDatabaseHelper.updateExpensesByBudgetName(oldGroupName, newGroupName);
                        if (expenseUpdateResult) {
                            Toast.makeText(this, "All related expenses updated successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Some expenses could not be updated.", Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(this, "Budget updated successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to update budget. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                }
                finish();
            } else {
                Toast.makeText(this, "Failed to update budget", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Kiểm tra dữ liệu đầu vào
     */
    private boolean validateInputs() {
        String groupName = edtAddGroupName.getText().toString().trim();
        String amount = etAmount.getText().toString().trim();

        if (TextUtils.isEmpty(groupName)) {
            edtAddGroupName.setError("Group name cannot be blank");
            return false;
        }
        if (TextUtils.isEmpty(amount) || !amount.endsWith(" VND")) {
            etAmount.setError("Amount cannot be blank and must include VND");
            return false;
        }
        if (startDate == null || endDate == null) {
            Toast.makeText(this, "Please select a time period!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

/**
 * Lấy số tiền dạng số
 */private String getNumericAmount() {
    return etAmount.getText().toString().replace(" VND", "").replace(",", "").trim();
}
}