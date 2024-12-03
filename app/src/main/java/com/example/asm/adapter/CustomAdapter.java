package com.example.asm.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.asm.R;
import com.example.asm.budget.ItemDetailActivity;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> itemList;

    // Constructor
    public CustomAdapter(Context context, List<String> itemList) {
        super(context, 0, itemList);
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate layout nếu chưa tồn tại
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_with_delete, parent, false);
        }

        // Tham chiếu các view trong item layout
        TextView tvItemData = convertView.findViewById(R.id.tvItemData);

        // Hiển thị dữ liệu cho từng item
        String itemData = itemList.get(position);
        tvItemData.setText(itemData);

        // Xử lý khi nhấn vào toàn bộ item để mở ItemDetailActivity
        // Trong CustomAdapter.java
        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItemDetailActivity.class);
            intent.putExtra("groupName", itemData.split(" - ")[1]); // Giả sử groupName là phần tử thứ 2 trong chuỗi
            context.startActivity(intent);
        });



        return convertView;
    }
}
