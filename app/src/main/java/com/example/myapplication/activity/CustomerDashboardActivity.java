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
import com.example.myapplication.adapter.FruitCustomerAdapter;
import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.model.Fruit;
import com.example.myapplication.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class CustomerDashboardActivity extends AppCompatActivity {
    private RecyclerView rvCustomerFruits;
    private EditText edtSearchCustomer;
    private Button btnViewHistory, btnLogout;
    private DatabaseHelper dbHelper;
    private SessionManager session;
    private FruitCustomerAdapter adapter;
    private List<Fruit> allFruits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        dbHelper = new DatabaseHelper(this);
        session = new SessionManager(this);

        rvCustomerFruits = findViewById(R.id.rvCustomerFruits);
        edtSearchCustomer = findViewById(R.id.edtSearchCustomer);
        btnViewHistory = findViewById(R.id.btnViewHistory);
        btnLogout = findViewById(R.id.btnLogoutCustomer);

        rvCustomerFruits.setLayoutManager(new LinearLayoutManager(this));

        // Bắt sự kiện tìm kiếm trái cây
        edtSearchCustomer.addTextChangedListener(new TextWatcher() {
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

        // CHUYỂN SANG MÀN HÌNH LỊCH SỬ ĐƠN HÀNG (Khách hàng)
        btnViewHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderListActivity.class);
            intent.putExtra("IS_ADMIN", false); // Báo cho OrderListActivity biết đây là Khách
            startActivity(intent);
        });
    }

    // Tự động load lại danh sách mỗi khi vào lại màn hình này
    @Override
    protected void onResume() {
        super.onResume();
        loadAvailableFruits();
    }

    private void loadAvailableFruits() {
        allFruits = dbHelper.getAllFruits();
        adapter = new FruitCustomerAdapter(this, allFruits);
        rvCustomerFruits.setAdapter(adapter);
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