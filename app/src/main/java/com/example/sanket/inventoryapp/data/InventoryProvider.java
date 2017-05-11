package com.example.sanket.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.example.sanket.inventoryapp.data.InventoryContract.InventoryEntry;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by sanket on 07/05/17.
 */

public class InventoryProvider extends ContentProvider {

    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    private static final int INVENTORY = 100;
    private static final int INVENTORY_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static
    {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, INVENTORY);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", INVENTORY_ID);
    }

    private InventoryDbHelper mDbHelper;
    @Override
    public boolean onCreate() {

        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);

        switch(match)
        {
            case INVENTORY:
                cursor = db.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case INVENTORY_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri)) };
                cursor = db.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot Resolve URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch(match)
        {
            case INVENTORY:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case INVENTORY_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + "with match " + match);
        }

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final int match = sUriMatcher.match(uri);
        switch(match)
        {
            case INVENTORY:
                return insertInventory(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }

    }

    private Uri insertInventory(Uri uri, ContentValues values)
    {
        String productName = values.getAsString(InventoryEntry.PRODUCT_NAME);
        if(productName == null)
        {
            throw new IllegalArgumentException("Item requires a name");
        }

        String productSupplier = values.getAsString(InventoryEntry.PRODUCT_SUPPLIER);
        if(productSupplier == null)
        {
            throw new IllegalArgumentException("Item requires a supplier");
        }

        Integer productQuantity = values.getAsInteger(InventoryEntry.CURRENT_QUANTITY);
        if(productQuantity != null && productQuantity < 0)
        {
            throw new IllegalArgumentException("Product requires a valid weight");
        }

        Integer productPrice = values.getAsInteger(InventoryEntry.PRODUCT_PRICE);
        if(productPrice != null && productPrice < 0)
        {
            throw new IllegalArgumentException("Product requires a valid price");
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insert(InventoryEntry.TABLE_NAME, null, values);

        if(id == -1)
        {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);

        switch(match)
        {
            case INVENTORY:
                rowsDeleted = db.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INVENTORY_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                rowsDeleted = db.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri " + uri + "with match " + match);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);

        switch(match)
        {
            case INVENTORY:
                return updateInventory(uri, values, selection, selectionArgs);

            case INVENTORY_ID:

                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};

                return updateInventory(uri, values, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }

    private int updateInventory(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {

        String productName = values.getAsString(InventoryEntry.PRODUCT_NAME);
        if(productName == null)
        {
            throw new IllegalArgumentException("Item requires a name");
        }

        String productSupplier = values.getAsString(InventoryEntry.PRODUCT_SUPPLIER);
        if(productSupplier == null)
        {
            throw new IllegalArgumentException("Item requires a supplier");
        }

        Integer productQuantity = values.getAsInteger(InventoryEntry.CURRENT_QUANTITY);
        if(productQuantity != null && productQuantity < 0)
        {
            throw new IllegalArgumentException("Product requires a valid weight");
        }

        Integer productPrice = values.getAsInteger(InventoryEntry.PRODUCT_PRICE);
        if(productPrice != null && productPrice < 0)
        {
            throw new IllegalArgumentException("Product requires a valid price");
        }

        if(values.size() == 0)
        {
            return 0;
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int rowsUpdated = db.update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);

        if(rowsUpdated != 0)
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;

    }
}
