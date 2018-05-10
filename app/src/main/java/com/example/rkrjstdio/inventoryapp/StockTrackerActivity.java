package com.example.rkrjstdio.inventoryapp;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.rkrjstdio.inventoryapp.data.StockContract.StockEntry;

public class StockTrackerActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the stock data loader
     */
    private static final int EXISTING_STOCK_LOADER = 0;

    /**
     * EditText field to enter the product name of stock
     */
    private EditText mProductNameEditText;

    /**
     * EditText field to enter the supplier information of stock
     */
    private EditText mProductSupplierEditText;

    /**
     * EditText field to enter the cost of item
     */
    private EditText mRupeeEditText;

    /**
     * EditText field to enter the available offers on item
     */
    private EditText mOfferEditText;

    /**
     * EditText field to enter the number of quantity required
     */
    private EditText mQuantityEditText;

    /**
     * Button to increase the number of quantity required
     */
    Button mIncreaseQuantity;

    /**
     * Button to decrease the number of quantity required
     */
    Button mDecreaseQuantity;

    /**
     * EditText field to give image for the item
     */
    private ImageView mImageView;

    /**
     * Uri to parse image into stock database
     */
    private Uri mImageURI;

    /**
     * Identifier for the stock image loader
     */
    private static final int RESULT_LOAD_IMAGE = 1;

    /**
     * Identifier for the requesting external permissions to access image
     */
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    /**
     * EditText field to enter information stock availability
     */
    private Spinner mStockAvailabilitySpinner;

    /**
     * Availability of the stock. The possible valid values are in the StockContract.java file:
     * {@link StockEntry#STOCK_AVAILABLE}, or
     * {@link StockEntry#STOCK_NOT_AVAILABLE}.
     */
    private int mStockAvailability = StockEntry.STOCK_UNKNOWN;

    /**
     * Drop Down Spinner field to select the valid stock either InStock or OutOfStock
     */
    private Spinner mSaleDataSpinner;

    /**
     * Sale Data of the stock. The possible valid values are in the StockContract.java file:
     * {@link StockEntry#SALE_DATA_PROFIT}, or
     * {@link StockEntry#SALE_DATA_LOSS}.
     */
    private int mSaleData = StockEntry.SALE_DATA_PROFIT;

    /**
     * Drop Down Spinner field to select the valid customer review information either Good,
     * Bad or Average
     */
    private Spinner mCustomerReviewSpinner;

    /**
     * Setting selection default as good product for spinner unselected
     */
    private int mCustomerReview = StockEntry.REVIEW_GOOD_PRODUCT;

    /**
     * Initialising category spinner to choose stock category
     */
    private Spinner mCategorySpinner;

    /**
     * Setting selection default as electronics for spinner unselected in categories
     */
    private int mCategory = StockEntry.CATEGORY_ELECTRONICS;

    /**
     * Boolean flag that keeps track of whether the stock has been edited (true) or not (false)
     */
    private boolean mStockHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mStockHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mStockHasChanged = true;
            return false;
        }
    };

    /**
     * Content URI for the existing stock (null if it's a new stock)
     */
    private Uri mCurrentStockUri;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_tracker);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new stock or editing an existing one.
        Intent intent = getIntent();
        mCurrentStockUri = intent.getData();

        // If the intent DOES NOT contain a stock content URI, then we know that we are
        // creating a new stock.
        if (mCurrentStockUri == null) {
            // This is a new stock, so change the app bar to say "Add new stock"
            setTitle(getString(R.string.editor_activity_title_new_stock));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a stock that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing stock, so change app bar to say "Edit stock"
            setTitle(getString(R.string.editor_activity_title_edit_stock));

            // Initialize a loader to read the stock data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_STOCK_LOADER, null, this);
        }

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Find all relevant views that we will need to read user input from
        mProductNameEditText = findViewById(R.id.track_product_name);
        mProductSupplierEditText = findViewById(R.id.track_product_supplier);
        mRupeeEditText = findViewById(R.id.track_product_rupee);
        mOfferEditText = findViewById(R.id.track_product_offer);
        mStockAvailabilitySpinner = findViewById(R.id.track_spinner_stock);
        mImageView = findViewById(R.id.track_product_image);
        mSaleDataSpinner = findViewById(R.id.track_spinner_sale_data);
        mCustomerReviewSpinner = findViewById(R.id.track_spinner_customer_review);
        mCategorySpinner = findViewById(R.id.track_spinner_category);
        mQuantityEditText = findViewById(R.id.track_product_add_more);

        // Button to increase stock value by +1 on click
        // Setting on click listener to the button to increase stock
        // On click executes calling activity increaseQuantity() to handle values
        mIncreaseQuantity = findViewById(R.id.track_increase_quantity);
        mIncreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increaseQuantity();
            }
        });

        // Button to decrease stock value by -1 on click
        // Setting on click listener to the button to decrease stock
        // On click executes calling activity decreaseQuantity() to handle values
        mDecreaseQuantity = findViewById(R.id.track_decrease_quantity);
        mDecreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decreaseQuantity();
            }
        });

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mProductNameEditText.setOnTouchListener(mTouchListener);
        mProductSupplierEditText.setOnTouchListener(mTouchListener);
        mRupeeEditText.setOnTouchListener(mTouchListener);
        mOfferEditText.setOnTouchListener(mTouchListener);
        mStockAvailabilitySpinner.setOnTouchListener(mTouchListener);
        mImageView.setOnTouchListener(mTouchListener);
        mSaleDataSpinner.setOnTouchListener(mTouchListener);
        mCustomerReviewSpinner.setOnTouchListener(mTouchListener);
        mCategorySpinner.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);

        // Method to initialize spinners which allows user to select values into stock
        // Spinner with Stock Availability options
        stockAvailabilitySpinner();
        // Spinner with Sale Data options
        saleDataSpinner();
        // Spinner with Customer Review options
        customerReviewSpinner();
        // Spinner with Stock Category options
        categorySpinner();
    }

    // Remove from quantity -1 onClick
    private void increaseQuantity() {
        String currentQuantity = mQuantityEditText.getText().toString().trim();
        int newQuantity = Integer.parseInt(currentQuantity);
        mQuantityEditText.setText(String.valueOf(newQuantity + 1));
    }

    // Add to quantity +1 onClick
    private void decreaseQuantity() {
        String currentQuantity = mQuantityEditText.getText().toString().trim();
        int newQuantity = Integer.parseInt(currentQuantity);
        if (newQuantity <= 0) {
            mQuantityEditText.setText(String.valueOf(0));
        } else {
            mQuantityEditText.setText(String.valueOf(newQuantity - 1));
        }
    }

    /**
     * Setup the dropdown spinner that allows the user to select the availability of stock.
     */
    private void stockAvailabilitySpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter stockAvailabilitySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_stock_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        stockAvailabilitySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mStockAvailabilitySpinner.setAdapter(stockAvailabilitySpinnerAdapter);

        // Set the integer mSelected to the constant values
        mStockAvailabilitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.in_stock))) {
                        mStockAvailability = StockEntry.STOCK_AVAILABLE;
                    } else if (selection.equals(getString(R.string.no_stock))) {
                        mStockAvailability = StockEntry.STOCK_NOT_AVAILABLE;
                    } else {
                        mStockAvailability = StockEntry.STOCK_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mStockAvailability = StockEntry.STOCK_UNKNOWN;
            }
        });
    }

    /**
     * Setup the dropdown spinner that allows the user to select the sale data from stock.
     */
    private void saleDataSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter saleDataSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_sale_data_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        saleDataSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mSaleDataSpinner.setAdapter(saleDataSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mSaleDataSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.profit))) {
                        mSaleData = StockEntry.SALE_DATA_PROFIT;
                    } else {
                        mSaleData = StockEntry.SALE_DATA_LOSS;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSaleData = StockEntry.SALE_DATA_PROFIT;
            }
        });
    }

    /**
     * Setup the dropdown spinner that allows the user to select the customer review from stock.
     */
    private void customerReviewSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter customerReviewSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_customer_review_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        customerReviewSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mCustomerReviewSpinner.setAdapter(customerReviewSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mCustomerReviewSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.good))) {
                        mCustomerReview = StockEntry.REVIEW_GOOD_PRODUCT;
                    } else if (selection.equals(getString(R.string.average))) {
                        mCustomerReview = StockEntry.REVIEW_AVERAGE_PRODUCT;
                    } else {
                        mCustomerReview = StockEntry.REVIEW_BAD_PRODUCT;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSaleData = StockEntry.REVIEW_GOOD_PRODUCT;
            }
        });
    }

    /**
     * Setup the dropdown spinner that allows the user to select the category item from stock.
     */
    private void categorySpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter categorySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_category_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mCategorySpinner.setAdapter(categorySpinnerAdapter);

        // Set the integer mSelected to the constant values
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.electronics))) {
                        mCategory = StockEntry.CATEGORY_ELECTRONICS;
                    } else if (selection.equals(getString(R.string.clothes))) {
                        mCategory = StockEntry.CATEGORY_CLOTHES;
                    } else {
                        mCategory = StockEntry.CATEGORY_VIDEOGAMES;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSaleData = StockEntry.REVIEW_GOOD_PRODUCT;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_stock_tracker.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_stock_tracker, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new stock, hide the "Delete" menu item.
        if (mCurrentStockUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Inserting stocks into fields displaying data for user
                saveStock();

                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;

            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the stock hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mStockHasChanged) {
                    NavUtils.navigateUpFromSameTask(StockTrackerActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(StockTrackerActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the stock hasn't changed, continue with handling back button press
        if (!mStockHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all stock attributes, define a projection that contains
        // all columns from the stock table
        String[] projection = {
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

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this, mCurrentStockUri, projection, null,
                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of stock attributes that we're interested in
            int productNameColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_PRODUCT_NAME);
            int supplierColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_PRODUCT_SUPPLIER);
            int rupeeColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_RUPEES);
            int offerColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_OFFER);
            int stockAvailabilityColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_STOCK_AVAILABILITY);
            int imageViewColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_IMAGE);
            int saleDataColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_SALE_DATA);
            int customerReviewColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_CUSTOMER_REVIEW);
            int categoryColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_CATEGORY);
            int quantityColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_QUANTITY);

            // Extract out the value from the Cursor for the given column index
            String productName = cursor.getString(productNameColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            int rupee = cursor.getInt(rupeeColumnIndex);
            int offer = cursor.getInt(offerColumnIndex);
            int stockAvailability = cursor.getInt(stockAvailabilityColumnIndex);
            String imageView = cursor.getString(imageViewColumnIndex);
            int saleData = cursor.getInt(saleDataColumnIndex);
            int customerReview = cursor.getInt(customerReviewColumnIndex);
            int category = cursor.getInt(categoryColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);

            // Update the views on the screen with the values from the database
            mProductNameEditText.setText(productName);
            mProductSupplierEditText.setText(supplier);
            mRupeeEditText.setText(String.valueOf(rupee));
            mOfferEditText.setText(String.valueOf(offer));
            mImageView.setImageURI(Uri.parse(imageView));
            mImageURI = Uri.parse(imageView);
            mQuantityEditText.setText(String.valueOf(quantity));

            // Stock Availability is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown, 1 is In Stock, 2 is No Stock).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (stockAvailability) {
                case StockEntry.STOCK_AVAILABLE:
                    mStockAvailabilitySpinner.setSelection(0);
                    break;
                case StockEntry.STOCK_NOT_AVAILABLE:
                    mStockAvailabilitySpinner.setSelection(1);
                    break;
                default:
                    mStockAvailabilitySpinner.setSelection(2);
                    break;
            }

            // Sale Data is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Profit, 1 is Loss).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (saleData) {
                case StockEntry.SALE_DATA_PROFIT:
                    mSaleDataSpinner.setSelection(0);
                    break;
                default:
                    mSaleDataSpinner.setSelection(1);
                    break;
            }

            // Customer Review is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Good Product, 1 is Average Product, 2 is Bad Product).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (customerReview) {
                case StockEntry.REVIEW_GOOD_PRODUCT:
                    mCustomerReviewSpinner.setSelection(0);
                    break;
                case StockEntry.REVIEW_AVERAGE_PRODUCT:
                    mCustomerReviewSpinner.setSelection(1);
                    break;
                default:
                    mCustomerReviewSpinner.setSelection(2);
                    break;
            }

            // Category is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Electronics, 1 is Clothes, 2 is Video Games).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (category) {
                case StockEntry.CATEGORY_ELECTRONICS:
                    mCategorySpinner.setSelection(0);
                    break;
                case StockEntry.CATEGORY_CLOTHES:
                    mCategorySpinner.setSelection(1);
                    break;
                default:
                    mCategorySpinner.setSelection(2);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mProductNameEditText.setText("");
        mProductSupplierEditText.setText("");
        mRupeeEditText.setText("");
        mOfferEditText.setText("");
        mStockAvailabilitySpinner.setSelection(0);
        mSaleDataSpinner.setSelection(0);
        mCustomerReviewSpinner.setSelection(0);
        mCategorySpinner.setSelection(0);
        mQuantityEditText.setText("");
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
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
     * Prompt the user to confirm that they want to delete this stock.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the stock.
                deleteStock();
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
     * Perform the deletion of the stock in the database.
     */
    private void deleteStock() {
        // Only perform the delete if this is an existing stock.
        if (mCurrentStockUri != null) {
            // Call the ContentResolver to delete the stock at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentStockUri
            // content URI already identifies the stock that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentStockUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_stock_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_stock_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

    // Inserting data into EditText fields and storing into fields
    private void saveStock() {
        String productNameString = mProductNameEditText.getText().toString().trim();
        String supplierNameString = mProductSupplierEditText.getText().toString().trim();
        String rupeeString = mRupeeEditText.getText().toString().trim();
        String offerString = mOfferEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();

        // Validate if user leaves views empty
        if(TextUtils.isEmpty(productNameString)) {
            mProductNameEditText.setError(getString(R.string.required));
            mProductSupplierEditText.requestFocus();
            return;
        }

        // Validate if user leaves views empty
        if(TextUtils.isEmpty(rupeeString)) {
            mRupeeEditText.setError(getString(R.string.required));
            return;
        }

        // Validate if user leaves views empty
        if(TextUtils.isEmpty(quantityString)) {
            mQuantityEditText.setError(getString(R.string.required));
            return;
        }

        // Validate if user leaves views empty
        if(TextUtils.isEmpty(offerString)) {
            mOfferEditText.setError(getString(R.string.required));
            return;
        }

        // Validate if user leaves views empty
        if(TextUtils.isEmpty(supplierNameString)) {
            mProductSupplierEditText.setError(getString(R.string.required));
            return;
        }

        // storing data into fields and displaying to layout using values
        ContentValues values = new ContentValues();
        values.put(StockEntry.COLUMN_STOCK_PRODUCT_NAME, productNameString);
        values.put(StockEntry.COLUMN_STOCK_PRODUCT_SUPPLIER, supplierNameString);
        values.put(StockEntry.COLUMN_STOCK_RUPEES, rupeeString);
        values.put(StockEntry.COLUMN_STOCK_OFFER, offerString);
        values.put(StockEntry.COLUMN_STOCK_STOCK_AVAILABILITY, mStockAvailability);
        values.put(StockEntry.COLUMN_STOCK_IMAGE, String.valueOf(mImageURI));
        values.put(StockEntry.COLUMN_STOCK_SALE_DATA, mSaleData);
        values.put(StockEntry.COLUMN_STOCK_CUSTOMER_REVIEW, mCustomerReview);
        values.put(StockEntry.COLUMN_STOCK_CATEGORY, mCategory);
        values.put(StockEntry.COLUMN_STOCK_QUANTITY, quantityString);

        // Determine if this is a new or existing stock by checking if mCurrentStockUri is null or not
        if (mCurrentStockUri == null) {
            // This is a NEW stock, so insert a new stock into the provider,
            // returning the content URI for the new stock.
            Uri newUri = getContentResolver().insert(StockEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_stock_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_stock_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING stock, so update the stock with content URI: mCurrentStockUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentStockUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentStockUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_stock_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_stock_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    // On CLick Image Button open activity in device to choose image.
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onClickImageChoose(View view) {
        selectImageForProduct();
    }

    // Opens gallery in device by accessing device permissions declared in manifest
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void selectImageForProduct() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }
        chooseImage();
    }

    // Open gallery as intent to choose image for product
    private void chooseImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
    }

    // If external permission is granted loads image in stock database successfully
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseImage();
                }
            }
        }
    }

    // Parse image using ImageUri request data
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            mImageView.setImageURI(selectedImage);
            assert selectedImage != null;
            mImageURI = Uri.parse(selectedImage.toString());
        }
    }

}
