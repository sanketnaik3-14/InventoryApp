package com.example.sanket.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by sanket on 07/05/17.
 */

public class InventoryContract {

    private InventoryContract()
    {

    }

    public static final String CONTENT_AUTHORITY = "com.example.sanket.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_INVENTORY = "inventoryapp";


    public static final class InventoryEntry implements BaseColumns
    {

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);
        public static final String TABLE_NAME = "inventory";

        public static final String _ID = BaseColumns._ID;
        public static final String PRODUCT_NAME = "name";
        public static final String CURRENT_QUANTITY = "quantity";
        public static final String PRODUCT_PRICE = "price";
        public static final String PRODUCT_IMAGE = "image";
        public static final String PRODUCT_SUPPLIER = "supplier";

    }

}
