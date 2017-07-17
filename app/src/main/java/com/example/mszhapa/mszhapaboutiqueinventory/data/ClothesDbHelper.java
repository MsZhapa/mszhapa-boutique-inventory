package com.example.mszhapa.mszhapaboutiqueinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mszhapa.mszhapaboutiqueinventory.data.ClothesContract.ClothesEntry;

/**
 * Created by MsZhapa on 15/07/2017.
 */

public class ClothesDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = ClothesDbHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "boutique.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link ClothesDbHelper}.
     *
     * @param context of the app
     */
    public ClothesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_CLOTHES_TABLE =  "CREATE TABLE " + ClothesEntry.TABLE_NAME + " ("
                + ClothesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ClothesEntry.COLUMN_CLOTHES_NAME + " TEXT , "
                + ClothesEntry.COLUMN_CLOTHES_TYPE + " TEXT, "
                + ClothesEntry.COLUMN_CLOTHES_PRICE + " INTEGER NOT NULL, "
                + ClothesEntry.COLUMN_CLOTHES_QUANTITY + " INTEGER NOT NULL,"
                + ClothesEntry.COLUMN_CLOTHES_SUPPLIER_NAME + " TEXT, "
                + ClothesEntry.COLUMN_CLOTHES_SUPPLIER_EMAIL + " TEXT, "
                + ClothesEntry.COLUMN_CLOTHES_IMAGE + " TEXT );";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_CLOTHES_TABLE);
    }
    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}