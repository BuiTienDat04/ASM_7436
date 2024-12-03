package com.example.asm.expenses;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asm.HomeFragment;
import com.example.asm.R;
import com.example.asm.adapter.TransactionAdapter;

import java.util.ArrayList;
import java.util.List;

public class TransactionListActivity extends AppCompatActivity {

    private ListView lvTransactions;
    private ExpenseDatabaseHelper dbHelper;
    private List<Transaction> transactionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);

        lvTransactions = findViewById(R.id.lvTransactions);
        dbHelper = new ExpenseDatabaseHelper(this);

        loadTransactions();


    }

    private void loadTransactions() {
        transactionList = new ArrayList<>();

        Cursor cursor = dbHelper.getAllTransactions();
        if (cursor == null) {
            Log.e("Database Error", "Cursor is null");
            return;
        }

        Log.d("Database", "Number of records: " + cursor.getCount());

        if (cursor.moveToFirst()) {
            do {
                try {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                    double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                    String budgetName = cursor.getString(cursor.getColumnIndexOrThrow("budget_name")); // This maps to budgetName

                    Log.d("TransactionData", "ID: " + id + ", Description: " + description + ", Amount: " + amount + ", Date: " + date + ", Budget Name: " + budgetName);

                    transactionList.add(new Transaction(id, description, amount, date, budgetName)); // This maps to budgetName
                } catch (Exception e) {
                    Log.e("Cursor Error", "Error reading data", e);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        TransactionAdapter adapter = new TransactionAdapter(this, transactionList);
        lvTransactions.setAdapter(adapter);

        Log.d("ExpenseList", "Loaded " + transactionList.size() + " transactions");
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTransactions();
    }


    public void onBackClick(View view) {
        Intent intent = new Intent(TransactionListActivity.this, AddExpenseActivity.class);
        startActivity(intent);
        finish();
    }// Close the current activity
}
