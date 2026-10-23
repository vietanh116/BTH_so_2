package com.example.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapplication.model.Fruit;
import com.example.myapplication.model.Order;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "FruitShop.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // ==========================================
        // PHẦN CỦA ĐỒNG ĐỘI: KHỞI TẠO BẢNG
        // ==========================================

        String createTableAccount = "CREATE TABLE IF NOT EXISTS Account (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "role TEXT NOT NULL CHECK (role IN ('Admin', 'Customer')))";
        db.execSQL(createTableAccount);

        String createTableFruit = "CREATE TABLE IF NOT EXISTS Fruit (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "category TEXT NOT NULL, " +
                "price REAL NOT NULL CHECK (price >= 0), " +
                "quantity INTEGER NOT NULL DEFAULT 0 CHECK (quantity >= 0), " +
                "expiryDate TEXT NOT NULL, " +
                "description TEXT)";
        db.execSQL(createTableFruit);

        String createTableCustomer = "CREATE TABLE IF NOT EXISTS Customer (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "fullname TEXT NOT NULL, " +
                "phone TEXT, " +
                "email TEXT, " +
                "accountId INTEGER NOT NULL UNIQUE, " +
                "FOREIGN KEY (accountId) REFERENCES Account(id) ON DELETE CASCADE)";
        db.execSQL(createTableCustomer);

        String createTableOrder = "CREATE TABLE IF NOT EXISTS \"Order\" (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "customerId INTEGER NOT NULL, " +
                "orderDate TEXT NOT NULL, " +
                "totalAmount REAL NOT NULL DEFAULT 0 CHECK (totalAmount >= 0), " +
                "status TEXT NOT NULL DEFAULT 'Pending' CHECK (status IN ('Pending', 'Confirmed', 'Completed')), " +
                "FOREIGN KEY (customerId) REFERENCES Customer(id) ON DELETE RESTRICT)";
        db.execSQL(createTableOrder);

        // TẠO SẴN 1 TÀI KHOẢN ADMIN ĐỂ BẠN TEST
        db.execSQL("INSERT INTO Account(username, password, role) VALUES ('admin', 'admin123', 'Admin')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS \"Order\"");
        db.execSQL("DROP TABLE IF EXISTS Customer");
        db.execSQL("DROP TABLE IF EXISTS Fruit");
        db.execSQL("DROP TABLE IF EXISTS Account");
        onCreate(db);
    }

    // ==========================================
    // PHẦN LOGIC CHỨC NĂNG CỦA BẠN
    // ==========================================

    // 1. Đăng nhập
    public String checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String role = "";
        Cursor cursor = db.rawQuery("SELECT role FROM Account WHERE username = ? AND password = ?", new String[]{username, password});
        if (cursor.moveToFirst()) {
            role = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return role;
    }

    // 2. Thêm trái cây (Admin)
    public boolean addFruit(String name, String category, double price, int quantity, String expiryDate, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("category", category);
        values.put("price", price);
        values.put("quantity", quantity);
        values.put("expiryDate", expiryDate);
        values.put("description", description);
        long result = db.insert("Fruit", null, values);
        db.close();
        return result != -1;
    }

    // 3. Lấy tất cả trái cây (Sử dụng câu truy vấn ORDER BY name ASC của đồng đội)
    public List<Fruit> getAllFruits() {
        List<Fruit> fruitList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Fruit ORDER BY name ASC", null); // Đã update theo DB design
        if (cursor.moveToFirst()) {
            do {
                Fruit fruit = new Fruit();
                fruit.setId(cursor.getInt(0));
                fruit.setName(cursor.getString(1));
                fruit.setCategory(cursor.getString(2));
                fruit.setPrice(cursor.getDouble(3));
                fruit.setQuantity(cursor.getInt(4));
                fruit.setExpiryDate(cursor.getString(5));
                fruit.setDescription(cursor.getString(6));
                fruitList.add(fruit);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return fruitList;
    }

    // 4. Lấy trái cây cho Khách (Chỉ hiển thị còn hạn và còn hàng - Theo truy vấn của đồng đội[cite: 5.2])
    public List<Fruit> getAvailableFruitsForCustomer() {
        List<Fruit> fruitList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Fruit WHERE expiryDate >= date('now', 'localtime') AND quantity > 0 ORDER BY expiryDate ASC", null);
        if (cursor.moveToFirst()) {
            do {
                Fruit fruit = new Fruit();
                fruit.setId(cursor.getInt(0));
                fruit.setName(cursor.getString(1));
                fruit.setCategory(cursor.getString(2));
                fruit.setPrice(cursor.getDouble(3));
                fruit.setQuantity(cursor.getInt(4));
                fruit.setExpiryDate(cursor.getString(5));
                fruit.setDescription(cursor.getString(6));
                fruitList.add(fruit);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return fruitList;
    }

    // 5. Cập nhật trái cây (Admin)
    public boolean updateFruit(int id, String name, String category, double price, int quantity, String expiryDate, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("category", category);
        values.put("price", price);
        values.put("quantity", quantity);
        values.put("expiryDate", expiryDate);
        values.put("description", description);
        int result = db.update("Fruit", values, "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    // 6. Xóa trái cây (Admin)
    public boolean deleteFruit(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("Fruit", "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    // 7. Thêm Đơn hàng (Khách hàng)
    public boolean addOrder(int customerId, double totalAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("customerId", customerId);
        values.put("orderDate", "date('now', 'localtime')"); // Lấy ngày giờ hiện tại
        values.put("totalAmount", totalAmount);
        values.put("status", "Pending"); // Mặc định Pending theo DB[cite: 5.2]
        long result = db.insert("\"Order\"", null, values);
        db.close();
        return result != -1;
    }

    // 8. Cập nhật trạng thái đơn hàng (Admin)
    public boolean updateOrderStatus(int orderId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", newStatus);
        int result = db.update("\"Order\"", values, "id = ?", new String[]{String.valueOf(orderId)});
        db.close();
        return result > 0;
    }

    // 9. Đăng ký tài khoản mới (Customer)
    public long registerAccount(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        values.put("role", "Customer"); // Form đăng ký mặc định là khách hàng
        long accountId = db.insert("Account", null, values);
        db.close();
        return accountId;
    }

    // 10. Thêm thông tin chi tiết Khách hàng sau khi đăng ký
    public boolean addCustomerInfo(String fullname, String phone, String email, long accountId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fullname", fullname);
        values.put("phone", phone);
        values.put("email", email);
        values.put("accountId", accountId);
        long result = db.insert("Customer", null, values);
        db.close();
        return result != -1;
    }
    // 11. Lấy ID của Customer đang đăng nhập
    public int getCustomerIdByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        int customerId = -1;
        // Kết hợp 2 bảng Customer và Account để tìm ID
        String query = "SELECT Customer.id FROM Customer INNER JOIN Account ON Customer.accountId = Account.id WHERE Account.username = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});
        if (cursor.moveToFirst()) {
            customerId = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return customerId;
    }
    // 12. Lấy lịch sử đơn hàng của MỘT Khách hàng
    public List<Order> getOrdersByCustomer(int customerId) {
        List<Order> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM \"Order\" WHERE customerId = ? ORDER BY orderDate DESC", new String[]{String.valueOf(customerId)});
        if (cursor.moveToFirst()) {
            do {
                Order order = new Order();
                order.setId(cursor.getInt(0));
                order.setCustomerId(cursor.getInt(1));
                order.setOrderDate(cursor.getString(2));
                order.setTotalAmount(cursor.getDouble(3));
                order.setStatus(cursor.getString(4));
                list.add(order);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // 13. Lấy TOÀN BỘ đơn hàng (Dành cho Admin)
    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM \"Order\" ORDER BY orderDate DESC", null);
        if (cursor.moveToFirst()) {
            do {
                Order order = new Order();
                order.setId(cursor.getInt(0));
                order.setCustomerId(cursor.getInt(1));
                order.setOrderDate(cursor.getString(2));
                order.setTotalAmount(cursor.getDouble(3));
                order.setStatus(cursor.getString(4));
                list.add(order);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }
}