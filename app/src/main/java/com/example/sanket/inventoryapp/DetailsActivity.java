package com.example.sanket.inventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanket.inventoryapp.R;
import com.example.sanket.inventoryapp.data.InventoryContract;
import com.example.sanket.inventoryapp.data.InventoryContract.InventoryEntry;

import static android.R.attr.data;
import static android.R.attr.id;
import static android.R.attr.name;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{


    private Uri inventoryUri;
    private static final int EXISTING_INVENTORY_LOADER = 0;

    ImageView inventoryImage;
    TextView inventoryName;
    TextView inventoryPrice;
    TextView inventoryQuantity;
    TextView inventorySupplier;

    String supplier;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        inventoryImage = (ImageView)findViewById(R.id.details_inventory_image);
        inventoryName = (TextView)findViewById(R.id.details_inventory_name);
        inventoryPrice = (TextView)findViewById(R.id.details_inventory_price);
        inventoryQuantity =(TextView)findViewById(R.id.details_inventory_quantity);
        inventorySupplier = (TextView)findViewById(R.id.details_inventory_supplier);

        inventoryUri = getIntent().getData();

        Button orderButton = (Button)findViewById(R.id.details_inventory_order);
        Button sellButton = (Button)findViewById(R.id.sell_button);
        Button shipmentButton = (Button)findViewById(R.id.shipment_button);

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.ordering_products));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] {supplier});
                startActivity(Intent.createChooser(intent, getString(R.string.send_email)));
            }
        });

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivity.this);
                final EditText edittext = new EditText(v.getContext());
                builder.setMessage(getString(R.string.enter_sold_quantity));
                builder.setView(edittext);

                builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
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

                        String[] projection =
                                {
                                        InventoryEntry._ID,
                                        InventoryEntry.PRODUCT_NAME,
                                        InventoryEntry.PRODUCT_PRICE,
                                        InventoryEntry.CURRENT_QUANTITY,
                                        InventoryEntry.PRODUCT_SUPPLIER,
                                        InventoryEntry.PRODUCT_IMAGE
                                };

                        Cursor cursor = getContentResolver().query(inventoryUri, projection, null, null, null);

                        if(cursor.moveToFirst())
                        {
                            do {
                                int intName = cursor.getColumnIndex(InventoryEntry.PRODUCT_NAME);
                                int intPrice = cursor.getColumnIndex(InventoryEntry.PRODUCT_PRICE);
                                int intQuantity = cursor.getColumnIndex(InventoryEntry.CURRENT_QUANTITY);
                                int intSupplier = cursor.getColumnIndex(InventoryEntry.PRODUCT_SUPPLIER);


                                String name = cursor.getString(intName);
                                int price = cursor.getInt(intPrice);
                                supplier = cursor.getString(intSupplier);
                                int quantity = cursor.getInt(intQuantity);


                                if(quantity - subtract >=0)
                                {
                                    int current = (quantity - subtract);

                                    ContentValues values2 = new ContentValues();
                                    values2.put(InventoryEntry.PRODUCT_NAME, name);
                                    values2.put(InventoryEntry.PRODUCT_PRICE, price);
                                    values2.put(InventoryEntry.PRODUCT_SUPPLIER, supplier);
                                    values2.put(InventoryEntry.CURRENT_QUANTITY, current);

                                    int rowsAffected = getContentResolver().update(inventoryUri, values2, null, null);
                                }
                                else if(quantity - subtract < 0)
                                {
                                    Toast.makeText(DetailsActivity.this, "Only " + quantity + " products available !", Toast.LENGTH_SHORT).show();
                                }

                            }
                            while(cursor.moveToNext());
                        }


                    }
                });

                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        shipmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivity.this);
                final EditText edittext1 = new EditText(v.getContext());
                builder.setMessage(getString(R.string.received_shipment));
                builder.setView(edittext1);

                builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        int addition;
                        if(TextUtils.isEmpty(edittext1.getText().toString().trim()))
                        {
                            addition = 0;
                        }
                        else
                        {
                            addition = Integer.parseInt(edittext1.getText().toString().trim());
                        }

                        String[] projection =
                                {
                                        InventoryEntry._ID,
                                        InventoryEntry.PRODUCT_NAME,
                                        InventoryEntry.PRODUCT_PRICE,
                                        InventoryEntry.CURRENT_QUANTITY,
                                        InventoryEntry.PRODUCT_SUPPLIER,
                                        InventoryEntry.PRODUCT_IMAGE
                                };

                        Cursor cursor = getContentResolver().query(inventoryUri, projection, null, null, null);

                        if(cursor.moveToFirst())
                        {
                            do {
                                int intName = cursor.getColumnIndex(InventoryEntry.PRODUCT_NAME);
                                int intPrice = cursor.getColumnIndex(InventoryEntry.PRODUCT_PRICE);
                                int intQuantity = cursor.getColumnIndex(InventoryEntry.CURRENT_QUANTITY);
                                int intSupplier = cursor.getColumnIndex(InventoryEntry.PRODUCT_SUPPLIER);


                                String name = cursor.getString(intName);
                                int price = cursor.getInt(intPrice);
                                supplier = cursor.getString(intSupplier);
                                int quantity = cursor.getInt(intQuantity);

                                    int current = (quantity + addition);

                                    ContentValues values3 = new ContentValues();
                                    values3.put(InventoryEntry.PRODUCT_NAME, name);
                                    values3.put(InventoryEntry.PRODUCT_PRICE, price);
                                    values3.put(InventoryEntry.PRODUCT_SUPPLIER, supplier);
                                    values3.put(InventoryEntry.CURRENT_QUANTITY, current);

                                    int rowsAffected = getContentResolver().update(inventoryUri, values3, null, null);

                            }
                            while(cursor.moveToNext());
                        }
                    }
                });

                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        getSupportLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.detailsactivity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.delete_current_inventory:
                showDeleteConfirmationDialog();
                return true;

            case R.id.edit_current_inventory:
                Intent intent = new Intent(DetailsActivity.this, EditorActivity.class);
                intent.setData(inventoryUri);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_current_inventory));
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deletePet() {

        if(inventoryUri != null)
        {
            int rowDeleted = getContentResolver().delete(inventoryUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.delete_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.delete_successfull),
                        Toast.LENGTH_SHORT).show();
            }

            finish();
        }

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection =
                {
                        InventoryEntry._ID,
                        InventoryEntry.PRODUCT_NAME,
                        InventoryEntry.PRODUCT_PRICE,
                        InventoryEntry.CURRENT_QUANTITY,
                        InventoryEntry.PRODUCT_SUPPLIER,
                        InventoryEntry.PRODUCT_IMAGE
                };



        return new CursorLoader(this,
                inventoryUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data == null || data.getCount() < 1) {
            return;
        }

        if(data.moveToFirst())
        {
            do{
                int intName = data.getColumnIndex(InventoryEntry.PRODUCT_NAME);
                int intPrice = data.getColumnIndex(InventoryEntry.PRODUCT_PRICE);
                int intQuantity = data.getColumnIndex(InventoryEntry.CURRENT_QUANTITY);
                int intSupplier = data.getColumnIndex(InventoryEntry.PRODUCT_SUPPLIER);
                int intImage = data.getColumnIndex(InventoryEntry.PRODUCT_IMAGE);

                String name = data.getString(intName);
                int price = data.getInt(intPrice);
                supplier = data.getString(intSupplier);
                int quantity = data.getInt(intQuantity);
                byte[] b = data.getBlob(intImage);

                if (b == null) {
                    inventoryImage.setImageResource(R.drawable.noimage);
                } else {
                    Bitmap image = BitmapFactory.decodeByteArray(b, 0, b.length);
                    inventoryImage.setImageBitmap(image);
                }
                inventoryName.setText(name);
                inventoryPrice.setText(String.valueOf(price));

                if(TextUtils.isEmpty(supplier))
                {
                    String supp = getString(R.string.unknown);
                    inventorySupplier.setText(supp);
                }
                else
                {
                    inventorySupplier.setText(supplier);
                }
                inventoryQuantity.setText(String.valueOf(quantity));
            }
            while(data.moveToNext());
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        inventoryImage.setImageResource(R.drawable.noimage);
        inventoryName.setText("");
        inventoryPrice.setText("");
        inventorySupplier.setText("");
        inventoryQuantity.setText("");

    }
}
