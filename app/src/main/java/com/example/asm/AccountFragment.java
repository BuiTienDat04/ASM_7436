package com.example.asm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class AccountFragment extends Fragment {

    private Button btnLogout;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Ánh xạ nút Logout
        btnLogout = view.findViewById(R.id.btn_logout);

        // Xử lý sự kiện nút Logout
        btnLogout.setOnClickListener(v -> {
            // Xóa thông tin phiên làm việc
            SharedPreferences preferences = requireContext().getSharedPreferences("UserSession", getContext().MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear(); // Xóa toàn bộ dữ liệu phiên làm việc
            editor.apply();

            // Chuyển đến màn hình LoginActivity
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa các Activity trước đó
            startActivity(intent);

            // Kết thúc Fragment hoặc Activity hiện tại
            getActivity().finish();
        });

        return view;
    }
}