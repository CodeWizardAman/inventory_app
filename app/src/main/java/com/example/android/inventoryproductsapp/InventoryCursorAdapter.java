package com.example.android.inventoryproductsapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class InventoryCursorAdapter extends CursorAdapter {

    private final MainActivity mainActivity;

    public InventoryCursorAdapter(MainActivity context, Cursor c) {
        super(context, c, 0);
        this.mainActivity = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView productNameTextView = (TextView) view.findViewById(R.id.product_name);
        TextView productQtyTextView = (TextView) view.findViewById(R.id.quantity);
        TextView productPriceTextView = (TextView) view.findViewById(R.id.price);
        ImageView saleImageView = (ImageView) view.findViewById(R.id.sale);
        ImageView productImageView = (ImageView) view.findViewById(R.id.image_view);

        String name = cursor.getString(cursor.getColumnIndex(InventoryProductContract.InventoryEntry.COLUMN_NAME));
        final int quantity = cursor.getInt(cursor.getColumnIndex(InventoryProductContract.InventoryEntry.COLUMN_QUANTITY));
        String price = cursor.getString(cursor.getColumnIndex(InventoryProductContract.InventoryEntry.COLUMN_PRICE));

        productImageView.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(InventoryProductContract.InventoryEntry.COLUMN_IMAGE))));

        productNameTextView.setText(name);
        productQtyTextView.setText(String.valueOf(quantity));
        productPriceTextView.setText(price);

        final long id = cursor.getLong(cursor.getColumnIndex(InventoryProductContract.InventoryEntry._ID));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.clickOnViewItem(id);
            }
        });

        saleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.clickOnSale(id,
                       quantity);
            }
        });
    }
}
