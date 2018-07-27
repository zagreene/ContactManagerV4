package com.example.aaron.maptest2;
/**
 * Created by Zachary Aaron Greene on 4/12/2018.
 *
 * This is an intermediary loading activity between the main activity and the maps activity. Geocoding pf the passed address is done here.
 *
 * */


import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddressLoading extends AppCompatActivity {

    //UI elements
    private TextView loadingFailure;
    private TextView loadingSuccess;
    private ProgressBar progressBar;

    //strings for importing info
    private String firstName;
    private String lastName;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String zip;
    private String addressReady;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_loading);

        // get intent data
        Bundle recievedData = new Bundle();
        recievedData = getIntent().getExtras();

        //load intent data into strings
        firstName = recievedData.getString("firstName");
        lastName = recievedData.getString("lastName");
        address1 = recievedData.getString("address1");
        address2 = recievedData.getString("address2");
        city = recievedData.getString("city");
        state = recievedData.getString("state");
        zip = recievedData.getString("zip");

        addressReady = address1 + " " + address2 + ", " + city + ", " + state + " " + zip;


        //set up UI elements
        loadingFailure = (TextView) findViewById(R.id.loadFailure);
        loadingSuccess = (TextView) findViewById(R.id.loadSuccess);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        loadingSuccess.setVisibility(View.INVISIBLE);
        loadingFailure.setVisibility(View.INVISIBLE);

        // start searching via geocoding
        new getLocationFromAddress().execute();
    }


    // the following inner class uses an async task to load an address from geocoder.
    class getLocationFromAddress extends AsyncTask<Void, Void, Address>
    {
        @Override
        protected void onPreExecute()
        {
            //do we do somthing here?
        }

        // do in background
        @Override
        protected Address doInBackground(Void ... none)     // we pass no parameters (?)
        {
            Geocoder geocoder = new Geocoder(AddressLoading.this, Locale.ENGLISH);    // create geocoder object, keyed to the mapsActivity activity
            List<Address> addresses = null;

            try
            {
                // retrieve address class from provided data
                addresses = geocoder.getFromLocationName(addressReady, 1);
            } catch (IOException e)
            {
                // error handling
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Address not found.", Toast.LENGTH_SHORT).show();
            }

            if (addresses.size() > 0)       // check to make sure we got an address
            {
                return addresses.get(0);  // get the first address and return it
            }
            return null;    // if no address is gotten
        }

        //return!
        @Override
        protected void onPostExecute(Address address)
        {
            if(address != null)
            {
                //adjust UI elements
                loadingSuccess.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);

                //prep information for transfer
                Intent mapIntent = new Intent(AddressLoading.this, MapsActivity.class);

                Bundle bundle = new Bundle();

                bundle.putString("firstName", firstName);
                bundle.putString("lastName", lastName);
                bundle.putDouble("latitude", address.getLatitude());
                bundle.putDouble("longitude", address.getLongitude());

                mapIntent.putExtras(bundle);

                // transition to map activity
                startActivity(mapIntent);
            }
            else
            {
                // display no address found
                loadingFailure.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

}
