package com.example.mszhapa.mszhapaboutiqueinventory;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mszhapa.mszhapaboutiqueinventory.data.ClothesContract.ClothesEntry;

/**
 * Created by MsZhapa on 15/07/2017.
 */

public class ClothesCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link ClothesCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ClothesCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.item_name);
        TextView priceTextView = (TextView) view.findViewById(R.id.item_price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.item_quantity);
        ImageView imageView = (ImageView) view.findViewById(R.id.item_image);

        // Find the columns of pet attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ClothesEntry.COLUMN_CLOTHES_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ClothesEntry.COLUMN_CLOTHES_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ClothesEntry.COLUMN_CLOTHES_QUANTITY);
        int imageColumnIndex = cursor.getColumnIndex(ClothesEntry.COLUMN_CLOTHES_IMAGE);


        // Read the pet attributes from the Cursor for the current pet
        String clothesName = cursor.getString(nameColumnIndex);
        int price = cursor.getInt(priceColumnIndex);
        int quantity = cursor.getInt(quantityColumnIndex);
        Uri imageUri = Uri.parse(cursor.getString(imageColumnIndex));


        // Update the TextViews with the attributes for the current pet
        nameTextView.setText(clothesName);
        priceTextView.setText(Integer.toString(price));
        quantityTextView.setText(Integer.toString(quantity));
        Glide.with(context).load(imageUri)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.drawable.add)
                .crossFade()
                .centerCrop()
                .into(imageView);

    }
}
