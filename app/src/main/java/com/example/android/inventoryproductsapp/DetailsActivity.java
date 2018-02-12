package com.example.android.inventoryproductsapp;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;


public class DetailsActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailsActivity.class.getCanonicalName();
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private InventoryProductDbHelper dbHelper;
    EditText nameEditText;
    EditText priceEditText;
    EditText quantityEditText;
    EditText supplierNameEditText;
    EditText supplierPhoneEditText;
    EditText supplierEmailEditText;
    long currentItemId;
    ImageButton decreaseQuantityBtn;
    ImageButton increaseQuantityBtn;
    Button imageBtn;
    ImageView imageView;
    Uri actualUri;
    private static final int PICK_IMAGE_REQUEST = 0;
    Boolean infoItemHasChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initViews();

        dbHelper = new InventoryProductDbHelper(this);
        currentItemId = getIntent().getLongExtra("itemId", 0);

        if (currentItemId == 0) {
            setTitle(getString(R.string.editor_activity_title_new_item));
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_item));
            addValuesToEditItem(currentItemId);
        }

        handleClickEvents();

    }

    private void initViews() {
        nameEditText = (EditText) findViewById(R.id.product_name_edit_text);
        priceEditText = (EditText) findViewById(R.id.price_edit_text);
        quantityEditText = (EditText) findViewById(R.id.quantity_edit_text);
        supplierNameEditText = (EditText) findViewById(R.id.supplier_name_edit_text);
        supplierPhoneEditText = (EditText) findViewById(R.id.supplier_name_edit_text);
        supplierEmailEditText = (EditText) findViewById(R.id.supplier_email_edit_text);
        decreaseQuantityBtn = (ImageButton) findViewById(R.id.decrease_quantity_btn);
        increaseQuantityBtn = (ImageButton) findViewById(R.id.increase_quantity_btn);
        imageBtn = (Button) findViewById(R.id.select_image);
        imageView = (ImageView) findViewById(R.id.image_view);

    }

    @Override
    public void onBackPressed() {
        if (!infoItemHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void handleClickEvents() {

        decreaseQuantityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subtractOneToQuantity();
                infoItemHasChanged = true;
            }
        });

        increaseQuantityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sumOneToQuantity();
                infoItemHasChanged = true;
            }
        });

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryOpeningImageSelector();
                infoItemHasChanged = true;
            }
        });
    }

    private void subtractOneToQuantity() {
        String previousValueString = quantityEditText.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            return;
        } else if (previousValueString.equals("0")) {
            return;
        } else {
            previousValue = Integer.parseInt(previousValueString);
            quantityEditText.setText(String.valueOf(previousValue - 1));
        }
    }

    private void sumOneToQuantity() {
        String previousValueString = quantityEditText.getText().toString();
        int previousValue;
        if (previousValueString.isEmpty()) {
            previousValue = 0;
        } else {
            previousValue = Integer.parseInt(previousValueString);
        }
        quantityEditText.setText(String.valueOf(previousValue + 1));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentItemId == 0) {
            MenuItem deleteProductMenuItem = menu.findItem(R.id.action_delete_product);
            MenuItem deleteAllProductMenuItem = menu.findItem(R.id.action_delete_all_products);
            MenuItem orderMenuItem = menu.findItem(R.id.action_order_products);
            deleteProductMenuItem.setVisible(false);
            deleteAllProductMenuItem.setVisible(false);
            orderMenuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                // save item in DB
                if (!addItemToDb()) {
                    // saying to onOptionsItemSelected that user clicked button
                    return true;
                }
                finish();
                return true;
            case android.R.id.home:
                if (!infoItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
            case R.id.action_order_products:
                // dialog with phone and email
                showConfirmOderDialog();
                return true;
            case R.id.action_delete_product:
                // delete one item
                showDeleteConfirmationDialog(currentItemId);
                return true;
            case R.id.action_delete_all_products:
                //delete all data
                showDeleteConfirmationDialog(0);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean addItemToDb() {
        boolean isValid = true;
        if (!checkForEmptiness(nameEditText, "name")) {
            isValid = false;
        }
        if (!checkForEmptiness(priceEditText, "price")) {
            isValid = false;
        }
        if (!checkForEmptiness(quantityEditText, "quantity")) {
            isValid = false;
        }
        if (!checkForEmptiness(supplierNameEditText, "supplier name")) {
            isValid = false;
        }
        if (!checkForEmptiness(supplierPhoneEditText, "supplier phone")) {
            isValid = false;
        }
        if (!checkForEmptiness(supplierEmailEditText, "supplier email")) {
            isValid = false;
        }
        if (actualUri == null && currentItemId == 0) {
            isValid = false;
            imageBtn.setError("Missing image");
        }
        if (!isValid) {
            return false;
        }

        if (currentItemId == 0) {
            InventoryProduct item = new InventoryProduct(
                    nameEditText.getText().toString().trim(),
                    Integer.parseInt(quantityEditText.getText().toString().trim()),
                    priceEditText.getText().toString().trim(),
                    supplierNameEditText.getText().toString().trim(),
                    supplierEmailEditText.getText().toString().trim(),
                    supplierPhoneEditText.getText().toString().trim(),
                    actualUri.toString());
            dbHelper.insertProduct(item);
        } else {
            int quantity = Integer.parseInt(quantityEditText.getText().toString().trim());
            dbHelper.updateProduct(currentItemId, quantity);
        }
        return true;
    }

    private boolean checkForEmptiness(EditText text, String description) {
        if (TextUtils.isEmpty(text.getText())) {
            text.setError("Missing product " + description);
            return false;
        } else {
            text.setError(null);
            return true;
        }
    }

    private void addValuesToEditItem(long itemId) {
        Cursor cursor = dbHelper.readProduct(itemId);
        cursor.moveToFirst();
      
        nameEditText.setText(cursor.getString(cursor.getColumnIndex(InventoryProductContract.InventoryEntry.COLUMN_NAME)));
        priceEditText.setText(cursor.getString(cursor.getColumnIndex(InventoryProductContract.InventoryEntry.COLUMN_PRICE)));
        quantityEditText.setText(cursor.getString(cursor.getColumnIndex(InventoryProductContract.InventoryEntry.COLUMN_QUANTITY)));
        supplierNameEditText.setText(cursor.getString(cursor.getColumnIndex(InventoryProductContract.InventoryEntry.COLUMN_SUPPLIER_NAME)));
        supplierPhoneEditText.setText(cursor.getString(cursor.getColumnIndex(InventoryProductContract.InventoryEntry.COLUMN_SUPPLIER_PHONE)));
        supplierEmailEditText.setText(cursor.getString(cursor.getColumnIndex(InventoryProductContract.InventoryEntry.COLUMN_SUPPLIER_EMAIL)));
        imageView.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(InventoryProductContract.InventoryEntry.COLUMN_IMAGE))));
        nameEditText.setEnabled(false);
        priceEditText.setEnabled(false);
        supplierNameEditText.setEnabled(false);
        supplierPhoneEditText.setEnabled(false);
        supplierEmailEditText.setEnabled(false);
        imageBtn.setEnabled(false);
    }

    private void showConfirmOderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.order_message);
        builder.setPositiveButton(R.string.phone, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // intent to phone
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + supplierPhoneEditText.getText().toString().trim()));
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.email, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // intent to email
                Intent intent = new Intent(android.content.Intent.ACTION_SENDTO);
                intent.setType("text/plain");
                intent.setData(Uri.parse("mailto:" + supplierEmailEditText.getText().toString().trim()));
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "New order");
                String bodyMessage = "Please send us following product:\nProduct Name - " +
                        nameEditText.getText().toString().trim()+
                        "\nProduct Quantiy - "+ quantityEditText.getText().toString().trim();
                intent.putExtra(android.content.Intent.EXTRA_TEXT, bodyMessage);
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private int deleteAllProducts() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        return database.delete(InventoryProductContract.InventoryEntry.TABLE_NAME, null, null);
    }

    private int deleteProduct(long itemId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String selection = InventoryProductContract.InventoryEntry._ID + "=?";
        String[] selectionArgs = {String.valueOf(itemId)};
        int rowsDeleted = database.delete(
                InventoryProductContract.InventoryEntry.TABLE_NAME, selection, selectionArgs);
        return rowsDeleted;
    }

    private void showDeleteConfirmationDialog(final long itemId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_message);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (itemId == 0) {
                    deleteAllProducts();
                } else {
                    deleteProduct(itemId);
                }
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void tryOpeningImageSelector() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }
        openImageSelector();
    }

    private void openImageSelector() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImageSelector();
                    // permission was granted
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.  Pull that uri using "resultData.getData()"

            if (resultData != null) {
                actualUri = resultData.getData();
                imageView.setImageURI(actualUri);
                imageView.invalidate();
            }
        }
    }

}
