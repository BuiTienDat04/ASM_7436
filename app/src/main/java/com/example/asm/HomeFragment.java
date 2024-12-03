package com.example.asm;

import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.asm.adapter.CustomAdapter;
import com.example.asm.budget.BudgetDatabaseHelper;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private ListView listView;
    private TextView tvTotalAmount;
    private ArrayList<String> budgetList;
    private BudgetDatabaseHelper databaseHelper;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Ánh xạ View
        listView = view.findViewById(R.id.listView);
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);

        // Khởi tạo database helper
        databaseHelper = new BudgetDatabaseHelper(getContext());

        // Lấy dữ liệu từ SQLite
        loadBudgetsAndCalculateTotal();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cập nhật lại danh sách ngân sách mỗi khi fragment quay lại
        loadBudgetsAndCalculateTotal();
    }

    /**
     * Lấy dữ liệu từ SQLite và hiển thị danh sách + tính tổng số tiền
     */
    private void loadBudgetsAndCalculateTotal() {
        budgetList = new ArrayList<>();
        double totalAmount = 0;

        // Truy vấn dữ liệu từ SQLite
        Cursor cursor = databaseHelper.getAllBudgets();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String groupName = cursor.getString(cursor.getColumnIndex("group_name"));
                double amount = cursor.getDouble(cursor.getColumnIndex("amount"));
                String startDate = cursor.getString(cursor.getColumnIndex("start_date"));
                String endDate = cursor.getString(cursor.getColumnIndex("end_date"));

                // Tạo đối tượng Budget và thêm vào danh sách, thêm "VND" sau số tiền
                String formattedAmount = NumberFormat.getNumberInstance(Locale.US).format(amount) + " VND";
                String budgetData = id + " - " + groupName + " - " + formattedAmount + " - " + startDate + " to " + endDate;
                budgetList.add(budgetData);

                // Cộng dồn số tiền
                totalAmount += amount;
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Hiển thị danh sách bằng CustomAdapter
        CustomAdapter adapter = new CustomAdapter(getContext(), budgetList);
        listView.setAdapter(adapter);

        // Định dạng số tiền với dấu phẩy và thêm đơn vị VND cho tổng số tiền
        String formattedTotal = NumberFormat.getNumberInstance(Locale.US).format(totalAmount) + " VND";

        // Hiển thị tổng số tiền
        tvTotalAmount.setText("Total Amount: " + formattedTotal);
    }

}