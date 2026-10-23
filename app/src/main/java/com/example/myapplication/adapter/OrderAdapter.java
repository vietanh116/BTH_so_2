package com.example.myapplication.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.model.Order;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private Context context;
    private List<Order> orderList;
    private boolean isAdmin;
    private DatabaseHelper dbHelper;

    public OrderAdapter(Context context, List<Order> orderList, boolean isAdmin) {
        this.context = context;
        this.orderList = orderList;
        this.isAdmin = isAdmin;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.tvOrderId.setText("Order #" + order.getId());
        holder.tvOrderDate.setText("Date: " + order.getOrderDate());
        holder.tvOrderAmount.setText("Total: " + order.getTotalAmount() + " VND");
        holder.tvOrderStatus.setText("Status: " + order.getStatus());

        // Đổi màu chữ theo trạng thái đơn hàng cho trực quan
        if (order.getStatus().equals("Pending")) {
            holder.tvOrderStatus.setTextColor(Color.parseColor("#FF9800")); // Màu cam
        } else if (order.getStatus().equals("Confirmed")) {
            holder.tvOrderStatus.setTextColor(Color.parseColor("#2196F3")); // Màu xanh dương
        } else {
            holder.tvOrderStatus.setTextColor(Color.parseColor("#4CAF50")); // Màu xanh lá cây (Completed)
        }

        // Nếu là Admin, cho phép bấm vào item để cập nhật trạng thái
        if (isAdmin) {
            holder.itemView.setOnClickListener(v -> {
                String[] statuses = {"Pending", "Confirmed", "Completed"};
                new AlertDialog.Builder(context)
                        .setTitle("Update Status (Order #" + order.getId() + ")")
                        .setItems(statuses, (dialog, which) -> {
                            String newStatus = statuses[which];
                            // Cập nhật trạng thái vào SQLite
                            if (dbHelper.updateOrderStatus(order.getId(), newStatus)) {
                                order.setStatus(newStatus);
                                notifyItemChanged(position); // Cập nhật lại giao diện ngay lập tức
                                Toast.makeText(context, "Status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvOrderAmount, tvOrderStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderAmount = itemView.findViewById(R.id.tvOrderAmount);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
        }
    }
}