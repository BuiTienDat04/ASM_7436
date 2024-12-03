package com.example.asm.budget;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asm.R;

public class Group extends AppCompatActivity {
    LinearLayout layoutGroup;
    TextView edtGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sudget);

        // Ánh xạ các View từ layout
        layoutGroup = findViewById(R.id.layoutGroup);
        edtGroupName = findViewById(R.id.edtAddGroupName);

        // Đặt sự kiện onClick cho TextView "Nhập nhóm"
        edtGroupName.setOnClickListener(view -> {
            Log.d("GroupActivity", "TextView clicked!");
            showGroupMessage();
        });
    }

    private void showGroupMessage() {
        Toast.makeText(this, "Bạn đã nhấn vào Nhập nhóm", Toast.LENGTH_LONG).show();
    }
}
