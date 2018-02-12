package com.example.android.inventoryproductsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by UFO_24 on 12-02-2018.
 */


public class InventoryProductDbHelper extends SQLiteOpenHelper {

    public final static String DB_NAME = "inventory.db";
    public final static int DB_VERSION = 1;
    public final static String LOG_TAG = InventoryProductDbHelper.class.getCanonicalName();

    public InventoryProductDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(InventoryProductContract.InventoryEntry.CREATE_TABLE_STOCK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertProduct(InventoryProduct product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(InventoryProductContract.InventoryEntry.COLUMN_NAME, product.getProductName());
        values.put(InventoryProductContract.InventoryEntry.COLUMN_PRICE, product.getProductPrice());
        values.put(InventoryProductContract.InventoryEntry.COLUMN_QUANTITY, product.getProductQuantity());
        values.put(InventoryProductContract.InventoryEntry.COLUMN_SUPPLIER_NAME, product.getSupplierName());
        values.put(InventoryProductContract.InventoryEntry.COLUMN_SUPPLIER_PHONE, product.getSupplierPhone());
        values.put(InventoryProductContract.InventoryEntry.COLUMN_SUPPLIER_EMAIL, product.getSupplierEmail());
        values.put(InventoryProductContract.InventoryEntry.COLUMN_IMAGE, product.getImage());
        long id = db.insert(InventoryProductContract.InventoryEntry.TABLE_NAME, null, values);
    }

    public Cursor readInventory() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                InventoryProductContract.InventoryEntry._ID,
                InventoryProductContract.InventoryEntry.COLUMN_NAME,
                InventoryProductContract.InventoryEntry.COLUMN_PRICE,
                InventoryProductContract.InventoryEntry.COLUMN_QUANTITY,
                InventoryProductContract.InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryProductContract.InventoryEntry.COLUMN_SUPPLIER_PHONE,
                InventoryProductContract.InventoryEntry.COLUMN_SUPPLIER_EMAIL,
                InventoryProductContract.InventoryEntry.COLUMN_IMAGE
        };
        Cursor cursor = db.query(
                InventoryProductContract.InventoryEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        return cursor;
    }

    public Cursor readProduct(long productId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                InventoryProductContract.InventoryEntry._ID,
                InventoryProductContract.InventoryEntry.COLUMN_NAME,
                InventoryProductContract.InventoryEntry.COLUMN_PRICE,
                InventoryProductContract.InventoryEntry.COLUMN_QUANTITY,
                InventoryProductContract.InventoryEntry.COLUMN_SUPPLIER_NAME,
                InventoryProductContract.InventoryEntry.COLUMN_SUPPLIER_PHONE,
                InventoryProductContract.InventoryEntry.COLUMN_SUPPLIER_EMAIL,
                InventoryProductContract.InventoryEntry.COLUMN_IMAGE
        };
        String selection = InventoryProductContract.InventoryEntry._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(productId)};

        Cursor cursor = db.query(
                InventoryProductContract.InventoryEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        return cursor;
    }

    public void updateProduct(long currentProductId, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(InventoryProductContract.InventoryEntry.COLUMN_QUANTITY, quantity);
        String selection = InventoryProductContract.InventoryEntry._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(currentProductId)};
        db.update(InventoryProductContract.InventoryEntry.TABLE_NAME,
                values, selection, selectionArgs);
    }

    public void sellProduct(long productId, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        int newQuantity = 0;
        if (quantity > 0) {
            newQuantity = quantity - 1;
        }
        ContentValues values = new ContentValues();
        values.put(InventoryProductContract.InventoryEntry.COLUMN_QUANTITY, newQuantity);
        String selection = InventoryProductContract.InventoryEntry._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(productId)};
        db.update(InventoryProductContract.InventoryEntry.TABLE_NAME,
                values, selection, selectionArgs);
    }
}

