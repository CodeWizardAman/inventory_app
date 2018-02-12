package com.example.android.inventoryproductsapp;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private final static String LOG_TAG = MainActivity.class.getCanonicalName();
    InventoryProductDbHelper dbHelper;
    InventoryCursorAdapter adapter;
    int lastVisibleItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new InventoryProductDbHelper(this);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_new_product_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                startActivity(intent);
            }
        });

        final ListView listView = (ListView) findViewById(R.id.list_view_id);
        View emptyView = findViewById(R.id.empty_view_id);
        listView.setEmptyView(emptyView);

        Cursor cursor = dbHelper.readInventory();

        adapter = new InventoryCursorAdapter(this, cursor);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == 0) return;
                final int currentFirstVisibleItem = view.getFirstVisiblePosition();
                if (currentFirstVisibleItem > lastVisibleItem) {
                    fab.show();
                } else if (currentFirstVisibleItem < lastVisibleItem) {
                    fab.hide();
                }
                lastVisibleItem = currentFirstVisibleItem;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.swapCursor(dbHelper.readInventory());
    }

    public void clickOnViewItem(long id) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("itemId", id);
        startActivity(intent);
    }

    public void clickOnSale(long id, int quantity) {
        dbHelper.sellProduct(id, quantity);
        adapter.swapCursor(dbHelper.readInventory());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_dummy_data:
                // add dummy data for testing
                addDummyData();
                adapter.swapCursor(dbHelper.readInventory());
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Add data for demo purposes
     *
     * tring productName, int productQuantity, String productPrice, String supplierName,
     String supplierEmail, String supplierPhone, String image
     */
    private void addDummyData() {
        InventoryProduct chocolateCake = new InventoryProduct(
                "Chocolate Cake",
                12,
                "Rs 200",
                "Hari Sweets",
                "harisweets24@meetha.com",
                "020-23458798",
                "android.resource://com.example.android.inventoryproductsapp/drawable/chocolate_cake");
        dbHelper.insertProduct(chocolateCake);

        InventoryProduct pineappleCake = new InventoryProduct(
                "Chocolate Cake",
                12,
                "Rs 200",
                "Hari Sweets",
                "harisweets24@meetha.com",
                "020-23458798",
                "android.resource://com.example.android.inventoryproductsapp/drawable/chocolate_cake");
        dbHelper.insertProduct(chocolateCake);

    }

}
