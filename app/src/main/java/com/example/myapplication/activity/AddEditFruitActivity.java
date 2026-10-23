package com.example.myapplication.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.database.DatabaseHelper;

public class AddEditFruitActivity extends AppCompatActivity {
    private EditText edtName, edtCategory, edtPrice, edtQuantity, edtExpiry, edtDesc;
    private Button btnSave;
    private TextView tvTitle;
    private DatabaseHelper dbHelper;
    private int fruitId = -1; // -1 nghĩa là chế độ Thêm mới

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_fruit);

        dbHelper = new DatabaseHelper(this);

        tvTitle = findViewById(R.id.tvTitle);
        edtName = findViewById(R.id.edtFruitName);
        edtCategory = findViewById(R.id.edtFruitCategory);
        edtPrice = findViewById(R.id.edtFruitPrice);
        edtQuantity = findViewById(R.id.edtFruitQuantity);
        edtExpiry = findViewById(R.id.edtFruitExpiry);
        edtDesc = findViewById(R.id.edtFruitDesc);
        btnSave = findViewById(R.id.btnSaveFruit);

        // Kiểm tra xem là Add hay Edit (Lấy dữ liệu từ Intent gửi tới)
        if (getIntent().hasExtra("FRUIT_ID")) {
            fruitId = getIntent().getIntExtra("FRUIT_ID", -1);
            tvTitle.setText("Edit Fruit");
            edtName.setText(getIntent().getStringExtra("FRUIT_NAME"));
            edtCategory.setText(getIntent().getStringExtra("FRUIT_CATEGORY"));
            edtPrice.setText(String.valueOf(getIntent().getDoubleExtra("FRUIT_PRICE", 0)));
            edtQuantity.setText(String.valueOf(getIntent().getIntExtra("FRUIT_QUANTITY", 0)));
            edtExpiry.setText(getIntent().getStringExtra("FRUIT_EXPIRY"));
            edtDesc.setText(getIntent().getStringExtra("FRUIT_DESC"));
        }

        btnSave.setOnClickListener(v -> saveFruit());
    }

    private void saveFruit() {
        String name = edtName.getText().toString().trim();
        String category = edtCategory.getText().toString().trim();
        String expiry = edtExpiry.getText().toString().trim();
        String desc = edtDesc.getText().toString().trim();
        String priceStr = edtPrice.getText().toString().trim();
        String qtyStr = edtQuantity.getText().toString().trim();

        if (name.isEmpty() || category.isEmpty() || expiry.isEmpty() || priceStr.isEmpty() || qtyStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        int quantity = Integer.parseInt(qtyStr);

        boolean isSuccess;
        if (fruitId == -1) {
            // Thêm mới
            isSuccess = dbHelper.addFruit(name, category, price, quantity, expiry, desc);
        } else {
            // Cập nhật
            isSuccess = dbHelper.updateFruit(fruitId, name, category, price, quantity, expiry, desc);
        }

        if (isSuccess) {
            Toast.makeText(this, "Saved successfully!", Toast.LENGTH_SHORT).show();
            finish(); // Đóng Activity và quay về Dashboard
        } else {
            Toast.makeText(this, "Failed to save fruit.", Toast.LENGTH_SHORT).show();
        }
    }
}