package com.example.asm;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.asm.budget.AddBudgetActivity;

public class BudgetFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_budget, container, false);

        // Tìm Button và gắn sự kiện
        Button btnCreateBudget = view.findViewById(R.id.btnCreateBudget);
        btnCreateBudget.setOnClickListener(v -> {
            // Mở Activity AddBudgetActivity
            Intent intent = new Intent(getActivity(), AddBudgetActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
