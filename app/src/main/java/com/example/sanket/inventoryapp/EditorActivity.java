package com.example.sanket.inventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sanket.inventoryapp.data.InventoryContract;
import com.example.sanket.inventoryapp.data.InventoryContract.InventoryEntry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.R.attr.bitmap;
import static android.R.attr.data;
import static android.R.attr.x;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{


    ImageView inventoryImage;
    ImageButton camera;
    ImageButton gallery;
    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_PHOTO = 100;
    private static final int EXISTING_URI = 0;
    Bitmap inventory_image;

    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierEditText;

    private Uri inventoryUri;

    private boolean mInventoryHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mInventoryHasChanged = true;
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        inventoryUri = getIntent().getData();

        if(inventoryUri == null)
        {
            setTitle(getString(R.string.add_inventory));
            invalidateOptionsMenu();
        }
        else
        {
            setTitle(getString(R.string.edit_inventory));
            getSupportLoaderManager().initLoader(EXISTING_URI, null, this);
        }


        inventoryImage = (ImageView)findViewById(R.id.inventory_image);
        mNameEditText = (EditText)findViewById(R.id.edit_inventory_name);
        mPriceEditText = (EditText)findViewById(R.id.edit_inventory_price);
        mQuantityEditText = (EditText)findViewById(R.id.edit_inventory_quantity);
        mSupplierEditText = (EditText)findViewById(R.id.edit_inventory_supplier);

        camera = (ImageButton)findViewById(R.id.camera_image);
        gallery = (ImageButton)findViewById(R.id.gallery_image);

        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        });


        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch(requestCode) {
            case CAMERA_REQUEST:

            if (resultCode == RESULT_OK) {

                inventory_image = (Bitmap) data.getExtras().get("data");
                inventoryImage.setImageBitmap(inventory_image);
            }
            break;

            case SELECT_PHOTO:

                if (resultCode == RESULT_OK && null!= data) {
                    Uri imageUri = data.getData();
                    try {
                            inventory_image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        }
                        catch (IOException ie)
                        {
                            ie.printStackTrace();
                        }
                        inventoryImage.setImageBitmap(inventory_image);
                }

                break;
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if(inventoryUri == null)
        {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_save:

                String stringName = mNameEditText.getText().toString().trim();
                String stringPrice = mPriceEditText.getText().toString().trim();
                String stringQuantity = mQuantityEditText.getText().toString().trim();
                String stringSupplier = mSupplierEditText.getText().toString().trim();
                byte[] img = getBytes(inventory_image);

                if(TextUtils.isEmpty(stringName) && TextUtils.isEmpty(stringPrice) && TextUtils.isEmpty(stringQuantity) &&
                        TextUtils.isEmpty(stringSupplier) && img == null) {

                    finish();
                }
                else
                {
                    saveInventory();
                    finish();
                }
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                if(!mInventoryHasChanged)
                {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                NavUtils.navigateUpFromSameTask(EditorActivity.this);

                            }
                        };

                showUnsavedChangedDialog(discardButtonClickListener);
                return true;
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
    public void onBackPressed()
    {
        if(!mInventoryHasChanged)
        {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        finish();
                    }
                };

        showUnsavedChangedDialog(discardButtonClickListener);
    }

    private void showUnsavedChangedDialog(DialogInterface.OnClickListener discardButtonClickListener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.discard_changes));
        builder.setPositiveButton(getString(R.string.discard), discardButtonClickListener);
        builder.setNegativeButton(getString(R.string.keep_editing), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(dialog != null)
                {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void saveInventory()
    {

        String stringName = mNameEditText.getText().toString().trim();
        String stringPrice = mPriceEditText.getText().toString().trim();
        String stringQuantity = mQuantityEditText.getText().toString().trim();
        String stringSupplier = mSupplierEditText.getText().toString().trim();

        byte[] img = getBytes(inventory_image);

        if(inventoryUri == null &&
                TextUtils.isEmpty(stringName) && TextUtils.isEmpty(stringPrice)
                && TextUtils.isEmpty(stringQuantity) && TextUtils.isEmpty(stringSupplier))
        {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.PRODUCT_NAME, stringName);
        values.put(InventoryEntry.PRODUCT_PRICE, stringPrice);
        values.put(InventoryEntry.CURRENT_QUANTITY, stringQuantity);
        values.put(InventoryEntry.PRODUCT_SUPPLIER, stringSupplier);

        if(img !=null) {
            values.put(InventoryEntry.PRODUCT_IMAGE, img);
        }

        if(inventoryUri == null) {

            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.data_insert_failed), Toast.LENGTH_LONG).show();
            } else
            {
                Toast.makeText(this, getString(R.string.data_insert_successfull), Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            int rowsAffected = getContentResolver().update(inventoryUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.data_update_failed), Toast.LENGTH_LONG).show();
            } else
            {
                Toast.makeText(this, getString(R.string.data_update_successfull), Toast.LENGTH_LONG).show();
            }
        }
    }

    public static byte[] getBytes(Bitmap bitmap)
    {
        if(bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            return stream.toByteArray();
        }
        else
        {
            return null;
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
                String supplier = data.getString(intSupplier);
                int quantity = data.getInt(intQuantity);
                byte[] b = data.getBlob(intImage);

                if (b == null) {
                    inventoryImage.setImageResource(R.drawable.noimage);
                } else {
                    Bitmap image = BitmapFactory.decodeByteArray(b, 0, b.length);
                    inventoryImage.setImageBitmap(image);
                }
                mNameEditText.setText(name);
                mPriceEditText.setText(String.valueOf(price));
                mSupplierEditText.setText(supplier);
                mQuantityEditText.setText(String.valueOf(quantity));
            }
            while(data.moveToNext());
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        inventoryImage.setImageResource(R.drawable.noimage);
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mSupplierEditText.setText("");
        mQuantityEditText.setText("");

    }
}
