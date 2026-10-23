package com.example.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.myapplication.activity.AddEditFruitActivity;
import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.model.Fruit;

import java.util.List;

public class AdminFruitAdapter extends RecyclerView.Adapter<AdminFruitAdapter.ViewHolder> {
    private Context context;
    private List<Fruit> fruitList;
    private DatabaseHelper dbHelper;

    public AdminFruitAdapter(Context context, List<Fruit> fruitList) {
        this.context = context;
        this.fruitList = fruitList;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_fruit_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Fruit fruit = fruitList.get(position);

        holder.tvName.setText(fruit.getName() + " (" + fruit.getCategory() + ")");
        holder.tvPrice.setText(fruit.getPrice() + " VND | SL: " + fruit.getQuantity());
        holder.tvExpiry.setText("HSD: " + fruit.getExpiryDate());

        // Xử lý nút Xóa
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to delete " + fruit.getName() + "?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (dbHelper.deleteFruit(fruit.getId())) {
                            fruitList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, fruitList.size());
                            Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        // Xử lý nút Sửa (Chuyển sang màn hình AddEditFruitActivity)
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddEditFruitActivity.class);
            intent.putExtra("FRUIT_ID", fruit.getId());
            intent.putExtra("FRUIT_NAME", fruit.getName());
            intent.putExtra("FRUIT_CATEGORY", fruit.getCategory());
            intent.putExtra("FRUIT_PRICE", fruit.getPrice());
            intent.putExtra("FRUIT_QUANTITY", fruit.getQuantity());
            intent.putExtra("FRUIT_EXPIRY", fruit.getExpiryDate());
            intent.putExtra("FRUIT_DESC", fruit.getDescription());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return fruitList.size();
    }

    // Hàm cập nhật danh sách khi tìm kiếm
    public void updateList(List<Fruit> newList) {
        fruitList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvExpiry;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvFruitNameAdmin);
            tvPrice = itemView.findViewById(R.id.tvFruitPriceAdmin);
            tvExpiry = itemView.findViewById(R.id.tvFruitExpiryAdmin);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}