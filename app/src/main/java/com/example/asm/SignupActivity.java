package com.example.asm;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {
    TextView tvLogin;
    EditText edtUsername, edtPassword;
    Button btnSignup, btnCancel;

    private AccountService accountService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        accountService = new AccountService(this);

        tvLogin = findViewById(R.id.tvLogin);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignup = findViewById(R.id.btnSignup);
        btnCancel = findViewById(R.id.btnCancel);

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if (TextUtils.isEmpty(user)) {
                    edtUsername.setError("Username cannot be empty");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    edtPassword.setError("Password cannot be empty");
                    return;
                }

                if (accountService.isUsernameExists(user)) {
                    Toast.makeText(SignupActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isAdded = accountService.addAccount(user, password);
                if (isAdded) {
                    Toast.makeText(SignupActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                    edtUsername.setText("");
                    edtPassword.setText("");
                    new android.os.Handler().postDelayed(() -> {
                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                        finish();
                    }, 1500);
                } else {
                    Toast.makeText(SignupActivity.this, "Failed to create account", Toast.LENGTH_SHORT).show();
                }
            }
        });



        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
