package com.example.asm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AccountService {
    private final AccountDb dbHelper;

    public AccountService(Context context) {
        dbHelper = new AccountDb(context);
    }

    public boolean addAccount(String username, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean isSuccess = false;

        try {
            ContentValues values = new ContentValues();
            values.put(AccountDb.COLUMN_USERNAME, username);
            values.put(AccountDb.COLUMN_PASSWORD, password);

            long result = db.insert(AccountDb.TABLE_NAME, null, values); // Thêm dữ liệu
            isSuccess = result != -1; // Thành công nếu kết quả khác -1
        } catch (Exception e) {
            e.printStackTrace(); // Debug lỗi
        } finally {
            db.close();
        }

        return isSuccess;
    }

    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        boolean isValid = false;

        try {
            String query = "SELECT 1 FROM " + AccountDb.TABLE_NAME +
                    " WHERE " + AccountDb.COLUMN_USERNAME + " = ? AND " + AccountDb.COLUMN_PASSWORD + " = ?";
            cursor = db.rawQuery(query, new String[]{username, password});
            isValid = cursor.moveToFirst();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return isValid;
    }

    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        boolean exists = false;

        try {
            String query = "SELECT 1 FROM " + AccountDb.TABLE_NAME + " WHERE " + AccountDb.COLUMN_USERNAME + " = ?";
            cursor = db.rawQuery(query, new String[]{username});
            exists = cursor.moveToFirst(); // Nếu có dòng trả về, tên người dùng tồn tại
        } catch (Exception e) {
            e.printStackTrace(); // In lỗi (tạm thời dùng để kiểm tra)
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return exists;
    }
}
