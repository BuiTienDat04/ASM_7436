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
    private void handleIncomingData() {
        Intent intent = getIntent();
        oldGroupName = intent.getStringExtra("groupName");
        String editAmount = intent.getStringExtra("amount");
        String editStartDate = intent.getStringExtra("startDate");
        String editEndDate = intent.getStringExtra("endDate");

        if (oldGroupName != null) {
            isEditing = true; // Đang chỉnh sửa
            edtAddGroupName.setText(oldGroupName);

            // Kiểm tra và xử lý giá trị null
            etAmount.setText((editAmount != null ? editAmount : "0") + " VND");
            tvDateRange.setText("From " + (editStartDate != null ? editStartDate : "N/A") +
                    " - To " + (editEndDate != null ? editEndDate : "N/A"));

            // Gán giá trị cho biến startDate và endDate
            startDate = editStartDate != null ? editStartDate : null;
            endDate = editEndDate != null ? editEndDate : null;

            // Đổi tên nút lưu thành "Update"
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

            databaseHelper.addBudget(groupName, numericAmount, startDate, endDate);

            Toast.makeText(this, "Data saved to database!", Toast.LENGTH_SHORT).show();
            finish(); // Kết thúc Activity
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
     */
    private String getNumericAmount() {
        return etAmount.getText().toString().replace(" VND", "").replace(",", "").trim();
    }
}
