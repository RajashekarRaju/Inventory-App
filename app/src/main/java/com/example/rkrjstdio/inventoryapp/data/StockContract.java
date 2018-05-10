package com.example.rkrjstdio.inventoryapp.data;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class StockContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private StockContract() {
    }

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    static final String CONTENT_AUTHORITY = "com.example.rkrjstdio.inventoryapp";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     */
    static final String PATH_STOCK = "stock";

    public static final class StockEntry implements BaseColumns {

        /**
         * The content URI to access the stock data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_STOCK);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of stock in store.
         */
        static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STOCK;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single stock in store.
         */
        static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STOCK;

        /**
         * Name of database table for stock
         */
        public final static String TABLE_NAME = "stocks";

        /**
         * Column id for each row in table in list of items
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Column name of the product in stock table
         */
        public final static String COLUMN_STOCK_PRODUCT_NAME = "productname";

        /**
         * Column image of the product in stock table
         */
        public final static String COLUMN_STOCK_IMAGE = "image";

        /**
         * Column rupees (cost) of the product in stock table
         */
        public final static String COLUMN_STOCK_RUPEES = "rupees";

        /**
         * Column Stock availability of the product
         */
        public final static String COLUMN_STOCK_STOCK_AVAILABILITY = "stockavailability";

        /**
         * Column quantity of the product in stock table
         */
        public final static String COLUMN_STOCK_QUANTITY = "quantity";

        /**
         * Column Customer review for items in table stock
         */
        public final static String COLUMN_STOCK_CUSTOMER_REVIEW = "customerreview";

        /**
         * Column sale data of the item in stock table
         */
        public final static String COLUMN_STOCK_SALE_DATA = "saledata";

        /**
         * Column category for unique stock updated in table
         */
        public final static String COLUMN_STOCK_CATEGORY = "category";

        /**
         * Column supplier information of the product in stock table
         */
        public final static String COLUMN_STOCK_PRODUCT_SUPPLIER = "supplier";

        /**
         * Column offer of the product in stock table
         */
        public final static String COLUMN_STOCK_OFFER = "offer";

        /**
         * Possible values for the Column Stock availability of the product
         * STOCK_AVAILABLE sets value In Stock
         * STOCK_NOT_AVAILABLE sets value No Stock
         * STOCK_UNKNOWN sets value Unknown
         */
        public static final int STOCK_AVAILABLE = 0;
        public static final int STOCK_NOT_AVAILABLE = 1;
        public static final int STOCK_UNKNOWN = 2;

        /**
         * Possible values for the customer review are
         * <p>
         * The only possible values are {#REVIEW_GOOD_PRODUCT},
         * or {@link #REVIEW_AVERAGE_PRODUCT} or {@link #REVIEW_BAD_PRODUCT}
         */
        public static final int REVIEW_GOOD_PRODUCT = 0;
        public static final int REVIEW_AVERAGE_PRODUCT = 1;
        public static final int REVIEW_BAD_PRODUCT = 2;

        /**
         * The only possible values are {#SALE_DATA_PROFIT},
         * or {@link #SALE_DATA_LOSS}.
         */
        public static final int SALE_DATA_PROFIT = 0;
        public static final int SALE_DATA_LOSS = 1;

        /**
         * Possible values for the stock category are
         * The only possible values are {#CATEGORY_ELECTRONICS},
         * {@link #CATEGORY_CLOTHES} or {@link #CATEGORY_VIDEOGAMES}
         */
        public static final int CATEGORY_ELECTRONICS = 0;
        public static final int CATEGORY_CLOTHES = 1;
        public static final int CATEGORY_VIDEOGAMES = 2;

        /**
         * Returns whether or not the given stock is valid or not from selected options
         */
        static boolean isValidStockAvailability(int stockAvailability) {
            return stockAvailability == STOCK_AVAILABLE || stockAvailability == STOCK_NOT_AVAILABLE
                    || stockAvailability == STOCK_UNKNOWN;
        }

        /**
         * Returns whether or not the given customer review is valid or not from selected
         * user options
         */
        static boolean isValidCustomerReview(int customerReview) {
            return customerReview == REVIEW_GOOD_PRODUCT || customerReview == REVIEW_AVERAGE_PRODUCT ||
                    customerReview == REVIEW_BAD_PRODUCT;
        }

        /**
         * Returns whether or not the given sale data is valid or not from selected options
         */
        static boolean isValidSaleData(int saleData) {
            return saleData == SALE_DATA_PROFIT || saleData == SALE_DATA_LOSS;
        }

        /**
         * Returns whether or not the given category is valid or not for user selected options
         * from stock table
         */
        static boolean isValidCategory(int category) {
            return category == CATEGORY_ELECTRONICS || category == CATEGORY_CLOTHES ||
                    category == CATEGORY_VIDEOGAMES;
        }
    }

}