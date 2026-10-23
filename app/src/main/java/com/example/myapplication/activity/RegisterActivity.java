package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.database.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtFullName, edtEmail, edtPhone, edtPassword, edtConfirmPassword;
    private Button btnRegister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Đảm bảo tên file layout đúng với tên bạn đã tạo (vd: activity_register.xml)
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        // Ánh xạ các UI elements từ file XML
        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);

        // Xử lý sự kiện khi bấm nút Đăng ký
        btnRegister.setOnClickListener(v -> performRegistration());
    }

    private void performRegistration() {
        String fullName = edtFullName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        // 1. Kiểm tra Validate dữ liệu đầu vào
        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Trong bài toán này, ta dùng Email hoặc Phone làm Username cho bảng Account.
        // Ở đây tôi chọn Email làm Username để đảm bảo tính duy nhất (UNIQUE) theo thiết kế DB.
        String username = email;

        // 2. Lưu vào bảng Account trước (để lấy accountId)
        long accountId = dbHelper.registerAccount(username, password);

        if (accountId != -1) { // -1 nghĩa là lỗi (ví dụ: email đã tồn tại do ràng buộc UNIQUE)
            // 3. Nếu tạo Account thành công, lưu tiếp vào bảng Customer
            boolean isCustomerCreated = dbHelper.addCustomerInfo(fullName, phone, email, accountId);

            if (isCustomerCreated) {
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                // Chuyển về trang Đăng nhập sau khi đăng ký thành công
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Đóng trang Đăng ký
            } else {
                Toast.makeText(this, "Failed to create customer profile.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Email is already registered!", Toast.LENGTH_SHORT).show();
        }
    }
}