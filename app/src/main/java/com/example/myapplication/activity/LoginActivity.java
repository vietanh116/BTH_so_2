package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;
    private DatabaseHelper dbHelper;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);
        session = new SessionManager(this);

        // 1. Kiểm tra session, nếu đã đăng nhập trước đó thì bỏ qua form Login
        if (session.isLoggedIn()) {
            redirectBasedOnRole(session.getRole());
        }

        // 2. Ánh xạ các thành phần giao diện từ file activity_login.xml
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        // 3. Bắt sự kiện bấm nút Đăng nhập
        btnLogin.setOnClickListener(v -> performLogin());

        // 4. Bắt sự kiện dòng chữ chuyển sang trang Đăng ký (Register)
        tvGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void performLogin() {
        String user = edtUsername.getText().toString().trim();
        String pass = edtPassword.getText().toString().trim();

        // Kiểm tra xem người dùng có để trống thông tin không
        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra tài khoản trong SQLite Database
        String role = dbHelper.checkLogin(user, pass);

        if (!role.isEmpty()) { // Nếu tìm thấy Role (nghĩa là đăng nhập đúng)

            // Lưu trạng thái đăng nhập vào SharedPreferences
            session.createLoginSession(user, role);

            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

            // Điều hướng tới Dashboard tương ứng
            redirectBasedOnRole(role);
        } else {
            Toast.makeText(this, "Invalid username or password!", Toast.LENGTH_SHORT).show();
        }
    }

    private void redirectBasedOnRole(String role) {
        Intent intent;
        if ("Admin".equals(role)) {
            intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
        } else {
            intent = new Intent(LoginActivity.this, CustomerDashboardActivity.class);
        }
        startActivity(intent);
        finish(); // Đóng Activity Login để người dùng không bấm nút Back (Trở về) lại màn hình này được
    }
}