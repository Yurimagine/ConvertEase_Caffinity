package com.example.unitconversion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UnitConversion.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_HISTORY = "history";
    public static final String COL_HISTORY_ID = "id";
    public static final String COL_CONVERSION_TYPE = "conversion_type";
    public static final String COL_INPUT_VALUE = "input_value";
    public static final String COL_INPUT_UNIT = "input_unit";
    public static final String COL_OUTPUT_VALUE = "output_value";
    public static final String COL_OUTPUT_UNIT = "output_unit";
    public static final String COL_DATE = "date";

    private static final String CREATE_TABLE_HISTORY =
            "CREATE TABLE " + TABLE_HISTORY + " (" +
                    COL_HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_CONVERSION_TYPE + " TEXT NOT NULL, " +
                    COL_INPUT_VALUE + " TEXT NOT NULL, " +
                    COL_INPUT_UNIT + " TEXT NOT NULL, " +
                    COL_OUTPUT_VALUE + " TEXT NOT NULL, " +
                    COL_OUTPUT_UNIT + " TEXT NOT NULL, " +
                    COL_DATE + " TEXT NOT NULL);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }

    // Updated addHistory method to accept separate units
    public boolean addHistory(String type,
                              String inputValue, String inputUnit,
                              String outputValue, String outputUnit,
                              String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_CONVERSION_TYPE, type);
        values.put(COL_INPUT_VALUE, inputValue);
        values.put(COL_INPUT_UNIT, inputUnit);
        values.put(COL_OUTPUT_VALUE, outputValue);
        values.put(COL_OUTPUT_UNIT, outputUnit);
        values.put(COL_DATE, date);

        long result = db.insert(TABLE_HISTORY, null, values);
        db.close();
        return result != -1;
    }

    public Cursor getAllHistory() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_HISTORY, null, null, null, null, null, COL_DATE + " DESC");
    }

    public boolean deleteHistory(int historyId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_HISTORY, COL_HISTORY_ID + "=?", new String[]{String.valueOf(historyId)});
        db.close();
        return result > 0;
    }

    public boolean clearAllHistory() {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_HISTORY, null, null);
        db.close();
        return result > 0;
    }

    public int getAllHistoryCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_HISTORY, null);
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return count;
    }
}
