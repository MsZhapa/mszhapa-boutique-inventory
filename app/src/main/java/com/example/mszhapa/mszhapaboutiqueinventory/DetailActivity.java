package com.example.mszhapa.mszhapaboutiqueinventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mszhapa.mszhapaboutiqueinventory.data.ClothesContract.ClothesEntry;

import static android.R.attr.id;

/**
 * Created by MsZhapa on 17/07/2017.
 */

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the pet data loader
     */
    private static final int EXISTING_CLOTHES_LOADER = 0;

    private static final String TAG = EditorActivity.class.getSimpleName();
    ;

    /**
     * Content URI for the existing pet (null if it's a new pet)
     */
    private Uri mCurrentClothesUri;

    /**
     * EditText field to enter the pet's name
     */
    private TextView mNameTextView;

    /**
     * EditText field to enter the pet's breed
     */
    private TextView mTypeTextView;

    /**
     * EditText field to enter the pet's name
     */
    private TextView mPriceTextView;

    /**
     * EditText field to enter the pet's name
     */
    private EditText mItemQuantityEditText;

    private Button mAddItem;

    private Button mSubtractItem;

    private Button mContactSupplier;


    private ImageView mItemImage;

    private TextView mSupplierNameTextView;

    private TextView mSupplierEmailTextView;

    private Button mDeleteItem;

    private Button mEditItem;

    /**
     * Boolean flag that keeps track of whether the pet has been edited (true) or not (false)
     */
    private boolean mClothesHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mPetHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mClothesHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new pet or editing an existing one.
        Intent intent = getIntent();
        mCurrentClothesUri = intent.getData();

        // If the intent DOES NOT contain a pet content URI, then we know that we are
        // creating a new pet.
        // If the intent DOES NOT contain a pet content URI, then we know that we are
        // creating a new pet.
        if (mCurrentClothesUri != null) {
            // This is a new pet, so change the app bar to say "Add a Pet"
            setTitle(getString(R.string.item_detail));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();
            // Initialize a loader to read the pet data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_CLOTHES_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mItemImage = (ImageView) findViewById(R.id.display_image);
        mNameTextView = (TextView) findViewById(R.id.display_name);
        mTypeTextView = (TextView) findViewById(R.id.display_type);
        mPriceTextView = (TextView) findViewById(R.id.display_price);
        mItemQuantityEditText = (EditText) findViewById(R.id.edit_item_quantity);
        mSupplierNameTextView = (TextView) findViewById(R.id.display_supplier_name);
        mSupplierEmailTextView = (TextView) findViewById(R.id.display_supplier_email);
        mAddItem = (Button) findViewById(R.id.add_item);
        mSubtractItem = (Button) findViewById(R.id.subtract_item);
        mContactSupplier = (Button) findViewById(R.id.contact_supplier);
        mEditItem = (Button) findViewById(R.id.edit_item_button_detail);
        mDeleteItem = (Button) findViewById(R.id.delete_item_button_detail);


        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.

        mItemQuantityEditText.setOnTouchListener(mTouchListener);

        mEditItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, EditorActivity.class);
                Uri currentClothesUri = ContentUris.withAppendedId(ClothesEntry.CONTENT_URI, id);
                intent.setData(currentClothesUri);
                startActivity(intent);
            }
        });

        mDeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), InventoryActivity.class);
                startActivity(i);
            }
        });

        mAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mClothesHasChanged = true;
                saveItem();
            }
        });
        mSubtractItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mClothesHasChanged = true;
                saveItem();
            }
        });
        mContactSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */


    /**
     * Get user input from editor and save pet into database.
     */
    private void saveItem() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space

        String quantityString = mItemQuantityEditText.getText().toString().trim();


        // Check if this is supposed to be a new pet
        // and check if all the fields in the editor are blank
        if (mCurrentClothesUri == null &&
                TextUtils.isEmpty(quantityString)) {
            // Since no fields were modified, we can return early without creating a new pet.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            Toast.makeText(this, R.string.editor_insert_item_failed, Toast.LENGTH_SHORT).show();
            // No change has been made so we can return
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();

        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(ClothesEntry.COLUMN_CLOTHES_QUANTITY, quantity);


        // Determine if this is a new or existing pet by checking if mCurrentClothesUri is null or not
        if (mCurrentClothesUri == null) {
            // This is a NEW pet, so insert a new pet into the provider,
            // returning the content URI for the new pet.
            Uri newUri = getContentResolver().insert(ClothesEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentClothesUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentClothesUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentClothesUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentClothesUri == null) {
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
                // Save pet to database
                saveItem();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mClothesHasChanged) {
                    NavUtils.navigateUpFromSameTask(DetailActivity.this);
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
                                NavUtils.navigateUpFromSameTask(DetailActivity.this);
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
        // If the pet hasn't changed, continue with handling back button press
        if (!mClothesHasChanged) {
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
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                ClothesEntry._ID,
                ClothesEntry.COLUMN_CLOTHES_NAME,
                ClothesEntry.COLUMN_CLOTHES_TYPE,
                ClothesEntry.COLUMN_CLOTHES_PRICE,
                ClothesEntry.COLUMN_CLOTHES_QUANTITY,
                ClothesEntry.COLUMN_CLOTHES_SUPPLIER_NAME,
                ClothesEntry.COLUMN_CLOTHES_SUPPLIER_EMAIL,
                ClothesEntry.COLUMN_CLOTHES_IMAGE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentClothesUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            final int idColumnIndex = cursor.getColumnIndex(ClothesEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(ClothesEntry.COLUMN_CLOTHES_NAME);
            int typeColumnIndex = cursor.getColumnIndex(ClothesEntry.COLUMN_CLOTHES_TYPE);
            int priceColumnIndex = cursor.getColumnIndex(ClothesEntry.COLUMN_CLOTHES_PRICE);
            final int quantityColumnIndex = cursor.getColumnIndex(ClothesEntry.COLUMN_CLOTHES_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ClothesEntry.COLUMN_CLOTHES_SUPPLIER_NAME);
            int supplierEmailColumnIndex = cursor.getColumnIndex(ClothesEntry.COLUMN_CLOTHES_SUPPLIER_EMAIL);
            int imageColumnIndex = cursor.getColumnIndex(ClothesEntry.COLUMN_CLOTHES_IMAGE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int type = cursor.getInt(typeColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierEmail = cursor.getString(supplierEmailColumnIndex);

            // Update the views on the screen with the values from the database
            Uri imageUri = Uri.parse(cursor.getString(imageColumnIndex));

            Glide.with(this).load(imageUri)
                    .placeholder(R.drawable.pic)
                    .error(R.drawable.pic)
                    .crossFade()
                    .centerCrop()
                    .into(mItemImage);
            mNameTextView.setText(name);
            mTypeTextView.setText(Integer.toString(type));
            mPriceTextView.setText(Integer.toString(price));
            mItemQuantityEditText.setText(Integer.toString(quantity));
            mSupplierNameTextView.setText(supplierName);
            mSupplierEmailTextView.setText(supplierEmail);

            final int position = cursor.getPosition();
            mAddItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cursor.moveToPosition(position);
                    int oldQuantity = (cursor.getInt(quantityColumnIndex));
                    if (oldQuantity >= 0) {
                        mAddItem.setEnabled(true);
                        oldQuantity++;
                        if (oldQuantity >= 0) {
                            int quantity = oldQuantity;
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(ClothesEntry.COLUMN_CLOTHES_QUANTITY, quantity);
                            String whereArg = ClothesEntry._ID + " =?";
                            //Get the item id which should be updated
                            int item_id = cursor.getInt(idColumnIndex);
                            String itemIDArgs = Integer.toString(item_id);
                            String[] selectionArgs = {itemIDArgs};
                            int rowsAffected = view.getContext().getContentResolver().update(
                                    ContentUris.withAppendedId(ClothesEntry.CONTENT_URI, item_id),
                                    contentValues,
                                    whereArg, selectionArgs);
                            String newQu = cursor.getString(quantityColumnIndex);
                            mItemQuantityEditText.setText(newQu);
                        } else {
                            mAddItem.setEnabled(false);
                        }

                    }

                }
            });

            mSubtractItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cursor.moveToPosition(position);
                    int oldQuantity = (cursor.getInt(quantityColumnIndex));
                    if (oldQuantity > 0) {
                        mAddItem.setEnabled(true);
                        oldQuantity--;
                        if (oldQuantity >= 0) {
                            int quantity = oldQuantity;
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(ClothesEntry.COLUMN_CLOTHES_QUANTITY, quantity);
                            String whereArg = ClothesEntry._ID + " =?";
                            //Get the item id which should be updated
                            int item_id = cursor.getInt(idColumnIndex);
                            String itemIDArgs = Integer.toString(item_id);
                            String[] selectionArgs = {itemIDArgs};
                            int rowsAffected = view.getContext().getContentResolver().update(
                                    ContentUris.withAppendedId(ClothesEntry.CONTENT_URI, item_id),
                                    contentValues,
                                    whereArg, selectionArgs);
                            String newQu = cursor.getString(quantityColumnIndex);
                            mItemQuantityEditText.setText(newQu);
                        } else {
                            mAddItem.setEnabled(false);
                        }

                    }

                }
            });

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mItemQuantityEditText.setText("");

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
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
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
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
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
     * Perform the deletion of the pet in the database.
     */
    private void deleteItem() {
        // Only perform the delete if this is an existing pet.
        if (mCurrentClothesUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentClothesUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentClothesUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }
}