package com.example.rkrjstdio.inventoryapp;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rkrjstdio.inventoryapp.data.StockContract.StockEntry;

public class StockCursorAdapter extends CursorAdapter {

    StockCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the stock data (in the current row pointed to by cursor) to the given
     * list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {

        // Find individual views that we want to modify in the list item layout
        TextView productNameTextView = view.findViewById(R.id.product_name);
        TextView supplierTextView = view.findViewById(R.id.product_supplier);
        TextView rupeesTextView = view.findViewById(R.id.product_price);
        TextView offerTextView = view.findViewById(R.id.product_offer);
        TextView stockAvailabilityTextView = view.findViewById(R.id.product_stock);
        ImageView itemImageView = view.findViewById(R.id.product_image);
        TextView saleDataTextView = view.findViewById(R.id.product_sale_data);
        TextView customerReviewTextView = view.findViewById(R.id.product_quality);
        TextView categoryTextView = view.findViewById(R.id.product_category);
        final TextView quantityTextView = view.findViewById(R.id.product_stock_value_current);
        ImageView saleImageView = view.findViewById(R.id.sell_on_click);

        // Find the columns of stock attributes that we're interested in
        int columnIdColumnIndex = cursor.getColumnIndex(StockEntry._ID);
        int productNameColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_PRODUCT_NAME);
        int supplierColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_PRODUCT_SUPPLIER);
        int rupeeColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_RUPEES);
        int offerColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_OFFER);
        int stockAvailabilityColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_STOCK_AVAILABILITY);
        int imageColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_IMAGE);
        int saleDataColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_SALE_DATA);
        int customerReviewColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_CUSTOMER_REVIEW);
        int categoryColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_CATEGORY);
        int quantityColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_STOCK_QUANTITY);

        // Read the stocks attributes from the Cursor for the current stock
        String stockProductName = cursor.getString(productNameColumnIndex);
        final int mId = cursor.getInt(columnIdColumnIndex);
        String stockSupplier = cursor.getString(supplierColumnIndex);
        String stockRupee = cursor.getString(rupeeColumnIndex);
        String stockOffer = cursor.getString(offerColumnIndex);
        String stockImageView = cursor.getString(imageColumnIndex);
        final int mQuantity = cursor.getInt(quantityColumnIndex);

        // Set on click listener to manage quantity value for user
        saleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get reference from StocksActivity to sell one item
                StocksActivity.sellItem(mId, mQuantity);
            }
        });

        /*
         * Get selected spinner value as TextView from editor class to stocks activity for selected
         * spinner value , following are the values from column index into stock information
         * STOCK_UNKNOWN sets value UnKnown , STOCK_AVAILABLE sets value In Stock
         * in default case stock is NOt Available
         */
        int stockInformation = cursor.getInt(stockAvailabilityColumnIndex);
        String stockInformationString;
        switch (stockInformation) {
            case StockEntry.STOCK_UNKNOWN:
                stockInformationString = context.getString(R.string.unknown);
                break;
            case StockEntry.STOCK_AVAILABLE:
                stockInformationString = context.getString(R.string.in_stock);
                break;
            default:
                stockInformationString = context.getString(R.string.no_stock);
        }

        /*
         * Get selected spinner value as TextView from editor class to stocks activity for selected
         * spinner value , following are the values from column index into stock information
         * SALE_DATA_PROFIT sets value Profit
         * in default case sale data is Loss
         */
        int saleDataInformation = cursor.getInt(saleDataColumnIndex);
        String saleDataInformationString;
        switch (saleDataInformation) {
            case StockEntry.SALE_DATA_PROFIT:
                saleDataInformationString = context.getString(R.string.profit);
                break;
            default:
                saleDataInformationString = context.getString(R.string.loss);
        }

        /*
         * Get selected spinner value as TextView from editor class to stocks activity for selected
         * spinner value , following are the values from column index into stock information
         * REVIEW_GOOD_PRODUCT sets value Good Product,
         * REVIEW_AVERAGE_PRODUCT sets value Average Product
         * in default case customer review is Bad product
         */
        int customerReviewInformation = cursor.getInt(customerReviewColumnIndex);
        String customerReviewInformationString;
        switch (customerReviewInformation){
            case StockEntry.REVIEW_GOOD_PRODUCT:
                customerReviewInformationString = context.getString(R.string.good);
                break;
            case StockEntry.REVIEW_AVERAGE_PRODUCT:
                customerReviewInformationString = context.getString(R.string.average);
                break;
            default:
                customerReviewInformationString = context.getString(R.string.bad);
        }

        /*
         * Get selected spinner value as TextView from editor class to stocks activity for selected
         * spinner value , following are the values from column index into stock information
         * CATEGORY_ELECTRONICS sets value Electronics,
         * CATEGORY_CLOTHES sets value Clothes
         * in default case category is video games
         */
        int categoryInformation = cursor.getInt(categoryColumnIndex);
        String categoryInformationString;
        switch (categoryInformation) {
            case StockEntry.CATEGORY_ELECTRONICS:
                categoryInformationString = context.getString(R.string.electronics);
                break;
            case StockEntry.CATEGORY_CLOTHES:
                categoryInformationString = context.getString(R.string.clothes);
                break;
            default:
                categoryInformationString = context.getString(R.string.video_games);
        }

        // Update the TextViews with the attributes for the current stock
        productNameTextView.setText(stockProductName);
        supplierTextView.setText(stockSupplier);
        rupeesTextView.setText(stockRupee);
        offerTextView.setText(stockOffer);
        stockAvailabilityTextView.setText(stockInformationString);
        itemImageView.setImageURI(Uri.parse(stockImageView));
        saleDataTextView.setText(saleDataInformationString);
        customerReviewTextView.setText(customerReviewInformationString);
        categoryTextView.setText(categoryInformationString);
        quantityTextView.setText(String.valueOf(mQuantity));
    }

}