# Inventory-App
Inventory App which would allow a store to keep track of its inventory of products. The app will need to store information about price, quantity available, supplier, and a picture of the product. It will also need to allow the user to track sales and shipments and make it easy for the user to order more from the listed supplier.
This project is part of udacity student nanodegree completion program.

App has welcome screen activity with 3 different layouts which are skippable , moves forward and backward to next activity. Each alyout gives overview of the app usage for user which is shown only for first time use of application.
Components of the each layout :
1. ImageView of respective screen.
2. TextView showing brief description of different parts of usage in application.
3. Borderless button with TextView SKIP which takes to main activity layout of application.
4. Borderless button with TextView NEXT which takes to next welcome screen layout without skipping.
5. Three Dots which shows present screen layout position of all welcome screens.
6. Different background colors for each layout.

This first time launch is handled ny PreferenceManager :

// setting activity to launch at first time

    void setFirstTimeLaunch() {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, false);
        editor.commit();   
    }

// checking activity for first time launch.

    boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }
 
 ![screenshot 104](https://user-images.githubusercontent.com/25173010/39888099-80169b74-54b1-11e8-826a-a249a4788570.png)
 
 Main Layout of app has following components
 1. Floating action bar which is necessery to add new item or product into inventory switching to new activity using intents.
 
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
 
2. ListView which contains list of cards, each card has different views and components :

![screenshot 109](https://user-images.githubusercontent.com/25173010/39894816-b9598ffe-54c5-11e8-8290-73ba901a4f1a.png)

(a) ImageView : each product can be merged with respective images. Images are easily selectable or picked from the gallery.
Declare user permissions in AndroidmManifest.xml file to pick image from gallery and sto store in database.

      <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
      <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>

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
  
  ![screenshot 105](https://user-images.githubusercontent.com/25173010/39889550-da5fd826-54b5-11e8-9c31-ba6d5e728e4f.png)
  
  In activity_main.xml , following attributes has been choosen :
  
          <ImageView
                android:id="@+id/product_image"
                android:layout_width="150dp"
                android:layout_height="130dp"
                android:padding="16dp"
                android:scaleType="centerCrop"
                android:layout_gravity="center"
                android:adjustViewBounds="true"
                android:src="@drawable/ic_laptop_mac_black_48px"
                tools:ignore="ContentDescription" />
       
 (b) TextViews :
 Name of the product
 Cost of the product
 Quantity of the product available
 Offer available for the product
 Product description
 
 (c) Spinners :
 Product stock which updates textview into 3 possible forms In-Stock, Out-Of-Stock, Unknown.
 Sale Data which gives store seller whether the product has positive or negative sale data. The possible spinner selections are profit and loss.
 Customer Review which gives product quanlity for seller to manage product inventory. The possible spinner selections are Average product, Good product, Bad product.
 Category which is useful for inventory to divide products seperately. The possible spinner selection available is Electronics, Clothes, Video games.
 
  Example code for spinner :
  
      <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:paddingTop="16dp">
            
            <ImageView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_gravity="center_vertical"
                android:paddingRight="28dp"
                android:src="@drawable/ic_stock"
                tools:ignore="ContentDescription" />

            <TextView
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1.5"
                android:textColor="@color/colorDeepPurple"
                android:textSize="16sp"
                android:layout_gravity="center_vertical"
                android:text="@string/product_stock" />

            <LinearLayout 
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="2"
                android:orientation="vertical">

                <Spinner
                    android:id="@+id/track_spinner_stock"
                    android:layout_width="wrap_content"
                    android:layout_height="48dp"
                    android:paddingRight="16dp"
                    android:spinnerMode="dropdown"/>
                    
            </LinearLayout>
        </LinearLayout>


// Setup the dropdown spinner that allows the user to select the availability of stock.

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
  
arrays.xml         

<!-- Array of words for spinner options Stock Availability -->

    <string-array name="array_stock_options">
        <item>@string/in_stock</item>
        <item>@string/no_stock</item>
        <item>@string/unknown</item>
    </string-array>
    
(d) EditText 
EditText fields to enter information of each product correctly.
Name of the product
Cost of the product
Quantity of the product available
Offer available for the product

Example :

        <EditText
             android:id="@+id/track_product_name"
             android:layout_width="0dp"
             android:layout_height="wrap_content"
             android:layout_weight="2"
             android:inputType="text"
             android:paddingLeft="4dp"
             android:hint="@string/product_name_hint" />
 
![screenshot 110](https://user-images.githubusercontent.com/25173010/39895023-6320c890-54c6-11e8-8ad7-8909d083c95f.png)
 
(e) Share Intent
On clicking this ImageView icon opens as intents

       <ImageView
             android:layout_width="wrap_content"
             android:layout_height="wrap_content"
             android:padding="8dp"
             android:layout_gravity="center"
             android:background="@drawable/ripple_effect_button"
             android:focusable="false"
             android:onClick="shareProduct"
             android:src="@drawable/ic_share_black_24dp"
             tools:ignore="ContentDescription" />
             
    ripple_effect_button.xml
    
         <ripple
             xmlns:android="http://schemas.android.com/apk/res/android"
             xmlns:tools="http://schemas.android.com/tools"
             android:color="@color/colorDeepPurple"
             tools:targetApi="lollipop">
             <item android:id="@android:id/mask">
                 <shape android:shape="rectangle">
                     <solid android:color="@color/colorDeepPurple" />
                 </shape>
             </item>
         </ripple>
         
   // Share intent to pass store stock availability
    
       public void shareProduct(View view) {
           Intent sendIntent = new Intent();
           sendIntent.setAction(Intent.ACTION_SEND);
           sendIntent.putExtra(Intent.EXTRA_TEXT,
                   "Hey check out my store for new deals and exciting products");
           sendIntent.setType("text/plain");
           startActivity(sendIntent);
        }
        
![screenshot 111](https://user-images.githubusercontent.com/25173010/39895162-e2f6e900-54c6-11e8-9304-0a1c0e04fb1e.png)


(f) Sell One item
On clicking this ImageView products quantity reduces by -1 and updates current value of stock availablity.

      <ImageView
             android:id="@+id/sell_on_click"
             android:src="@drawable/ic_if_buy_2639786"
             android:layout_width="wrap_content"
             android:layout_height="wrap_content"
             android:padding="8dp"
             android:layout_gravity="center"
             android:background="@drawable/ripple_effect_button"
             android:focusable="false"
             android:src="@drawable/ic_share_black_24dp"
             tools:ignore="ContentDescription" />
             
      ImageView saleImageView = view.findViewById(R.id.sell_on_click);
      int quantityColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_QUANTITY);
      final int mQuantity = cursor.getInt(quantityColumnIndex);
      // Set on click listener to manage quantity value for user
        saleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get reference from StocksActivity to sell one item
                StocksActivity.sellItem(mId, mQuantity);
            }
        });
      quantityTextView.setText(String.valueOf(mQuantity));

(g) Order More Button :
On clicking ths button action intents opens to share information through sharing options available.

            <Button
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_gravity="center"
                android:layout_marginLeft="16dp"
                android:focusable="false"
                android:background="@drawable/ripple_effect_button"
                android:onClick="orderMore"
                android:text="@string/order_more_button" />
                
      // On click button reference to order more stock from the supplier using intents
      
             public void orderMore(View view) {
                 Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                 sharingIntent.setType("text/plain");
                 String shareBody = "Bring order by end of the month";
                 sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                 sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                 startActivity(Intent.createChooser(sharingIntent, "Share via"));
             }
  
3. CardView

          <android.support.v7.widget.CardView 
              xmlns:android="http://schemas.android.com/apk/res/android"
              xmlns:card_view="http://schemas.android.com/apk/res-auto"
              xmlns:tools="http://schemas.android.com/tools"
              android:layout_width="match_parent"
              android:layout_height="wrap_content"
              card_view:cardBackgroundColor="@color/colorCardBackground"
              card_view:cardCornerRadius="@dimen/padding_8dp"
              card_view:cardUseCompatPadding="true">
              
              <Layout
                   .
                   .
                   .
                   />
              
          </android.support.v7.widget.CardView>
      
Also add dependencies in application directory

    implementation 'com.android.support:cardview-v7:27.1.0'
                       
5. If store is empty with no products available, to improve user freindly experience empty view should be updated in background.

       <RelativeLayout
             android:id="@+id/empty_view"
             android:layout_width="wrap_content"
             android:layout_height="wrap_content"
             android:layout_centerInParent="true">
     
             <ImageView
                 android:id="@+id/empty_store_image"
                 android:layout_width="wrap_content"
                 android:layout_height="wrap_content"
                 android:layout_centerHorizontal="true"
                 android:src="@drawable/ic_store"
                 tools:ignore="ContentDescription" />
     
             <TextView
                 android:id="@+id/empty_title_text"
                 style="@style/No_Stock_TextView"
                 android:layout_below="@+id/empty_store_image"
                 android:text="@string/store_is_empty"
                 android:textAppearance="?android:textAppearanceMedium" />
     
             <TextView
                 android:id="@+id/empty_subtitle_text"
                 style="@style/No_Stock_TextView"
                 android:layout_below="@+id/empty_title_text"
                 android:text="@string/add_stock_to_manage_inventory"
                 android:textAppearance="?android:textAppearanceSmall" />

       </RelativeLayout>
       
In MainActivity class

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);

        // Empty view to display when items are available in database no items
        stockListView.setEmptyView(emptyView);
     
     Layout :
     
![screenshot 93](https://user-images.githubusercontent.com/25173010/39894003-2fb36902-54c3-11e8-81c5-bfa6bec9c458.png)

5. Menu Item

Insert Existing Stock

    <menu xmlns:android="http://schemas.android.com/apk/res/android"
          xmlns:app="http://schemas.android.com/apk/res-auto"
          xmlns:tools="http://schemas.android.com/tools"
          tools:context=".StocksActivity">
      
          <item
              android:id="@+id/add_sample"
              android:title="@string/add_existing_stock"
              app:showAsAction="never" />
      
          <item
              android:id="@+id/action_delete_all_stock"
              android:title="@string/action_delete_all_entries"
              app:showAsAction="never" />
    </menu>
    
    
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

Delete all stock
 
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
    
![screenshot 108](https://user-images.githubusercontent.com/25173010/39894581-f5f6164a-54c4-11e8-8740-554010a621a8.png)

