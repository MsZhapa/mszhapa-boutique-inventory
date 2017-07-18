package com.example.mszhapa.mszhapaboutiqueinventory;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
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
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mszhapa.mszhapaboutiqueinventory.data.ClothesContract;
import com.example.mszhapa.mszhapaboutiqueinventory.data.ClothesContract.ClothesEntry;

import java.io.File;


/**
 * Created by MsZhapa on 15/07/2017.
 */

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the clothes data loader
     */
    private static final int EXISTING_CLOTHES_LOADER = 0;

    private static final String TAG = EditorActivity.class.getSimpleName();

    /**
     * Content URI for the existing item (null if it's a new item)
     */
    private Uri mCurrentClothesUri;

    /**
     * EditText field to enter the name
     */
    private EditText mNameEditText;

    /**
     * Spinner for the type of clothing
     */
    private Spinner mTypeSpinner;

    /**
     * EditText field to enter the price
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the quantity
     */
    private EditText mQuantityEditText;

    /**
     * ImageButton for adding a new picture
     */
    private ImageButton mImageBtn;

    /**
     * String for images
     */
    private String mCurrentPhotoUri = "no images";

    /**
     * ImageView for item picture
     */
    private ImageView mItemImage;

    /**
     * ImageButton for adding item
     */
    private ImageButton mAddItem;

    /**
     * ImageButton for subtracting an item
     */
    private ImageButton mSubtractItem;

    /**
     * Requests for adding a new picture
     */
    public static final int PICK_PHOTO_REQUEST = 20;

    public static final int EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE = 21;

    /**
     * Int for type of item
     */
    private int mType = ClothesEntry.TYPE_OTHER;

    /**
     * EditText for adding supplier name
     */
    private EditText mSupplierNameEditText;

    /**
     * EditText for adding a supplier email
     */
    private EditText mSupplierEmailEditText;

    /**
     * Boolean flag that keeps track of whether the pet has been edited (true) or not (false)
     */
    private boolean mClothesHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mClothesHasChanged boolean to true.
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
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new item or editing an existing one.
        Intent intent = getIntent();
        mCurrentClothesUri = intent.getData();

        // If the intent DOES NOT contain an item content URI, then we know that we are
        // creating a new item.
        // If the intent DOES NOT contain a clothes' content URI, then we know that we are
        // creating a new one.
        if (mCurrentClothesUri == null) {
            // This is a new item, so change the app bar to say "New Item"
            setTitle(getString(R.string.editor_activity_title_new_item));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete item that hasn't been created yet.)
            invalidateOptionsMenu();
            mAddItem = (ImageButton) findViewById(R.id.add_quantity);
            mSubtractItem = (ImageButton) findViewById(R.id.subtract_quantity);
            mSubtractItem.setVisibility(View.INVISIBLE);
            mAddItem.setVisibility(View.INVISIBLE);
        } else {
            // Otherwise this is an existing one, so change app bar to say "Edit Item"
            setTitle(getString(R.string.editor_activity_title_edit_item));

            // Initialize a loader to read the data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_CLOTHES_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mItemImage = (ImageView) findViewById(R.id.product_image);
        mImageBtn = (ImageButton) findViewById(R.id.image_selector);
        mNameEditText = (EditText) findViewById(R.id.edit_clothes_name);
        mTypeSpinner = (Spinner) findViewById(R.id.spinner_type);
        mPriceEditText = (EditText) findViewById(R.id.edit_clothes_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_item_quantity);
        mAddItem = (ImageButton) findViewById(R.id.add_quantity);
        mSubtractItem = (ImageButton) findViewById(R.id.subtract_quantity);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        mSupplierEmailEditText = (EditText) findViewById(R.id.edit_supplier_email);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.

        mNameEditText.setOnTouchListener(mTouchListener);
        mTypeSpinner.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierEmailEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);

        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPhotoProductUpdate(view);
                mClothesHasChanged = true;
            }
        });

        setupTypeSpinner();
    }
    //method for verifying permissions for picture and adding to database
    public void onPhotoProductUpdate(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //We are on M or above so we need to ask for runtime permissions
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                invokeGetPhoto();
            } else {
                // we are here if we do not all ready have permissions
                String[] permisionRequest = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permisionRequest, EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE);
            }
        } else {
            //We are on an older devices so we dont have to ask for runtime permissions
            invokeGetPhoto();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_REQUEST_PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //We got a GO from the user
            invokeGetPhoto();
        } else {
            Toast.makeText(this, R.string.editor_insert_item_failed, Toast.LENGTH_LONG).show();
        }
    }

    private void invokeGetPhoto() {
        // invoke the image gallery using an implict intent.
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

        // where do we want to find the data?
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();
        // finally, get a URI representation
        Uri data = Uri.parse(pictureDirectoryPath);

        // set the data and type.  Get all image types.
        photoPickerIntent.setDataAndType(data, "image/*");

        // we will invoke this activity, and get something back from it.
        startActivityForResult(photoPickerIntent, PICK_PHOTO_REQUEST);
    }

    /**
     * Setup the dropdown spinner that allows the user to select the type of the item.
     */
    private void setupTypeSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter typeSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_type_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        typeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mTypeSpinner.setAdapter(typeSpinnerAdapter);

        // Set the integer mTypeSpinner to the constant values
        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.type_pants))) {
                        mType = ClothesContract.ClothesEntry.TYPE_PANTS;
                    } else if (selection.equals(getString(R.string.type_top))) {
                        mType = ClothesContract.ClothesEntry.TYPE_TOP;
                    } else {
                        mType = ClothesContract.ClothesEntry.TYPE_OTHER;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mType = ClothesContract.ClothesEntry.TYPE_OTHER;
            }
        });
    }

    /**
     * Get user input from editor and save item into database.
     */
    private void saveItem() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierEmailString = mSupplierEmailEditText.getText().toString().trim();

        // Check if this is supposed to be a new item
        // and check if all the fields in the editor are blank
        if (mCurrentClothesUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && mType == ClothesEntry.TYPE_OTHER && TextUtils.isEmpty(supplierNameString) && TextUtils.isEmpty(supplierEmailString)) {
            // Since no fields were modified, we can return early without creating a new pet.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            Toast.makeText(this, R.string.editor_insert_item_failed, Toast.LENGTH_SHORT).show();
            // No change has been made so we can return
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and clothes' attributes from the editor are the values.
        ContentValues values = new ContentValues();

        values.put(ClothesEntry.COLUMN_CLOTHES_NAME, nameString);
        values.put(ClothesEntry.COLUMN_CLOTHES_TYPE, mType);

        int price = 0;
        if (!TextUtils.isEmpty(priceString)) {
            price = Integer.parseInt(priceString);
        }
        values.put(ClothesEntry.COLUMN_CLOTHES_PRICE, price);

        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        values.put(ClothesEntry.COLUMN_CLOTHES_QUANTITY, quantity);
        values.put(ClothesEntry.COLUMN_CLOTHES_SUPPLIER_NAME, supplierNameString);
        values.put(ClothesEntry.COLUMN_CLOTHES_SUPPLIER_EMAIL, supplierEmailString);
        values.put(ClothesEntry.COLUMN_CLOTHES_IMAGE, mCurrentPhotoUri);

        // Determine if this is a new or existing pet by checking if mCurrentClothesUri is null or not
        if (mCurrentClothesUri == null) {
            // This is a NEW item, so insert a new item into the provider,
            // returning the content URI for the new item.
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
            // Otherwise this is an EXISTING item, so update the item with content URI: mCurrentClothesUri
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
        // If this is a new item, hide the "Delete" and "Restock" menu item.
        if (mCurrentClothesUri == null) {
            MenuItem deleteItem = menu.findItem(R.id.action_delete);
            MenuItem restockItem = menu.findItem(R.id.action_restock);
            deleteItem.setVisible(false);
            restockItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save item to database
                saveItem();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_restock:
                showRestockConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the item hasn't changed, continue with navigating up to parent activity
                // which is the {@link InventoryActivity}.
                if (!mClothesHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
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
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
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
        // If the item hasn't changed, continue with handling back button press
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_PHOTO_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                //If we are here, everything processed successfully and we have an Uri data
                Uri mProductPhotoUri = data.getData();
                mCurrentPhotoUri = mProductPhotoUri.toString();
                Log.d(TAG, "Selected images " + mProductPhotoUri);

                //We use Glide to import photo images
                Glide.with(this).load(mProductPhotoUri)
                        .placeholder(R.drawable.pic)
                        .error(R.drawable.pic)
                        .crossFade()
                        .fitCenter()
                        .into(mItemImage);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all item attributes, define a projection that contains
        // all columns from the item table
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
            // Find the columns of item attributes that we're interested in
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
                    .error(R.drawable.add)
                    .crossFade()
                    .centerCrop()
                    .into(mItemImage);

            // Update the TextViews with the attributes for the current pet

            mNameEditText.setText(name);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mSupplierNameEditText.setText(supplierName);
            mSupplierEmailEditText.setText(supplierEmail);

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
                            mQuantityEditText.setText(newQu);
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
                            mQuantityEditText.setText(newQu);
                        } else {
                            mAddItem.setEnabled(false);
                        }
                    }
                }
            });
            // Type is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options.
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (type) {
                case ClothesEntry.TYPE_PANTS:
                    mTypeSpinner.setSelection(1);
                    break;
                case ClothesEntry.TYPE_TOP:
                    mTypeSpinner.setSelection(2);
                    break;
                default:
                    mTypeSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mTypeSpinner.setSelection(0);
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierNameEditText.setText("");
        mSupplierEmailEditText.setText("");

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

    /**
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showRestockConfirmationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.send_email_dialog);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        builder.setPositiveButton(R.string.send_email, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
            saveItem();
            // intent to email
            Intent intent = new Intent(android.content.Intent.ACTION_SENDTO);
            intent.setType("text/plain");
            intent.setData(Uri.parse("mailto:" + mSupplierEmailEditText.getText().toString().trim()));
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Item sold out -- Need more");
            String restockMessage = "The product has sold out, more are welcome. " +
                    mNameEditText.getText().toString().trim() +
                    "!!!";
            intent.putExtra(android.content.Intent.EXTRA_TEXT, restockMessage);
            startActivity(intent);
        }
    });
    AlertDialog alertDialog = builder.create();
        alertDialog.show();
}
}