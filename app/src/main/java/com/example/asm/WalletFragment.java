package com.example.asm;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.asm.adapter.TransactionAdapter;
import com.example.asm.expenses.ExpenseDatabaseHelper;
import com.example.asm.expenses.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WalletFragment extends Fragment {

    private ListView lvTransactions;
    private ExpenseDatabaseHelper dbHelper;
    private List<Transaction> transactionList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        lvTransactions = view.findViewById(R.id.lvTransactions);
        dbHelper = new ExpenseDatabaseHelper(requireContext());

        loadTransactions();

        return view;
    }

    private void loadTransactions() {
        transactionList = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = dbHelper.getAllTransactions();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                    double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                    String budgetName = cursor.getString(cursor.getColumnIndexOrThrow("budget_name"));

                    // Định dạng ngày theo định dạng "dd/MM/yyyy"
                    String formattedDate = formatDate(date);

                    // Log dữ liệu để kiểm tra
                    Log.d("WalletFragment", "ID: " + id + ", Description: " + description +
                            ", Amount: " + amount + ", Date: " + formattedDate + ", Budget: " + budgetName);

                    // Lưu dữ liệu dưới dạng double cho amount và định dạng ngày
                    transactionList.add(new Transaction(id, description, amount, formattedDate, budgetName));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("WalletFragment", "Error loading transactions", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        TransactionAdapter adapter = new TransactionAdapter(requireContext(), transactionList);
        lvTransactions.setAdapter(adapter);

        Log.d("WalletFragment", "Loaded " + transactionList.size() + " transactions");
    }

    // Định dạng lại ngày theo định dạng "dd/MM/yyyy"
    private String formatDate(String date) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // Giả sử ngày gốc là "yyyy-MM-dd"
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()); // Định dạng "dd/MM/yyyy"
            Date parsedDate = inputFormat.parse(date);
            return outputFormat.format(parsedDate);
        } catch (Exception e) {
            e.printStackTrace();
            return date; // Nếu có lỗi, trả về ngày gốc
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTransactions(); // Làm mới danh sách giao dịch khi quay lại Fragment
    }
}

