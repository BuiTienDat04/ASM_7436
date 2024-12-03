package com.example.asm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.asm.R;
import com.example.asm.model.BudgetItem;

import java.util.List;

public class BudgetAdapter extends ArrayAdapter<BudgetItem> {
    public BudgetAdapter(Context context, List<BudgetItem> budgetList) {
        super(context, 0, budgetList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_add_sudget, parent, false);
        }

        BudgetItem budgetItem = getItem(position);

        TextView tvGroupName = convertView.findViewById(R.id.edtAddGroupName);
        TextView tvAmount = convertView.findViewById(R.id.edtAmount);
        TextView tvDateRange = convertView.findViewById(R.id.tvDateRange);

        tvGroupName.setText(budgetItem.getGroupName());
        tvAmount.setText(budgetItem.getAmount());
        tvDateRange.setText(budgetItem.getDateRange());

        return convertView;
    }
}
