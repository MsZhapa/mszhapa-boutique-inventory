package com.example.mszhapa.mszhapaboutiqueinventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by MsZhapa on 15/07/2017.
 */

public final class ClothesContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private ClothesContract() {
    }

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.mszhapa.mszhapaboutiqueinventory";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     */
    public static final String PATH_CLOTHES = "clothes";

    /**
     * Inner class that defines constant values for the clothes database table.
     * Each entry in the table represents a single item.
     */
    public static final class ClothesEntry implements BaseColumns {

        /**
         * The content URI to access the clothes data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CLOTHES);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of clothes.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CLOTHES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single item.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CLOTHES;

        /**
         * Name of database table for clothes
         */
        public final static String TABLE_NAME = "clothes";

        /**
         * Column names for use in the database table).
         */
        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_CLOTHES_NAME = "name";

        public final static String COLUMN_CLOTHES_TYPE = "type";

        public final static String COLUMN_CLOTHES_PRICE = "price";

        public final static String COLUMN_CLOTHES_QUANTITY = "quantity";

        public final static String COLUMN_CLOTHES_SUPPLIER_NAME = "supplier_name";

        public final static String COLUMN_CLOTHES_SUPPLIER_EMAIL = "supplier_email";

        public final static String COLUMN_CLOTHES_IMAGE = "image";

        /**
         * Possible values for the type of the item.
         */
        public static final int TYPE_OTHER = 0;
        public static final int TYPE_PANTS = 1;
        public static final int TYPE_TOP = 2;


        public static boolean isValidType(int type) {
            if (type == TYPE_OTHER || type == TYPE_PANTS || type == TYPE_TOP) {
                return true;
            }
            return false;
        }
    }

}
