package com.example.aaron.maptest2;

/**
 * Created by Zachary Aaron Greene on 4/9/2018.
 *      credits located below.
 *
 * This is the Model object for the database as part of the MVC Architecture.
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
 *      This particular class is adapted from the Notes.java as seen on the above link.
 *
 *      *** Other useful resources:
 * https://developer.android.com/reference/android/database/sqlite/package-summary.html
 * https://developer.android.com/reference/android/database/package-summary.html
 * https://stackoverflow.com/questions/12015731/android-sqlite-example
 * https://stackoverflow.com/questions/43495549/cannot-install-repository-and-sync-project-in-android-studio
 *
 */

public class Contact implements Comparable<Contact>{

    // define table name
    public static final String TABLE_NAME = "myContacts";

    // define table entries
    public static final String KEY_FIRSTNAME = "firstname";
    public static final String KEY_LASTNAME = "lastname";
    public static final String KEY_MIDINITIAL = "middleinitial";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_BDATE = "birthdate";
    public static final String KEY_FIRSTCONTACT = "firstcontact";
    public static final String KEY_ADRSONE = "addressone";
    public static final String KEY_ADRSTWO = "addresstwo";
    public static final String KEY_CITY = "city";
    public static final String KEY_STATE = "state";
    public static final String KEY_ZIP = "zip";
    public static final String KEY_ROW_ID = "_id";

    // management variables
    private int id;
    private String firstName;
    private String lastName;
    private String middleInitial;
    private String phone;
    private String bDate;
    private String firstContact;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String zip;

    //SQL code for table creation
    public static final String DATABASE_CREATE =
            "create table " + TABLE_NAME + "(" + KEY_ROW_ID         // create table
                    + " INTEGER PRIMARY KEY AUTOINCREMENT," +       // create key, and autoincrement
                    KEY_FIRSTNAME + " TEXT NOT NULL," +             // first name, cannot be null on creation
                    KEY_LASTNAME + " TEXT NOT NULL," +             // last name, cannot be null on creation
                    KEY_MIDINITIAL + " TEXT," +              // mid initial, allowed to be null on creation
                    KEY_PHONE + " TEXT NOT NULL," +          // phone number, cannot be null on creation
                    KEY_BDATE + " TEXT," +                  // birth date, allowed to be null on creation
                    KEY_ADRSONE + " TEXT," +        // address one, allowed to be null on creation
                    KEY_ADRSTWO + " TEXT," +        // address two, allowed to be null on creation
                    KEY_CITY + " TEXT," +           // city, allowed to be null on creation
                    KEY_STATE + " TEXT," +          // state, allowed to be null on creation
                    KEY_ZIP + " TEXT," +             // zip code, allowed to be null on creation
                    KEY_FIRSTCONTACT + " DATETIME DEFAULT CURRENT_TIMESTAMP)";           // first contact, allowed to be null on creation

    //empty constructor because LOLZ
    public Contact()
    {}

    // the actual constructor
    // it doesn't care if the elements are null.
    public Contact(int id, String firstName, String lastName, String middleInitial, String phone, String bDate, String firstContact, String address1, String address2, String city, String state, String zip) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleInitial = middleInitial;
        this.phone = phone;
        this.bDate = bDate;
        this.address1 = address1;
        this.address2 = address2;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.firstContact = firstContact;
        // this part was abandoned because I do not possess a phone with API 26, and thus cannot use ANY DATE FUNCTIONALITY APPARENTLY.

        // part 4 addons
    }

    //sorting routine
    @Override
    public int compareTo(Contact other)     // compare elements based on their last name, weights regardless of case.
    {
        return this.getLastName().compareToIgnoreCase(other.getLastName());
    }

    // getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleInitial() {
        return middleInitial;
    }

    public void setMiddleInitial(String middleInitial) {
        this.middleInitial = middleInitial;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getbDate() {
        return bDate;
    }

    public void setbDate(String bDate) {
        this.bDate = bDate;
    }

    public String getFirstContact() {
        return firstContact;
    }

    public void setFirstContact(String firstContact) {
        this.firstContact = firstContact;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
}
