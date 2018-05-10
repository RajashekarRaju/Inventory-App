package com.example.rkrjstdio.inventoryapp.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.rkrjstdio.inventoryapp.data.StockContract.StockEntry;

public class StockDbHelper extends SQLiteOpenHelper {

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "stock.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link StockDbHelper}.
     *
     * @param context of the app
     */
    public StockDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create a String that contains the SQL statement to create the stock table
        String SQL_CREATE_STOCK_TABLE = "CREATE TABLE " + StockContract.StockEntry.TABLE_NAME + " ("
                + StockEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + StockEntry.COLUMN_STOCK_PRODUCT_NAME + " TEXT NOT NULL, "
                + StockEntry.COLUMN_STOCK_PRODUCT_SUPPLIER + " TEXT NOT NULL, "
                + StockEntry.COLUMN_STOCK_IMAGE + " TEXT, "
                + StockEntry.COLUMN_STOCK_STOCK_AVAILABILITY + " TEXT NOT NULL, "
                + StockEntry.COLUMN_STOCK_SALE_DATA + " TEXT NOT NULL, "
                + StockEntry.COLUMN_STOCK_CUSTOMER_REVIEW + " TEXT NOT NULL, "
                + StockEntry.COLUMN_STOCK_CATEGORY + " TEXT NOT NULL, "
                + StockEntry.COLUMN_STOCK_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + StockEntry.COLUMN_STOCK_OFFER + " INTEGER NOT NULL DEFAULT 0,"
                + StockEntry.COLUMN_STOCK_RUPEES + " INTEGER NOT NULL DEFAULT 0);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_STOCK_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }

}