package com.example.mszhapa.mszhapaboutiqueinventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by MsZhapa on 15/07/2017.
 */

public final class ClothesContract{

        // To prevent someone from accidentally instantiating the contract class,
        // give it an empty constructor.
        private ClothesContract() {}

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
         * For instance, content://com.example.android.pets/pets/ is a valid path for
         * looking at pet data. content://com.example.android.pets/staff/ will fail,
         * as the ContentProvider hasn't been given any information on what to do with "staff".
         */
        public static final String PATH_CLOTHES = "clothes";

/**
 * Inner class that defines constant values for the pets database table.
 * Each entry in the table represents a single pet.
 */
public static final class ClothesEntry implements BaseColumns {

    /** The content URI to access the pet data in the provider */
    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CLOTHES);

    /**
     * The MIME type of the {@link #CONTENT_URI} for a list of pets.
     */
    public static final String CONTENT_LIST_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CLOTHES;

    /**
     * The MIME type of the {@link #CONTENT_URI} for a single pet.
     */
    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CLOTHES;

    /** Name of database table for pets */
    public final static String TABLE_NAME = "clothes";

    /**
     * Unique ID number for the pet (only for use in the database table).
     *
     * Type: INTEGER
     */
    public final static String _ID = BaseColumns._ID;

    /**
     * Name of the pet.
     *
     * Type: TEXT
     */
    public final static String COLUMN_CLOTHES_NAME ="name";

    /**
     * Breed of the pet.
     *
     * Type: TEXT
     */
    public final static String COLUMN_CLOTHES_TYPE = "type";

    /**
     * Gender of the pet.
     *
     * The only possible values are {@link #SUPPLIER_ZARA}, {@link #SUPPLIER_MONKI},
     * or {@link #SUPPLIER_ONLY}.
     *
     * Type: INTEGER
     */
    public final static String COLUMN_CLOTHES_SUPPLIER= "supplier";

    /**
     * Weight of the pet.
     *
     * Type: INTEGER
     */
    public final static String COLUMN_CLOTHES_PRICE = "price";

    public final static String COLUMN_CLOTHES_QUANTITY = "quantity";


    /**
     * Possible values for the gender of the pet.
     */
    public static final int TYPE_OTHER = 0;
    public static final int TYPE_PANTS = 1;
    public static final int TYPE_TOP = 2;


    public static final int SUPPLIER_ONLY = 0;
    public static final int SUPPLIER_ZARA = 1;
    public static final int SUPPLIER_MONKI = 2;

    /**
     * Returns whether or not the given gender is {@link #SUPPLIER_ONLY}, {@link #SUPPLIER_ZARA},
     * or {@link #SUPPLIER_MONKI}.
     */
    public static boolean isValidSupplier(int supplier) {
        if (supplier == SUPPLIER_ONLY || supplier == SUPPLIER_ZARA || supplier == SUPPLIER_MONKI) {
            return true;
        }
        return false;
    }
    public static boolean isValidType(int type) {
        if (type == TYPE_OTHER || type == TYPE_PANTS || type == TYPE_TOP) {
            return true;
        }
        return false;
    }
}

}
