package com.example.myapplication.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.model.Fruit;
import com.example.myapplication.utils.SessionManager;

import java.util.List;

public class FruitCustomerAdapter extends RecyclerView.Adapter<FruitCustomerAdapter.ViewHolder> {
    private Context context;
    private List<Fruit> fruitList;
    private DatabaseHelper dbHelper;
    private SessionManager session;

    public FruitCustomerAdapter(Context context, List<Fruit> fruitList) {
        this.context = context;
        this.fruitList = fruitList;
        this.dbHelper = new DatabaseHelper(context);
        this.session = new SessionManager(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_fruit_customer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Fruit fruit = fruitList.get(position);

        holder.tvName.setText(fruit.getName());
        holder.tvPrice.setText(fruit.getPrice() + " VND");
        holder.tvExpiry.setText("HSD: " + fruit.getExpiryDate());

        // LOGIC YÊU CẦU: Ngăn khách đặt trái cây hết hạn hoặc hết hàng
        if (fruit.isExpired()) {
            holder.tvExpiry.setText("ĐÃ HẾT HẠN (" + fruit.getExpiryDate() + ")");
            holder.tvExpiry.setTextColor(Color.RED);
            holder.btnAddToCart.setEnabled(false);
            holder.btnAddToCart.setText("Hết hạn");
            holder.btnAddToCart.setBackgroundColor(Color.GRAY);
        } else if (fruit.getQuantity() <= 0) {
            holder.tvExpiry.setTextColor(Color.GRAY);
            holder.btnAddToCart.setEnabled(false);
            holder.btnAddToCart.setText("Hết hàng");
            holder.btnAddToCart.setBackgroundColor(Color.GRAY);
        } else {
            holder.tvExpiry.setTextColor(Color.parseColor("#4CAF50")); // Màu xanh lá
            holder.btnAddToCart.setEnabled(true);
            holder.btnAddToCart.setText("Order");

            // Xử lý khi khách bấm nút Đặt hàng (Order)
            holder.btnAddToCart.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Confirm Order")
                        .setMessage("Do you want to order " + fruit.getName() + " for " + fruit.getPrice() + " VND?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            String username = session.getUsername();
                            int customerId = dbHelper.getCustomerIdByUsername(username);

                            if(customerId != -1) {
                                // Tạo đơn hàng mới trong SQLite
                                boolean isSuccess = dbHelper.addOrder(customerId, fruit.getPrice());
                                if(isSuccess) {
                                    // (Tùy chọn) Trừ số lượng trái cây đi 1 sau khi mua
                                    int newQty = fruit.getQuantity() - 1;
                                    dbHelper.updateFruit(fruit.getId(), fruit.getName(), fruit.getCategory(), fruit.getPrice(), newQty, fruit.getExpiryDate(), fruit.getDescription());

                                    // Cập nhật lại giao diện ngay lập tức
                                    fruit.setQuantity(newQty);
                                    notifyItemChanged(position);

                                    Toast.makeText(context, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Failed to place order.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(context, "User error. Please login again.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return fruitList.size();
    }

    // Cập nhật khi tìm kiếm
    public void updateList(List<Fruit> newList) {
        fruitList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvExpiry;
        Button btnAddToCart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvFruitName);
            tvPrice = itemView.findViewById(R.id.tvFruitPrice);
            tvExpiry = itemView.findViewById(R.id.tvFruitExpiry);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}