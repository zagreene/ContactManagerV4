package com.example.aaron.maptest2;

/**
 * Created by Zachary Aaron Greene on 4/1/2018.
 *      credits located below.
 *
 * This is the Controller class for the database as part of the MVC Architecture.
 * It renders the RecyclerView view type with a defined layout and given data; it is what asks the database for information.
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
 *      This particular class is adpated from the NotesAdapter.java as listed in the guide linked above.
 *
 *      *** Other useful resources:
 * https://developer.android.com/reference/android/database/sqlite/package-summary.html
 * https://developer.android.com/reference/android/database/package-summary.html
 * https://stackoverflow.com/questions/12015731/android-sqlite-example
 * https://stackoverflow.com/questions/43495549/cannot-install-repository-and-sync-project-in-android-studio
 * https://stackoverflow.com/questions/37904739/html-fromhtml-deprecated-in-android-n
 *      some code here is adapted from k2col's answer
 *
 */

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.example.aaron.maptest2.Contact;
import com.example.aaron.maptest2.DataBaseManager;


public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder>
{
    private Context context;                // create global context
    private List<Contact> contactList;      // create global contact list container for manipulation and display

    // tie to Recyclerview, textview containers for contact fields in list
    public class ContactViewHolder extends RecyclerView.ViewHolder
    {
        public TextView contactLast;
        public TextView contactFirst;
        public TextView dot;
        public TextView timestamp;

        public ContactViewHolder(View view)
        {
            super(view);
            contactLast = view.findViewById(R.id.contactLastName);
            contactFirst = view.findViewById(R.id.contactFirstName);
            dot = view.findViewById(R.id.dot);
            timestamp = view.findViewById(R.id.timestamp);
        }
    }

    //constructor
    public ContactAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    //overridden class for view
    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list_row, parent, false);

        return new ContactViewHolder(itemView);
    }

    //overridden class for retrieving information from the list and displaying it to screen holder
    @Override
    @SuppressWarnings("deprecation")
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);    // retrieve contact

        //retrieve contact first and last name for list display
        holder.contactLast.setText(contact.getLastName());
        holder.contactFirst.setText(contact.getFirstName());

        // Displaying dot from HTML character code
        // requires compatibility check otherwise it messes up
        // guide code retrieved from k2col at https://stackoverflow.com/questions/37904739/html-fromhtml-deprecated-in-android-n
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            holder.dot.setText(Html.fromHtml("&#8226;", Html.FROM_HTML_MODE_LEGACY));   // for API 26 or greater
        else
            holder.dot.setText(Html.fromHtml("&#8226;"));   // for API 25 or lower

        // Formatting and displaying timestamp from when contact was first created.
        holder.timestamp.setText(formatDate(contact.getFirstContact()));
    }

    // simple way of measuring the number of contacts.
    @Override
    public int getItemCount() {
        return contactList.size();
    }

    // formats date for disply; takes a format of 2018-04-01 00:00:00 which becomes Apr 21 2018
    private String formatDate(String dateStr)
    {
        //requires try block, even though it doesn't handle the error
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d yyyy");
            return fmtOut.format(date);
        } catch (ParseException e)
        {
            //does nothing, see below
        }

        return "";      // returns null if there is an error in parsing the date.
    }
}