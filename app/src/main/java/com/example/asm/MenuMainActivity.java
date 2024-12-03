package com.example.asm;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.asm.adapter.ViewPageAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MenuMainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView  ;
    ViewPager2 viewPager2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_main);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        viewPager2 = findViewById(R.id.viewPage2);

        clickTabItemMenu();
        setupViewPager();

        // Kiểm tra nếu Intent yêu cầu mở WalletFragment
        if (getIntent().getBooleanExtra("open_wallet", false)) {
            // Chuyển ViewPager2 sang tab WalletFragment
            viewPager2.setCurrentItem(1); // Tab WalletFragment
            bottomNavigationView.setSelectedItemId(R.id.wallet_menu); // Đồng bộ với BottomNavigationView
        }
    }


    private void setupViewPager() {
        ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager2.setAdapter(adapter);
    }

    private void clickTabItemMenu() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home_menu){
                viewPager2.setCurrentItem(0);
            } else if (item.getItemId() == R.id.wallet_menu){
                viewPager2.setCurrentItem(1);
            } else if (item.getItemId() == R.id.budget_menu){
                viewPager2.setCurrentItem(2);
            } else if (item.getItemId() == R.id.account_menu) {
                viewPager2.setCurrentItem(3);
            } else {
                viewPager2.setCurrentItem(0);
            }
            return true;
        });
    }
}
