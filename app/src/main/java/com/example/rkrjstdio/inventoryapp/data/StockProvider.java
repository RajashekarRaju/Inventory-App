package com.example.rkrjstdio.inventoryapp.data;

import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.example.rkrjstdio.inventoryapp.data.StockContract.StockEntry;

public class StockProvider extends ContentProvider {

    // StockDbHelper object to match a mDbHelper to a corresponding code.
    private static StockDbHelper mDbHelper;

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = StockProvider.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the stock table
     */
    private static final int STOCK = 100;

    /**
     * URI matcher code for the content URI for a single item in the stocks table
     */
    private static final int STOCK_ID = 101;

    // quantityId to store long value for quantity display
    static long quantityId;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI(StockContract.CONTENT_AUTHORITY, StockContract.PATH_STOCK, STOCK);

        sUriMatcher.addURI(StockContract.CONTENT_AUTHORITY, StockContract.PATH_STOCK + "/#", STOCK_ID);
    }

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        mDbHelper = new StockDbHelper(getContext());

        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @SuppressLint("NewApi")
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {

            case STOCK:
                // For the stocks code, query the stocks table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the stocks table.
                cursor = database.query(StockEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, null);
                break;

            case STOCK_ID:
                // For the STOCK_ID code, extract out the ID from the URI.
                // The selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = StockEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the stocks table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(StockEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STOCK:
                return insertStock(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a stock into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertStock(Uri uri, ContentValues values) {

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new stock with the given values
        long id = database.insert(StockEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Check the image if is not null
        String image = values.getAsString(StockEntry.COLUMN_STOCK_IMAGE);
        if (image == null) {
            throw new IllegalArgumentException("Stock requires valid product image");
        }

        // Check the name if is not null
        String productName = values.getAsString(StockEntry.COLUMN_STOCK_PRODUCT_NAME);
        if (productName == null) {
            throw new IllegalArgumentException("Stock requires valid product name");
        }

        // Check the supplier information if is not null
        String productSupplier = values.getAsString(StockEntry.COLUMN_STOCK_PRODUCT_SUPPLIER);
        if (productSupplier == null) {
            throw new IllegalArgumentException("Stock requires valid supplier name");
        }

        // Check that the product rupees is not null
        String rupees = values.getAsString(StockEntry.COLUMN_STOCK_RUPEES);
        if (rupees == null) {
            throw new IllegalArgumentException("Stock requires valid rupees entry");
        }

        // Check that the product offer is not null
        String offer = values.getAsString(StockEntry.COLUMN_STOCK_OFFER);
        if (offer == null) {
            throw new IllegalArgumentException("Stock requires valid offer entry");
        }

        // Check that the stock quantity is not null
        String quantity = values.getAsString(StockEntry.COLUMN_STOCK_QUANTITY);
        if (quantity == null) {
            throw new IllegalArgumentException("Stock requires valid quantity entry");
        }

        // Check that the stock availability is valid entry
        Integer stockAvailability = values.getAsInteger(StockEntry.COLUMN_STOCK_STOCK_AVAILABILITY);
        if (stockAvailability == null || !StockEntry.isValidStockAvailability(stockAvailability)) {
            throw new IllegalArgumentException("Store requires valid stock information");
        }

        // Check that the sale data is valid entry
        Integer saleData = values.getAsInteger(StockEntry.COLUMN_STOCK_SALE_DATA);
        if (saleData == null || !StockEntry.isValidSaleData(saleData)) {
            throw new IllegalArgumentException("Store requires valid sale data information");
        }

        // Check that the customer review is valid entry
        Integer customerReview = values.getAsInteger(StockEntry.COLUMN_STOCK_CUSTOMER_REVIEW);
        if (customerReview == null || !StockEntry.isValidCustomerReview(customerReview)) {
            throw new IllegalArgumentException("Store requires valid customer review information");
        }

        // Check that the category is valid entry
        Integer category = values.getAsInteger(StockEntry.COLUMN_STOCK_CATEGORY);
        if (category == null || !StockEntry.isValidCategory(category)) {
            throw new IllegalArgumentException("Store requires valid category information");
        }

        // Notify all listeners that the data has changed for the stock content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in th`e table,
        // return the new URI with the ID appended `to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STOCK:
                return updateStock(uri, contentValues, selection, selectionArgs);
            case STOCK_ID:
                // For the STOCK_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = StockEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateStock(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update stock in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more stocks).
     * Return the number of rows that were successfully updated.
     */
    private int updateStock(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link STOCKEntry#COLUMN_STOCK_PRODUCT_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(StockEntry.COLUMN_STOCK_PRODUCT_NAME)) {
            String productName = values.getAsString(StockEntry.COLUMN_STOCK_PRODUCT_NAME);
            if (productName == null) {
                throw new IllegalArgumentException("Stock requires valid product name");
            }
        }

        // check that the supplier information value is not null.
        if (values.containsKey(StockEntry.COLUMN_STOCK_PRODUCT_SUPPLIER)) {
            String productSupplier = values.getAsString(StockEntry.COLUMN_STOCK_PRODUCT_SUPPLIER);
            if (productSupplier == null) {
                throw new IllegalArgumentException("Stock requires valid supplier name");
            }
        }

        // check that the product image is not null
        if (values.containsKey(StockEntry.COLUMN_STOCK_IMAGE)) {
            String imageView = values.getAsString(StockEntry.COLUMN_STOCK_IMAGE);
            if (imageView == null) {
                throw new IllegalArgumentException("Stock requires valid product image");
            }
        }

        // check that the product rupees entry is not null
        if (values.containsKey(StockEntry.COLUMN_STOCK_RUPEES)) {
            String rupees = values.getAsString(StockEntry.COLUMN_STOCK_RUPEES);
            if (rupees == null) {
                throw new IllegalArgumentException("Stock requires valid rupee entry");
            }
        }

        // check that the product offer entry is not null
        if (values.containsKey(StockEntry.COLUMN_STOCK_OFFER)) {
            String offer = values.getAsString(StockEntry.COLUMN_STOCK_OFFER);
            if (offer == null) {
                throw new IllegalArgumentException("Stock requires valid offer entry");
            }
        }

        // check that the product quantity choose is not null
        if (values.containsKey(StockEntry.COLUMN_STOCK_QUANTITY)) {
            String quantity = values.getAsString(StockEntry.COLUMN_STOCK_QUANTITY);
            if (quantity == null) {
                throw new IllegalArgumentException("Stock requires valid quantity entry");
            }
        }

        // check that the stock availability value is valid.
        if (values.containsKey(StockEntry.COLUMN_STOCK_STOCK_AVAILABILITY)) {
            Integer stockAvailability = values.getAsInteger(StockEntry.COLUMN_STOCK_STOCK_AVAILABILITY);
            if (stockAvailability == null || !StockEntry.isValidStockAvailability(stockAvailability)) {
                throw new IllegalArgumentException("Store requires valid stock information");
            }
        }

        // check that the sale data entry value is valid.
        if (values.containsKey(StockEntry.COLUMN_STOCK_SALE_DATA)) {
            Integer saleData = values.getAsInteger(StockEntry.COLUMN_STOCK_SALE_DATA);
            if (saleData == null || !StockEntry.isValidSaleData(saleData)) {
                throw new IllegalArgumentException("Store requires valid sale data information");
            }
        }

        // check that the customer review entry value is valid.
        if (values.containsKey(StockEntry.COLUMN_STOCK_CUSTOMER_REVIEW)) {
            Integer customerReview = values.getAsInteger(StockEntry.COLUMN_STOCK_CUSTOMER_REVIEW);
            if (customerReview == null || !StockEntry.isValidCustomerReview(customerReview)) {
                throw new IllegalArgumentException("Store requires valid customer review information");
            }
        }

        // check that the product category value is valid.
        if (values.containsKey(StockEntry.COLUMN_STOCK_CATEGORY)) {
            Integer category = values.getAsInteger(StockEntry.COLUMN_STOCK_CATEGORY);
            if (category == null || !StockEntry.isValidCategory(category)) {
                throw new IllegalArgumentException("Store requires valid category information");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }
        // Otherwise, get writable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(StockEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Returns the number of database rows affected by the update statement
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STOCK:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(StockEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STOCK_ID:
                // Delete a single row given by the ID in the URI
                selection = StockEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(StockEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }


    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STOCK:
                return StockEntry.CONTENT_LIST_TYPE;
            case STOCK_ID:
                return StockEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    // Notify quantity update to database table to change data cursor
    public static Cursor sellQuantity() {
        // Initialize cursor
        Cursor cursor;
        //Get readable database to update quantity of stock
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // Projection for selected columns to display on database
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

        cursor = database.query(
                StockEntry.TABLE_NAME, projection, null, null, null,
                null, null);

        // return cursor position
        return cursor;
    }

    // Method triggers for on click sell item in detail screen, from StocksActivity
    // On clicking button will reduce product stock by 1
    public static void sellOneItem(int mId, int mQuantity) {
        // Update quantity by Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Initialize quantity variable with 0 , to keep in check for negative values and increase
        // value by one on clicking the sell button
        int newQuantity = 0;
        // Check if value is equal greater than zero before starting evaluation for quantity
        if (mQuantity > 0) {
            newQuantity = mQuantity - 1;
        }
        // Create content values object
        ContentValues values = new ContentValues();

        //Input new quantity value into column
        values.put(StockEntry.COLUMN_STOCK_QUANTITY, newQuantity);

        // Update quantity value for selected position or row in table for all stock variables
        String selection = StockEntry._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(mId)};
        quantityId = database.update(StockEntry.TABLE_NAME, values, selection, selectionArgs);

        // Check if quantity becomes negative value
        if (quantityId == -1) {
            //Log message if update fails
            Log.e(LOG_TAG, "Failed to update row for " + mId);
        }
    }

}
