package com.example.asm.budget;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asm.R;
import com.example.asm.adapter.CustomAdapter;

import java.util.ArrayList;

public class BudgetListActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<String> budgetList;
    Button btnBackToAddBudget;
    BudgetDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitty_sudget2);

        listView = findViewById(R.id.listView);
        btnBackToAddBudget = findViewById(R.id.btnBackToAddBudget);

        // Khởi tạo database helper
        databaseHelper = new BudgetDatabaseHelper(this);

        // Lấy dữ liệu từ SQLite và hiển thị
        loadBudgets();

        // Nút "Quay lại"
        btnBackToAddBudget.setOnClickListener(v -> {
            Intent intent = new Intent(BudgetListActivity.this, AddBudgetActivity.class);
            startActivity(intent);
            finish(); // Kết thúc BudgetListActivity
        });
    }

    /**
     * Lấy dữ liệu từ SQLite và hiển thị trong ListView
     */
    private void loadBudgets() {
        budgetList = new ArrayList<>();

        // Truy vấn dữ liệu từ SQLite
        Cursor cursor = databaseHelper.getAllBudgets();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String groupName = cursor.getString(cursor.getColumnIndex("group_name"));
                String amount = cursor.getString(cursor.getColumnIndex("amount"));
                String startDate = cursor.getString(cursor.getColumnIndex("start_date"));
                String endDate = cursor.getString(cursor.getColumnIndex("end_date"));

                // Thêm thông tin ngân sách kèm ID vào danh sách
                budgetList.add(id + " - " + groupName + " - " + amount + " - " + startDate + " to " + endDate);
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Thiết lập CustomAdapter cho ListView
        CustomAdapter adapter = new CustomAdapter(this, budgetList);
        listView.setAdapter(adapter);
    }
}
