package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.AdminFruitAdapter;
import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.model.Fruit;
import com.example.myapplication.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {
    private RecyclerView rvAdminFruits;
    private EditText edtSearchAdmin;
    private FloatingActionButton btnAddFruit;
    private Button btnLogout, btnViewOrders; // Đã thêm btnViewOrders
    private DatabaseHelper dbHelper;
    private SessionManager session;
    private List<Fruit> allFruits;
    private AdminFruitAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        dbHelper = new DatabaseHelper(this);
        session = new SessionManager(this);

        rvAdminFruits = findViewById(R.id.rvAdminFruits);
        edtSearchAdmin = findViewById(R.id.edtSearchAdmin);
        btnAddFruit = findViewById(R.id.btnAddFruit);
        btnLogout = findViewById(R.id.btnLogoutAdmin);

        // Nút xem toàn bộ đơn hàng (bạn cần đảm bảo đã có ID này trong XML)
        btnViewOrders = findViewById(R.id.btnViewOrders);

        rvAdminFruits.setLayoutManager(new LinearLayoutManager(this));

        // Bắt sự kiện tìm kiếm trái cây
        edtSearchAdmin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFruits(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Đăng xuất
        btnLogout.setOnClickListener(v -> {
            session.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // Mở màn hình Thêm Trái cây
        btnAddFruit.setOnClickListener(v -> startActivity(new Intent(this, AddEditFruitActivity.class)));

        // CHUYỂN SANG MÀN HÌNH QUẢN LÝ ĐƠN HÀNG (Admin)
        if (btnViewOrders != null) {
            btnViewOrders.setOnClickListener(v -> {
                Intent intent = new Intent(this, OrderListActivity.class);
                intent.putExtra("IS_ADMIN", true); // Báo cho OrderListActivity biết đây là Admin
                startActivity(intent);
            });
        }
    }

    // Tự động load lại danh sách khi quay về từ màn hình Add/Edit hoặc Order
    @Override
    protected void onResume() {
        super.onResume();
        loadFruits();
    }

    private void loadFruits() {
        allFruits = dbHelper.getAllFruits();
        adapter = new AdminFruitAdapter(this, allFruits);
        rvAdminFruits.setAdapter(adapter);
    }

    private void filterFruits(String query) {
        List<Fruit> filteredList = new ArrayList<>();
        for (Fruit f : allFruits) {
            if (f.getName().toLowerCase().contains(query.toLowerCase()) ||
                    f.getCategory().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(f);
            }
        }

        if (adapter != null) {
            adapter.updateList(filteredList);
        }
    }
}