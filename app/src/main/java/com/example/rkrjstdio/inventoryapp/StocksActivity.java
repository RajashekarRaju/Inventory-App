package com.example.rkrjstdio.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.rkrjstdio.inventoryapp.data.StockContract.StockEntry;
import com.example.rkrjstdio.inventoryapp.data.StockDbHelper;
import com.example.rkrjstdio.inventoryapp.data.StockProvider;

public class StocksActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // private variable for StockDbHelper to initialize mDbHelper;
    StockDbHelper mDbHelper;

    ImageView imageView;

    // Loader Manager to load table stock into activity
    private static final int STOCK_LOADER = 0;

    // Private variable for stock adapter
    private static StockCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open StockTrackerActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent for another activity
                Intent intent = new Intent(StocksActivity.this, StockTrackerActivity.class);
                startActivity(intent);
            }
        });

        // Writing to database using StockDbHelper class.
        mDbHelper = new StockDbHelper(this);

        mCursorAdapter = new StockCursorAdapter(this, null);
        // Find the ListView which will be populated with the stock data
        ListView stockListView = findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);

        // Empty view to display when items are available in database no items
        stockListView.setEmptyView(emptyView);

        // Adapter for list of stock in activity notifying cursor adapter
        stockListView.setAdapter(mCursorAdapter);
        // Give reference to each list view using adapter
        stockListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(StocksActivity.this, StockTrackerActivity.class);
                Uri currentStockUri = ContentUris.withAppendedId(StockEntry.CONTENT_URI, id);
                intent.setData(currentStockUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(STOCK_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_stocks.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_stocks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert sample data" menu option
            case R.id.add_sample:
                // Get sample database on start for user to manage inventory
                insertStock();
                addMoreStock();
                addFewMoreStock();
                completeTheStock();

                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_stock:
                // Show dialog box to confirm user options
                showDeleteAllConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteAllConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg_all);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the stock.
                deleteAllStock();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the stock.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Helper method to delete all stock data in the database.
     */
    private void deleteAllStock() {
        // Only perform the delete if this is an existing stock in the store.
        if (StockEntry.CONTENT_URI != null) {
            // Call the ContentResolver to delete the stock at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentStockUri
            // content URI already identifies the stock that we want.
            int rowsDeleted = getContentResolver().delete(StockEntry.CONTENT_URI, null,
                    null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.delete_all_stock),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.delete_all_stock_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Helper method to insert hardcoded stock data into the database.
     */
    private void insertStock() {
        // Create a ContentValues object where column names are the keys,
        ContentValues values = new ContentValues();
        values.put(StockEntry.COLUMN_STOCK_PRODUCT_NAME, "MacBook Air");
        values.put(StockEntry.COLUMN_STOCK_PRODUCT_SUPPLIER, "Cloud Tail");
        values.put(StockEntry.COLUMN_STOCK_RUPEES, "44,500");
        values.put(StockEntry.COLUMN_STOCK_OFFER, "17");
        Uri image = Uri.parse("android.resource://com.example.rkrjstdio.inventoryapp/drawable/ic_laptop_mac_black_48px");
        values.put(StockEntry.COLUMN_STOCK_IMAGE, String.valueOf(image));
        values.put(StockEntry.COLUMN_STOCK_STOCK_AVAILABILITY, StockEntry.STOCK_AVAILABLE);
        values.put(StockEntry.COLUMN_STOCK_SALE_DATA, StockEntry.SALE_DATA_PROFIT);
        values.put(StockEntry.COLUMN_STOCK_CUSTOMER_REVIEW, StockEntry.REVIEW_GOOD_PRODUCT);
        values.put(StockEntry.COLUMN_STOCK_CATEGORY, StockEntry.CATEGORY_ELECTRONICS);
        values.put(StockEntry.COLUMN_STOCK_QUANTITY, "25");

        // Insert a new row for stock type into the provider using the ContentResolver.
        // Use the {@link StockEntry#CONTENT_URI} to indicate that we want to insert
        // into the stock database table.
        // Receive the new content URI that will allow us to access stocks data in the future.
        Uri newUri = getContentResolver().insert(StockEntry.CONTENT_URI, values);
    }

    private void addMoreStock() {
        // Create a ContentValues object to diplay more data fro user
        ContentValues values = new ContentValues();
        values.put(StockEntry.COLUMN_STOCK_PRODUCT_NAME, "Dress");
        values.put(StockEntry.COLUMN_STOCK_PRODUCT_SUPPLIER, "Shine Sights");
        values.put(StockEntry.COLUMN_STOCK_RUPEES, "2,500");
        values.put(StockEntry.COLUMN_STOCK_OFFER, "33");
        Uri image = Uri.parse("android.resource://com.example.rkrjstdio.inventoryapp/drawable/dress");
        values.put(StockEntry.COLUMN_STOCK_IMAGE, String.valueOf(image));
        values.put(StockEntry.COLUMN_STOCK_STOCK_AVAILABILITY, StockEntry.STOCK_AVAILABLE);
        values.put(StockEntry.COLUMN_STOCK_SALE_DATA, StockEntry.SALE_DATA_LOSS);
        values.put(StockEntry.COLUMN_STOCK_CUSTOMER_REVIEW, StockEntry.REVIEW_AVERAGE_PRODUCT);
        values.put(StockEntry.COLUMN_STOCK_CATEGORY, StockEntry.CATEGORY_CLOTHES);
        values.put(StockEntry.COLUMN_STOCK_QUANTITY, "36");

        // Insert a new row for stock type into the provider using the ContentResolver.
        // Use the {@link StockEntry#CONTENT_URI} to indicate that we want to insert
        // into the stock database table.
        // Receive the new content URI that will allow us to access stocks data in the future.
        Uri newUri = getContentResolver().insert(StockEntry.CONTENT_URI, values);
    }

    private void addFewMoreStock() {
        // Create a ContentValues object to display more data for user
        ContentValues values = new ContentValues();
        values.put(StockEntry.COLUMN_STOCK_PRODUCT_NAME, "Joystick");
        values.put(StockEntry.COLUMN_STOCK_PRODUCT_SUPPLIER, "Gamer Lines");
        values.put(StockEntry.COLUMN_STOCK_RUPEES, "1,950");
        values.put(StockEntry.COLUMN_STOCK_OFFER, "5");
        Uri image = Uri.parse("android.resource://com.example.rkrjstdio.inventoryapp/drawable/gamepad");
        values.put(StockEntry.COLUMN_STOCK_IMAGE, String.valueOf(image));
        values.put(StockEntry.COLUMN_STOCK_STOCK_AVAILABILITY, StockEntry.STOCK_AVAILABLE);
        values.put(StockEntry.COLUMN_STOCK_SALE_DATA, StockEntry.SALE_DATA_PROFIT);
        values.put(StockEntry.COLUMN_STOCK_CUSTOMER_REVIEW, StockEntry.REVIEW_GOOD_PRODUCT);
        values.put(StockEntry.COLUMN_STOCK_CATEGORY, StockEntry.CATEGORY_VIDEOGAMES);
        values.put(StockEntry.COLUMN_STOCK_QUANTITY, "19");

        // Insert a new row for stock type into the provider using the ContentResolver.
        // Use the {@link StockEntry#CONTENT_URI} to indicate that we want to insert
        // into the stock database table.
        // Receive the new content URI that will allow us to access stocks data in the future.
        Uri newUri = getContentResolver().insert(StockEntry.CONTENT_URI, values);
    }

    private void completeTheStock() {
        // Create a ContentValues object to display more data for the user
        ContentValues values = new ContentValues();
        values.put(StockEntry.COLUMN_STOCK_PRODUCT_NAME, "Shoe");
        values.put(StockEntry.COLUMN_STOCK_PRODUCT_SUPPLIER, "Star Traders");
        values.put(StockEntry.COLUMN_STOCK_RUPEES, "6,200");
        values.put(StockEntry.COLUMN_STOCK_OFFER, "0");
        Uri image = Uri.parse("android.resource://com.example.rkrjstdio.inventoryapp/drawable/shoe");
        values.put(StockEntry.COLUMN_STOCK_IMAGE, String.valueOf(image));
        values.put(StockEntry.COLUMN_STOCK_STOCK_AVAILABILITY, StockEntry.STOCK_NOT_AVAILABLE);
        values.put(StockEntry.COLUMN_STOCK_SALE_DATA, StockEntry.SALE_DATA_PROFIT);
        values.put(StockEntry.COLUMN_STOCK_CUSTOMER_REVIEW, StockEntry.REVIEW_GOOD_PRODUCT);
        values.put(StockEntry.COLUMN_STOCK_CATEGORY, StockEntry.CATEGORY_CLOTHES);
        values.put(StockEntry.COLUMN_STOCK_QUANTITY, "0");

        // Insert a new row for stock type into the provider using the ContentResolver.
        // Use the {@link StockEntry#CONTENT_URI} to indicate that we want to insert
        // into the stock database table.
        // Receive the new content URI that will allow us to access stocks data in the future.
        Uri newUri = getContentResolver().insert(StockEntry.CONTENT_URI, values);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                StockEntry._ID,
                StockEntry.COLUMN_STOCK_PRODUCT_NAME,
                StockEntry.COLUMN_STOCK_PRODUCT_SUPPLIER,
                StockEntry.COLUMN_STOCK_IMAGE,
                StockEntry.COLUMN_STOCK_RUPEES,
                StockEntry.COLUMN_STOCK_OFFER,
                StockEntry.COLUMN_STOCK_STOCK_AVAILABILITY,
                StockEntry.COLUMN_STOCK_SALE_DATA,
                StockEntry.COLUMN_STOCK_CUSTOMER_REVIEW,
                StockEntry.COLUMN_STOCK_CATEGORY,
                StockEntry.COLUMN_STOCK_QUANTITY};

        return new CursorLoader(this, StockEntry.CONTENT_URI,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    // On click button reference to order more stock from the supplier using intents
    public void orderMore(View view) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Bring order by end of the month";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    // Share intent to pass store stock availability
    public void shareProduct(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out my store for new deals and exciting products");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    // Sell one item on click sell button reference to provider class
    public static void sellItem(int mId, int mQuantity) {
        StockProvider.sellOneItem(mId, mQuantity);
        mCursorAdapter.swapCursor(StockProvider.sellQuantity());
    }

}
