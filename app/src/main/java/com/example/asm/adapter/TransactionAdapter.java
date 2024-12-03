package com.example.asm.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.asm.R;
import com.example.asm.expenses.Transaction;
import com.example.asm.expenses.ExpenseDatabaseHelper;
import com.example.asm.expenses.AddExpenseActivity;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends ArrayAdapter<Transaction> {

    private Context context;
    private List<Transaction> transactions;
    private ExpenseDatabaseHelper dbHelper;

    public TransactionAdapter(Context context, List<Transaction> transactions) {
        super(context, 0, transactions);
        this.context = context;
        this.transactions = transactions;
        dbHelper = new ExpenseDatabaseHelper(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.transaction_list_item, parent, false);
        }

        Transaction transaction = transactions.get(position);

        TextView descriptionTextView = convertView.findViewById(R.id.tvDescription);
        TextView amountTextView = convertView.findViewById(R.id.tvAmount);
        TextView dateTextView = convertView.findViewById(R.id.tvDate);
        TextView categoryTextView = convertView.findViewById(R.id.tvCategory); // This maps to budgetName
        Button btnEdit = convertView.findViewById(R.id.btnEdit);
        Button btnDelete = convertView.findViewById(R.id.btnDelete);

        descriptionTextView.setText(transaction.getDescription());
        amountTextView.setText(formatCurrency(transaction.getAmount()));
        dateTextView.setText(transaction.getDate());
        categoryTextView.setText(transaction.getBudgetName()); // This maps to budgetName

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddExpenseActivity.class);
            intent.putExtra("transactionId", transaction.getId());
            intent.putExtra("amount", transaction.getAmount());
            intent.putExtra("description", transaction.getDescription());
            intent.putExtra("date", transaction.getDate());
            intent.putExtra("budgetName", transaction.getBudgetName()); // This maps to budgetName
            context.startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> {
            dbHelper.deleteExpense(transaction.getId());
            transactions.remove(position);
            notifyDataSetChanged();
        });

        return convertView;
    }

    private String formatCurrency(double amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        format.setCurrency(Currency.getInstance("VND"));
        return format.format(amount);
    }
}
