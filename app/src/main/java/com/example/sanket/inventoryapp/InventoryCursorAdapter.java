package com.example.sanket.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanket.inventoryapp.data.InventoryContract.InventoryEntry;

import static android.R.attr.name;

/**
 * Created by sanket on 10/05/17.
 */

public class InventoryCursorAdapter extends CursorAdapter {

    private MainActivity activity = new MainActivity();

    public InventoryCursorAdapter(MainActivity context, Cursor c)
    {
        super(context, c, 0);
        this.activity = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        final long id;
        final int mQuantity;
        final String mName;
        final int mPrice;
        final String mSupplier;

        TextView inventoryName = (TextView)view.findViewById(R.id.list_item_name);
        TextView inventoryPrice = (TextView)view.findViewById(R.id.list_item_price);
        TextView inventoryQuantity = (TextView)view.findViewById(R.id.list_item_quantity);
        Button inventorySaleButton = (Button)view.findViewById(R.id.list_item_button);

        String name = cursor.getString(cursor.getColumnIndex(InventoryEntry.PRODUCT_NAME));
        int price = cursor.getInt(cursor.getColumnIndex(InventoryEntry.PRODUCT_PRICE));
        int quantity = cursor.getInt(cursor.getColumnIndex(InventoryEntry.CURRENT_QUANTITY));
        id = cursor.getLong(cursor.getColumnIndex(InventoryEntry._ID));
        String supplier = cursor.getString(cursor.getColumnIndex(InventoryEntry.PRODUCT_SUPPLIER));

        mName = name;
        mPrice = price;
        mQuantity = quantity;
        mSupplier = supplier;

        inventorySaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                final EditText edittext = new EditText(v.getContext());
                builder.setMessage(activity.getString(R.string.enter_sale_quantity));
                builder.setTitle(activity.getString(R.string.quantity));

                builder.setView(edittext);

                builder.setPositiveButton(activity.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        int subtract;
                        if(TextUtils.isEmpty(edittext.getText().toString().trim()))
                        {
                            subtract = 0;
                        }
                        else
                        {
                            subtract = Integer.parseInt(edittext.getText().toString().trim());
                        }

                        if(mQuantity - subtract >=0)
                        {
                            activity.onSaleClick(id, mName, mPrice, mSupplier, mQuantity, subtract);
                        }
                        else if(mQuantity - subtract < 0)
                        {
                            Toast.makeText(activity, "Only "+ mQuantity + " products available !" , Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                builder.setNegativeButton(activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(dialog!=null) {
                            dialog.dismiss();
                        }
                    }
                });


                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        inventoryName.setText(name);
        inventoryPrice.setText(String.valueOf(price));
        inventoryQuantity.setText(String.valueOf(quantity));

    }
}
