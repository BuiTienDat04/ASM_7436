package com.example.asm.budget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BudgetDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "budget.db";
    private static final int DATABASE_VERSION = 1;

    // Table for budgets
    private static final String TABLE_BUDGET = "budget";
    private static final String COLUMN_BUDGET_ID = "id";
    private static final String COLUMN_GROUP_NAME = "group_name";
    private static final String COLUMN_BUDGET_AMOUNT = "amount";
    private static final String COLUMN_START_DATE = "start_date";
    private static final String COLUMN_END_DATE = "end_date";

    public BudgetDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Budget table
        String CREATE_TABLE_BUDGET = "CREATE TABLE " + TABLE_BUDGET + " (" +
                COLUMN_BUDGET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_GROUP_NAME + " TEXT NOT NULL UNIQUE, " +
                COLUMN_BUDGET_AMOUNT + " REAL NOT NULL, " +
                COLUMN_START_DATE + " TEXT NOT NULL, " +
                COLUMN_END_DATE + " TEXT NOT NULL)";
        db.execSQL(CREATE_TABLE_BUDGET);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGET);
        onCreate(db);
    }

    // Methods for Budget table
    public boolean addBudget(String groupName, String amount, String startDate, String endDate) {
        if (isBudgetGroupExist(groupName)) {
            return false; // Avoid adding duplicate budget groups
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GROUP_NAME, groupName);
        values.put(COLUMN_BUDGET_AMOUNT, amount);
        values.put(COLUMN_START_DATE, startDate);
        values.put(COLUMN_END_DATE, endDate);

        long result = db.insert(TABLE_BUDGET, null, values);
        db.close();

        return result != -1;
    }

    public boolean updateBudget(String oldGroupName, String newGroupName, String amount, String startDate, String endDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GROUP_NAME, newGroupName);
        values.put(COLUMN_BUDGET_AMOUNT, amount);
        values.put(COLUMN_START_DATE, startDate);
        values.put(COLUMN_END_DATE, endDate);

        int rows = db.update(TABLE_BUDGET, values, COLUMN_GROUP_NAME + "=?", new String[]{oldGroupName});
        db.close();
        return rows > 0;
    }

    public Cursor getBudgetByGroupName(String groupName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BUDGET + " WHERE " + COLUMN_GROUP_NAME + "=?";
        return db.rawQuery(query, new String[]{groupName});
    }

    public boolean deleteBudgetByGroupName(String groupName) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_BUDGET, COLUMN_GROUP_NAME + " = ?", new String[]{groupName});
        db.close();
        return rowsAffected > 0;
    }

    public Cursor getAllBudgets() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BUDGET;
        return db.rawQuery(query, null);
    }

    // Get all Budget groups (group names)
    public Cursor getAllBudgetGroups() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_GROUP_NAME + " FROM " + TABLE_BUDGET;
        return db.rawQuery(query, null);
    }

    // Check if a budget group exists
    public boolean isBudgetGroupExist(String groupName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT 1 FROM " + TABLE_BUDGET + " WHERE " + COLUMN_GROUP_NAME + "=?";
            cursor = db.rawQuery(query, new String[]{groupName});
            return cursor.moveToFirst(); // Nếu có dữ liệu, trả về true
        } finally {
            if (cursor != null) {
                cursor.close(); // Đóng Cursor
            }
        }
    }

    public Cursor getTransactionsByBudgetName(String budgetName) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM transactions WHERE budget_name = ?", new String[]{budgetName}); }
}
