package com.example.aaron.maptest2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.aaron.maptest2.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zachary Aaron Greene on 4/9/2018.
 *      credits located below.
 *
 * This is the database helper for doing database stuff as part of the Model of the MVC Architecture.
 *
 * I used a bazillion tutorials.
 *
 *      *** Initial research:
 * This document was modeled with help from an application designed back in 2008. The guided code is linked below:
 *      http://www.cs.trincoll.edu/hfoss/wiki/Tutorial:Making_a_Contacts_Application
 * I realized that wasn't going to be much help as I had to redesign this under DataBaseHelper since older methods were depreciated.
 * I wasted a lot of time until I realized this was the case.
 *
 * ******** Key Resource: ********
 * http://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
 *      THIS ONE HELPED A LOT, ALL OF THESE FUNCTIONS ARE ADAPTED FROM RAVI TAMADA'S GUIDE. I cannot thank this guy enough.
 *      This particular class is adapted from the DataBaseHelper.java as listed in the above link.
 *
 *      *** Other useful resources:
 * https://developer.android.com/reference/android/database/sqlite/package-summary.html
 * https://developer.android.com/reference/android/database/package-summary.html
 * https://stackoverflow.com/questions/12015731/android-sqlite-example
 * https://stackoverflow.com/questions/43495549/cannot-install-repository-and-sync-project-in-android-studio
 *
 */

public class DataBaseManager extends SQLiteOpenHelper {
    //declare and initialize class variables

    //these are the names of the table and our database, such that they are referenced in this class. Again, static final
    private static final String DATABASE_NAME = "contactDB";
    private static final int DATABASE_VERSION = 4;  // changed from v1 to v2, once address lines 1 and 2, city, state, and zip were added.

    // setting key names as static final such that keys are immutable. These are used for access in the application.


    // constructor
    public DataBaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //this is called during database creation
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Contact.DATABASE_CREATE);    // pretty simple, just inject the customized table creation into here.  Good lord I hate SQL.
    }

    //this is called during database update
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // drop older table
        database.execSQL("DROP TABLE IF EXISTS " + Contact.TABLE_NAME);

        // create updated table
        onCreate(database);
    }

    // inserting to database, with middle initial and bDate. We can let them be null we don't care
    public long insertContact(String firstName, String lastName, String middleInitial, String phone, String bDate, String address1, String address2, String city, String state, String zip)
    {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues insertionValues = new ContentValues();

        // put in values
        insertionValues.put(Contact.KEY_FIRSTNAME, firstName);
        insertionValues.put(Contact.KEY_LASTNAME, lastName);
        insertionValues.put(Contact.KEY_MIDINITIAL, middleInitial);
        insertionValues.put(Contact.KEY_PHONE, phone);
        insertionValues.put(Contact.KEY_BDATE, bDate);
        insertionValues.put(Contact.KEY_ADRSONE, address1);
        insertionValues.put(Contact.KEY_ADRSTWO, address2);
        insertionValues.put(Contact.KEY_CITY, city);
        insertionValues.put(Contact.KEY_STATE, state);
        insertionValues.put(Contact.KEY_ZIP, zip);

        // insert row
        long id = db.insert(Contact.TABLE_NAME, null, insertionValues);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    // this is for retrieving from the database
    public Contact getContact(long id) {
        // get database in read mode
        SQLiteDatabase db = this.getReadableDatabase();

        // this retrieves from the database. I'm calling the cursor half elf because in a pathfinder homebrew thats what I call my half elves. LEL
        Cursor halfElf = db.query(Contact.TABLE_NAME,
                new String[]{Contact.KEY_ROW_ID, Contact.KEY_FIRSTNAME, Contact.KEY_LASTNAME, Contact.KEY_MIDINITIAL, Contact.KEY_PHONE, Contact.KEY_BDATE, Contact.KEY_ADRSONE, Contact.KEY_ADRSTWO, Contact.KEY_CITY, Contact.KEY_STATE, Contact.KEY_ZIP, Contact.KEY_FIRSTCONTACT},
                Contact.KEY_ROW_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        // required code
        if (halfElf != null)
            halfElf.moveToFirst();

        // create a new contact object for this retrieval.
        Contact contact = new Contact(
                halfElf.getInt(halfElf.getColumnIndex(Contact.KEY_ROW_ID)),
                halfElf.getString(halfElf.getColumnIndex(Contact.KEY_FIRSTNAME)),
                halfElf.getString(halfElf.getColumnIndex(Contact.KEY_LASTNAME)),
                halfElf.getString(halfElf.getColumnIndex(Contact.KEY_MIDINITIAL)),
                halfElf.getString(halfElf.getColumnIndex(Contact.KEY_PHONE)),
                halfElf.getString(halfElf.getColumnIndex(Contact.KEY_BDATE)),
                halfElf.getString(halfElf.getColumnIndex(Contact.KEY_ADRSONE)),
                halfElf.getString(halfElf.getColumnIndex(Contact.KEY_ADRSTWO)),
                halfElf.getString(halfElf.getColumnIndex(Contact.KEY_CITY)),
                halfElf.getString(halfElf.getColumnIndex(Contact.KEY_STATE)),
                halfElf.getString(halfElf.getColumnIndex(Contact.KEY_ZIP)),
                halfElf.getString(halfElf.getColumnIndex(Contact.KEY_FIRSTCONTACT))
        );  // endline of create new contact object

        // close the db connection
        halfElf.close();

        return contact;
    }

    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();

        // Select everything... all queries, lets go. Orders by last name, because thats what requirements detail
        String selectionQuery = "SELECT  * FROM " + Contact.TABLE_NAME + " ORDER BY " +
                Contact.KEY_LASTNAME + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor halfElf = db.rawQuery(selectionQuery, null);     // more half elf shinanigans

        // we are now going to increment through the rows and add them to this arraylist. Yay MVC!
        if (halfElf.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(halfElf.getInt(halfElf.getColumnIndex(Contact.KEY_ROW_ID)));
                contact.setFirstName(halfElf.getString(halfElf.getColumnIndex(Contact.KEY_FIRSTNAME)));
                contact.setLastName(halfElf.getString(halfElf.getColumnIndex(Contact.KEY_LASTNAME)));
                contact.setMiddleInitial(halfElf.getString(halfElf.getColumnIndex(Contact.KEY_MIDINITIAL)));
                contact.setPhone(halfElf.getString(halfElf.getColumnIndex(Contact.KEY_PHONE)));
                contact.setbDate(halfElf.getString(halfElf.getColumnIndex(Contact.KEY_BDATE)));
                contact.setAddress1(halfElf.getString(halfElf.getColumnIndex(Contact.KEY_ADRSONE)));
                contact.setAddress2(halfElf.getString(halfElf.getColumnIndex(Contact.KEY_ADRSTWO)));
                contact.setCity(halfElf.getString(halfElf.getColumnIndex(Contact.KEY_CITY)));
                contact.setState(halfElf.getString(halfElf.getColumnIndex(Contact.KEY_STATE)));
                contact.setZip(halfElf.getString(halfElf.getColumnIndex(Contact.KEY_ZIP)));
                contact.setFirstContact(halfElf.getString(halfElf.getColumnIndex(Contact.KEY_FIRSTCONTACT)));

                contacts.add(contact);
            } while (halfElf.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return contacts;
    }

    // depreciated code, included anyway for coding practice
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + Contact.TABLE_NAME;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor halfElf = database.rawQuery(countQuery, null);

        int num = halfElf.getCount();
        halfElf.close();

        // return count
        return num;
    }

    // now we update.... this is getting boring. Update via ID
    public int updateContact(Contact contact) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues insertionValues = new ContentValues();

        // put in values
        insertionValues.put(Contact.KEY_FIRSTNAME, contact.getFirstName());
        insertionValues.put(Contact.KEY_LASTNAME, contact.getLastName());
        insertionValues.put(Contact.KEY_MIDINITIAL, contact.getMiddleInitial());
        insertionValues.put(Contact.KEY_PHONE, contact.getMiddleInitial());
        insertionValues.put(Contact.KEY_BDATE, contact.getbDate());
        insertionValues.put(Contact.KEY_ADRSONE, contact.getAddress1());
        insertionValues.put(Contact.KEY_ADRSTWO, contact.getAddress2());
        insertionValues.put(Contact.KEY_CITY, contact.getCity());
        insertionValues.put(Contact.KEY_STATE, contact.getState());
        insertionValues.put(Contact.KEY_ZIP, contact.getZip());

        // now finally update the row
        return database.update(Contact.TABLE_NAME, insertionValues, Contact.KEY_ROW_ID + " = ?",
                new String[]{String.valueOf(contact.getId())});
    }

    // now we deleeeeete.... veyr bored now. Delete via id.

    public void deleteContact(Contact contact) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(Contact.TABLE_NAME, Contact.KEY_ROW_ID + " = ?",
                new String[]{String.valueOf(contact.getId())});
        database.close();
    }



}