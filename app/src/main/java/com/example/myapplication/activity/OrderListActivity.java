package com.example.myapplication.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.OrderAdapter;
import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.model.Order;
import com.example.myapplication.utils.SessionManager;

import java.util.List;

public class OrderListActivity extends AppCompatActivity {
    private RecyclerView rvOrders;
    private TextView tvOrderTitle;
    private DatabaseHelper dbHelper;
    private SessionManager session;
    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        dbHelper = new DatabaseHelper(this);
        session = new SessionManager(this);

        rvOrders = findViewById(R.id.rvOrders);
        tvOrderTitle = findViewById(R.id.tvOrderTitle);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));

        // Nhận cờ IS_ADMIN từ Intent chuyển sang để phân quyền hiển thị
        isAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);

        List<Order> orderList;

        if (isAdmin) {
            // Dành cho Admin: Lấy và quản lý toàn bộ đơn hàng
            tvOrderTitle.setText("Manage All Orders");
            orderList = dbHelper.getAllOrders();
        } else {
            // Dành cho Khách hàng: Lấy lịch sử mua hàng của chính họ
            tvOrderTitle.setText("My Order History");
            String username = session.getUsername();
            int customerId = dbHelper.getCustomerIdByUsername(username);
            orderList = dbHelper.getOrdersByCustomer(customerId);
        }

        // Gắn danh sách vào Adapter
        OrderAdapter adapter = new OrderAdapter(this, orderList, isAdmin);
        rvOrders.setAdapter(adapter);
    }
}