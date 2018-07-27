package com.example.aaron.maptest2;
/**
 * CONTACT MANAGER V4
 * Created by Zachary Aaron Greene on 4/16/2018.
 *      credits located below.
 *
 * This is the Controller class for the database as part of the MVC Architecture.
 * It renders the RecyclerView view type with a defined layout and given data; it is what asks the database for information.
 *
 * I used a bazillion tutorials.
 *
 *  ******** HOW TO USE ********
 *      1) when the application starts, you will get a screen that says "no contacts" and a button to create one in the bottom right.
 *      2) when you create new contact, you will get a prompt with fields. Fill in the required fields with whatever you want, there
 *          is no input checking beyond that it exists. To select a field, click on it and it will bring up the appropriate android
 *          standard keyboard to fill in that field.
 *          2.1) As designed, currently First name, Last name, and Phone number are required fields.
 *          2.2) further buttons are descriptive and do as they indicate they should.
 *      3) on the contact screen, hold and scroll to view multiple contacts. Contacts are displayed with last name first, followed by first name.
 *          Contacts are also displayed with their creation date tooltipped above the contact.
 *      4) if you would like to view, edit, or delete a contact, press and hold on the selected contact until a new dialog box shows.
 *          4.1) from this new dialog box you may select the "view & edit" box for viewing and editing
 *          4.2) select "show on map" to show the person's address on the map. Brings up a loading screen and then a map activity that center-zooms on their location.
 *              4.2.1) If loading succeeds, it just moves on to the map.
 *              4.2.2) If loading the address fails, the application will never transition to the maps page. From here, the user should use the back button on their phone.
 *          4.3) select delete to delete
 *          4.4) further buttons are descriptive and do as they indicate they should.
 *      5) Notice that the list always displays in order of Last name, regardless of case. You can reverse the order by shaking the device. Kind of buggy, though.
 *
 *  ******** Missing / Obsolete / Future Features ********
 *      Obsolete: Part 1's final I/O for contact storage. NOTE: was never implemented, as part 1 was not completed.
 *      Missing: Currently, the app does not support part 3's "edit contact creation date" feature.
 *      Missing: Currently, the app does not support part 4's "distance from user location to contact address on map" feature.
 *
 *      Bug: Currently, newly inserted contacts will not display their creation date. Upon restarting the application, they will be displayed.
 *          // I have not investigated how to fix this.
 *      Bug: Once you click on an entry to insert text in the Edit / View contact screen, you cannot back out until you have reached the end of the fields.
 *          // I do not know how to fix this, as it requires more knowledge about how editText works.
 *
 *  ******** RESOURCES ********
 *      *** Initial research:
 * This document was modeled with help from an application designed back in 2008. The guided code is linked below:
 *      http://www.cs.trincoll.edu/hfoss/wiki/Tutorial:Making_a_Contacts_Application
 * I realized that wasn't going to be much help as I had to redesign this under DataBaseHelper since older methods were depreciated.
 * I wasted a lot of time until I realized this was the case.
 *
 *      *** Key Resource:
 * http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
 *      THIS ONE HELPED A LOT, ALL OF THESE FUNCTIONS ARE ADAPTED FROM RAVI TAMADA'S GUIDE. I cannot thank this guy enough.
 *      This particular class is adpated from the NotesAdapter.java as listed in the guide linked above.
 * http://jasonmcreynolds.com/?p=388
 *      In particular helped a lot in demonstrating a successful way to use SensorEventListener, a custom interface, and some parameters I could reference for shaking.
 *
 *      *** Other useful resources:
 * https://developer.android.com/reference/android/database/sqlite/package-summary.html
 * https://developer.android.com/reference/android/database/package-summary.html
 * https://stackoverflow.com/questions/12015731/android-sqlite-example
 * https://stackoverflow.com/questions/43495549/cannot-install-repository-and-sync-project-in-android-studio
 * https://www.androidauthority.com/get-location-address-android-app-628764/
 * https://github.com/obaro/SimpleGeocodeApp/blob/master/app/src/main/java/com/sample/foo/simplegeocodeapp/MainActivityWithAsyncTask.java
 * https://www.journaldev.com/15676/android-geocoder-reverse-geocoding
 * https://www.androidauthority.com/get-location-address-android-app-628764/
 * https://stackoverflow.com/questions/14827532/waiting-till-the-async-task-finish-its-work
 * https://stackoverflow.com/questions/16252269/how-to-sort-an-arraylist
 * https://docs.oracle.com/javase/7/docs/api/java/util/Collections.html
 * https://docs.oracle.com/javase/tutorial/java/data/comparestrings.html
 * https://stackoverflow.com/questions/5271448/how-to-detect-shake-event-with-android
 * https://developer.android.com/guide/topics/sensors/sensors_overview.html
 * https://developer.android.com/guide/topics/sensors/sensors_motion.html
 *
 *      *** Special Thanks:
 *      Android studio, for highlighting all the disrepancies between the adapted code and my own with nice red bits which make editing in my changes so much easier.
 *      I saved a lot of time with this feature. The error detection and autocompletion are really good, and I saved a lot of time on this compared to past IDEs I have
 *      worked with.
 */
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // class variables
    private ContactAdapter myAdapter;
    private List<Contact> contactList = new ArrayList<>();
    private RecyclerView recyclerView;                  // chosen viewtype
    private TextView noContactsView;                   // etc.

    private DataBaseManager db;     // the database access and declaration

    private SensorManager sensorManager;    // sensormanager for managing accelerometer readings
    private Sensor aclrmeter;               // accelerometer object
    private ShakeDetection shakeDetector;   // shake detection for managing shake detection

    private boolean sortOrder;  // flag representing order of displaying contacts, whether descending (true), or ascending (false).


    // ****************************
    // BEGIN ACTIVITY LIFECYCLE

    // ********* ON CREATE

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //admin stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // layout instantiation
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        noContactsView = (TextView) findViewById(R.id.empty_notes_view);

        // instantiate our database
        db = new DataBaseManager(this);

        // reset the sort ordering flag
        sortOrder = true;

        // load from database and populate our list
        contactList.addAll(db.getAllContacts());
        Collections.sort(contactList);  // sort the list

        // Sensor and Shake instantiation
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);     // tie our sensorManager to device sensor services
        aclrmeter = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);      // make our sensor an accelerometer
        shakeDetector = new ShakeDetection();       // create shake detection object
        shakeDetector.setOnShakeListener(new ShakeDetection.OnShakeListener() {     //initiate onShake interface
            @Override
            public void onShake() {
                // THIS CODE HAPPENS WHEN SHAKE IS DETECTED.

                //flip sortOrder
                if(sortOrder) { sortOrder = false; }
                else { sortOrder = true; }

                refreshList();
            }
        });


        // if we click on something or w/e
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addContact);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showContactDialog(false, null, -1);
            }
        });

        // create the controller for DB and view management
        myAdapter = new ContactAdapter(this, contactList);

        //admin stuff for getting the layout working with recyclerview
        RecyclerView.LayoutManager myLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(myLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());    // for fun, because everything looks better when it moves
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));    // for fun, orients a vertical divider with 16 spacing between contact list items. Makes it look good.
        recyclerView.setAdapter(myAdapter);

        toggleNoContacts();  // check to see if we have no contacts or not, if so, disable the no contacts message on the screen.

        // okay here comes the fun part. WE LONG PRESS IT and something different happens. This is for the options to edit and delete, which are made further on in the program.
        // dang, recyclerview is really handy
        // of note, the following is borrowed directly from the MainActivity of RAVI TAMADA's Notes Application as shown in the link to the tutorial at the top credits.
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener()
        {
            @Override
            public void onClick(View view, final int position) {}

            @Override
            public void onLongClick(View view, int position)
            {
                showActionsDialog(position);
            }
        }
        ));
        // don'tcha just love these overtly long statements and overrides within statements, just the best.
    }

    // ********* ON RESUME

    @Override
    protected void onResume()
    {
        super.onResume();
        // mandatory code for resuming sensor onResume
        sensorManager.registerListener(shakeDetector, aclrmeter, SensorManager.SENSOR_DELAY_UI);
    }

    // ********* ON PAUSE

    @Override
    protected void onPause()
    {
        // mandatory code for releasing the sensor for onPause
        sensorManager.unregisterListener(shakeDetector);
        super.onPause();
    }

    // END ACTIVITY LIFECYCLE
    // ****************************
    // THE REST OF THESE ARE MEMBER FUNCTIONS.


    // the following function adds a new contact to the database contact object list, and refreshes our viewed list
    private void createContact(String firstName, String lastName, String middleInitial, String phone, String bDate, String address1, String address2, String city, String state, String zip) {
        // inserting contact & get its ID
        long id = db.insertContact(firstName, lastName, middleInitial, phone, bDate, address1, address2, city, state, zip);

        // get the newly inserted note from db
        Contact contact = db.getContact(id);

        //safety feature; we make sure the contact was successfully inserted, and if it is THEN we add it to the list.
        // this means app still functions even if the DB doesn't.
        if (contact != null) {
            contactList.add(0, contact);   // concatonate a new contact at the head of the list.
            refreshList();
            //used to be myAdapter.notifyDataSetChanged();    // refresh the list

            toggleNoContacts();  // check to see if we have no contacts or not, if so, disable the no contacts message on the screen.
        }
    }

    // the following function updates an existing contact in the database contact object list, and refreshes our viewed list
    // note this should still work even though we aren't checking for changes because the existing fields are re-imported.
    private void updateContact(String firstName, String lastName, String middleInitial, String phone, String bDate, String address1, String address2, String city, String state, String zip, int position) {
        // create temp contact for transport
        Contact contact = contactList.get(position);

        // reapply information
        contact.setFirstName(firstName);
        contact.setLastName(lastName);
        contact.setMiddleInitial(middleInitial);
        contact.setPhone(phone);
        contact.setbDate(bDate);
        contact.setAddress1(address1);
        contact.setAddress2(address2);
        contact.setCity(city);
        contact.setState(state);
        contact.setZip(zip);

        // now we update the database with this temporary contact
        db.updateContact(contact);

        // refresh the list by calling refreshList and setting new contact position.
        contactList.set(position, contact); // set this changed new contact as the one at that position. Not sure if this garbage collects.
        refreshList();  // refresh the list, see below
        // used to be myAdapter.itemChanged(position)

        toggleNoContacts();  // check to see if we have no contacts or not, if so, disable the no contacts message on the screen.
    }

    // function for removing things. Does it by position.
    private void deleteContact(int position) {
        // go straight into removing it from the database.
        db.deleteContact(contactList.get(position));

        // remove from list and update; no need to call refreshList because we are just removing.
        contactList.remove(position);
        myAdapter.notifyItemRemoved(position);

        toggleNoContacts();  // check to see if we have no contacts or not, if so, disable the no contacts message on the screen. If not, re-enable it.
    }

    // simply checks if we have no contacts or not for display of the no contacts message.
    private void toggleNoContacts() {
        // we do this from the database instead of the List because the database is the long term storage and everything is based off of it anyway.
        if (db.getContactsCount() > 0)     // we have contacts, so make the message go away.
            noContactsView.setVisibility(View.GONE);
        else                            // we DONT have contacts, so make the message appear.
            noContactsView.setVisibility(View.VISIBLE);
    }

    // now we get into the heavier stuff.


    // the following is almost unchanged from the original MainActivity of RAVI TAMADA in the guide I link at the top credits. Only a few variable names.
    //      this is a UI element that pops up when we hold down on the contact in the RecyclerView list of contacts, as done earlier in the activity lifecycle.
    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit or View", "Show on map", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0)
                {
                    showContactDialog(true, contactList.get(position), position);       // so if we chose edit, then we go into the super awesome dialog screen
                }
                else if(which == 1)
                {
                    // transition to map screen for contact
                    // create intent for transfer of data.

                    Intent loadingIntent = new Intent(MainActivity.this, AddressLoading.class);

                    //prep information for transfer, in particular address information
                    // WARNING: WE DO NOT CHECK FOR INCORRECT OR NOT ENTERED ADDRESSES. If the fields are all null, then null is passed.
                    Bundle bundle = new Bundle();

                    bundle.putString("firstName", contactList.get(position).getFirstName());
                    bundle.putString("lastName", contactList.get(position).getLastName());
                    bundle.putString("address1", contactList.get(position).getAddress1());
                    bundle.putString("address2", contactList.get(position).getAddress2());
                    bundle.putString("city", contactList.get(position).getCity());
                    bundle.putString("state", contactList.get(position).getState());
                    bundle.putString("zip", contactList.get(position).getZip());

                    loadingIntent.putExtras(bundle);

                    // transition to map activity
                    startActivity(loadingIntent);

                }
                else
                {
                    deleteContact(position);        // choose delete, so delete the contact.
                }
            }
        });
        builder.show();
    }

    // this is a UI element that pops up when we hold down on the contact in the RecyclerView list of contacts, as done earlier in the activity lifecycle.
    // it gives us an the EditText with UI elements for entering and editing a selected contact.
    // when shouldUpdate is true, the old contact is displayed and the button text is changed to Update
    private void showContactDialog(final boolean shouldUpdate, final Contact contact, final int position)
    {
        // the following several lines set up the pop up for the contact for display, editing, and deletion.
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.contact_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);


        //alright. the rest of this is where it gets... doozy.

        //get a bunch of edittext fields.
        //  This was originally one edittext field.
        final EditText inputFirst = view.findViewById(R.id.contactFirstName);
        final EditText inputLast = view.findViewById(R.id.contactLastName);
        final EditText inputMid = view.findViewById(R.id.contactMidInit);
        final EditText inputPhone = view.findViewById(R.id.contactPhone);
        final EditText inputBDate = view.findViewById(R.id.contactBDate);
        final EditText inputAdd1 = view.findViewById(R.id.contactAddress1);
        final EditText inputAdd2 = view.findViewById(R.id.contactAddress2);
        final EditText inputCity = view.findViewById(R.id.contactCity);
        final EditText inputState = view.findViewById(R.id.contactState);
        final EditText inputZip = view.findViewById(R.id.contactZip);

        //get the title of the dialog.
        TextView dialogTitle = view.findViewById(R.id.dialog_title);

        // shows us the new or update contact title for the dialog, depending on what we are doing.
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_contact_title) : getString(R.string.lbl_edit_contact_title));

        // if we are updateing and contact exists, fill in the update fields with existing data
        if (shouldUpdate && contact != null)
        {
            inputFirst.setText(contact.getFirstName());
            inputLast.setText(contact.getLastName());
            inputMid.setText(contact.getMiddleInitial());
            inputPhone.setText(contact.getPhone());
            inputBDate.setText(contact.getbDate());
            inputAdd1.setText(contact.getAddress1());
            inputAdd2.setText(contact.getAddress2());
            inputCity.setText(contact.getCity());
            inputState.setText(contact.getState());
            inputZip.setText(contact.getZip());
        }

        // view stuff...
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        // more view stuff...
        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        // we click on something now... more more view stuff...
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // The following several messages are to make sure the user doesn't leave any required fields empty using the Toast message prompt.
                //      required fields: First name, Last name, Phone
                //      works from the top of the list to the bottom; does not detect multiple empty fields.
                if (TextUtils.isEmpty(inputFirst.getText().toString()))     // this case is for first name
                {
                    Toast.makeText(MainActivity.this, "Enter first name!", Toast.LENGTH_SHORT).show();
                    return;     //exit
                }
                else if(TextUtils.isEmpty(inputLast.getText().toString())) // this case is for last name
                {
                    Toast.makeText(MainActivity.this, "Enter last name!", Toast.LENGTH_SHORT).show();
                    return;     // exit
                }
                else if(TextUtils.isEmpty(inputPhone.getText().toString())) // this case is for last name
                {
                    Toast.makeText(MainActivity.this, "Enter phone number!", Toast.LENGTH_SHORT).show();
                    return;     // yes this exits
                }
                else    // in this case, no required fields were left blank
                {
                    alertDialog.dismiss();
                }

                // check if user updating the contact, and that it exists.
                if (shouldUpdate && contact != null)    // we are updating and contact exists, so we use the update function.
                {
                    // update contact by it's id. Really long statement because updateContact takes a lot of fields.
                    updateContact(inputFirst.getText().toString(), inputLast.getText().toString(), inputMid.getText().toString(), inputPhone.getText().toString(), inputBDate.getText().toString(), inputAdd1.getText().toString(), inputAdd2.getText().toString(), inputCity.getText().toString(), inputState.getText().toString(), inputZip.getText().toString(), position);
                }
                else    // we are creating a new note or contact doesn't exist, in which case we create a new note and put it into the database.
                {
                    createContact(inputFirst.getText().toString(), inputLast.getText().toString(), inputMid.getText().toString(), inputPhone.getText().toString(), inputBDate.getText().toString(), inputAdd1.getText().toString(), inputAdd2.getText().toString(), inputCity.getText().toString(), inputState.getText().toString(), inputZip.getText().toString());
                }
            }
        });
    }

    // a quick function for resorting and refreshing the contact list.
    public void refreshList()
    {
        if(sortOrder)   // sort list in descending last name order, regardless of case.
        {
            Collections.sort(contactList);  // sort the list
        }
        else    // sort list in ascending last name order, regardless of case.
        {
            Collections.sort(contactList);  // sort the list
            Collections.reverse(contactList); // reverse the list order
        }

        myAdapter.notifyDataSetChanged();   // refresh the list on the screen
    }
}