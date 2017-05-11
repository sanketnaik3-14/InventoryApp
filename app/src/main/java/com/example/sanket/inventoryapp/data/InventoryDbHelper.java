package com.example.sanket.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.sanket.inventoryapp.data.InventoryContract.InventoryEntry;

import static android.os.FileObserver.CREATE;

/**
 * Created by sanket on 07/05/17.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "inventory.db";

    public InventoryDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + InventoryEntry.TABLE_NAME + " ("
                + InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryEntry.PRODUCT_NAME + " TEXT NOT NULL, "
                + InventoryEntry.CURRENT_QUANTITY + " INTEGER DEFAULT 0, "
                + InventoryEntry.PRODUCT_PRICE + " INTEGER NOT NULL, "
                + InventoryEntry.PRODUCT_IMAGE + " BLOB, "
                + InventoryEntry.PRODUCT_SUPPLIER + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

