package com.example.mszhapa.mszhapaboutiqueinventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.mszhapa.mszhapaboutiqueinventory.data.ClothesContract.ClothesEntry;

/**
 * Created by MsZhapa on 15/07/2017.
 */

public class InventoryActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the pet data loader
     */
    private static final int CLOTHES_LOADER = 0;

    /**
     * Adapter for the ListView
     */
    ClothesCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_activity);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InventoryActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the pet data
        ListView clothesListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        clothesListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of pet data in the Cursor.
        // There is no pet data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new ClothesCursorAdapter(this, null);
        clothesListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        clothesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(InventoryActivity.this, EditorActivity.class);

                Uri currentClothesUri = ContentUris.withAppendedId(ClothesEntry.CONTENT_URI, id);
                intent.setData(currentClothesUri);
                startActivity(intent);

            }

        });
        // Kick off the loader
        getLoaderManager().initLoader(CLOTHES_LOADER, null, this);
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void insertItem() {
        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(ClothesEntry.COLUMN_CLOTHES_NAME, "H&M garment");
        values.put(ClothesEntry.COLUMN_CLOTHES_TYPE, ClothesEntry.TYPE_OTHER);
        values.put(ClothesEntry.COLUMN_CLOTHES_PRICE, 7);
        values.put(ClothesEntry.COLUMN_CLOTHES_QUANTITY, 8);
        values.put(ClothesEntry.COLUMN_CLOTHES_SUPPLIER_NAME, "ALEXA");
        values.put(ClothesEntry.COLUMN_CLOTHES_SUPPLIER_EMAIL, "ana@fg.cim");


        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(ClothesEntry.CONTENT_URI, values);
    }

    /**
     * Helper method to delete all pets in the database.
     */
    private void deleteAllClothes() {
        int rowsDeleted = getContentResolver().delete(ClothesEntry.CONTENT_URI, null, null);
        Log.v("InventoryActivity", rowsDeleted + " rows deleted from clothes database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertItem();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllClothes();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                ClothesEntry._ID,
                ClothesEntry.COLUMN_CLOTHES_NAME,
                ClothesEntry.COLUMN_CLOTHES_PRICE,
                ClothesEntry.COLUMN_CLOTHES_QUANTITY,
                ClothesEntry.COLUMN_CLOTHES_IMAGE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                ClothesEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link PetCursorAdapter} with this new cursor containing updated pet data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}