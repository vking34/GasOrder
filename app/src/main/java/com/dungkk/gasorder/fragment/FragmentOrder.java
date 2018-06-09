package com.dungkk.gasorder.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.*;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.*;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dungkk.gasorder.MainActivity;
import com.dungkk.gasorder.passingObjects.User;
import com.dungkk.gasorder.signActivities.MapActivity;
import com.dungkk.gasorder.R;
import com.dungkk.gasorder.extensions.PlaceAutocompleteAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import android.support.v4.app.FragmentManager;

import com.dungkk.gasorder.passingObjects.location;
import org.json.JSONException;
import org.json.JSONObject;
import com.dungkk.gasorder.passingObjects.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FragmentOrder extends Fragment implements GoogleApiClient.OnConnectionFailedListener, LocationListener, AdapterView.OnItemSelectedListener, View.OnClickListener {

    // returned vars
    private LatLng pos;
    private String address;
    private String ward;
    private location loc;
    private Double lat;
    private Double lng;
    private int gasCode;
    private String details;
    private StringBuffer gasType;
    private String phoneNumber;

    //widgets
    private AutoCompleteTextView ac_address;
    private GoogleApiClient GoogleApiClient;
    private Button btn_clear;
    private Button btn_gps;
    private Button btn_openMap;
    private Button ibtn_order;
    private EditText et_details;
    private EditText et_phoneNumber;
    private Spinner sp_gasCode;

    // static vars
    private final static String TAG = "FragmentOrder";
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(7.520995, 110.581036), new LatLng(23.911473, 100.385724));
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final static String URLgetLastOrder = Server.getAddress() + "/getLastOrder";
    private final static String URL = Server.getAddress() + "/orderForm";
    private RequestQueue requestQueue;

    // vars
    private View view;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    private Boolean locationPermissionsGranted = false;
//    private Bundle lastOrderBundle;

    public LocationManager locationManager;
    public Criteria criteria;
    public String bestProvider;
    private  Toolbar toolbar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order, container, false);

        ac_address = (AutoCompleteTextView) view.findViewById(R.id.ac_address);
        btn_clear = (Button) view.findViewById(R.id.btn_clear);
        btn_openMap = (Button) view.findViewById(R.id.btn_openMap);
        ibtn_order = (Button) view.findViewById(R.id.ibtn_order);
        et_details = (EditText) view.findViewById(R.id.et_details);
        et_phoneNumber = (EditText) view.findViewById(R.id.et_phoneNumber);
        sp_gasCode = (Spinner) view.findViewById(R.id.sp_gasCode);

        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getContext()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getContext()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getContext()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity)getContext()).setTitle("Order Product");
        setHasOptionsMenu(true);

        requestQueue = Volley.newRequestQueue(view.getContext());

        Bundle bundle = this.getArguments();

        if(bundle != null)
        {
            address = bundle.getString("address");
            ward = bundle.getString("ward");
            lat = bundle.getDouble("lat");
            lng = bundle.getDouble("lng");
            Log.e(TAG, "address: "+address + ", lat: "+ lat + ", lng: " + lng);
            ac_address.setText(address + ", " + ward);
        }
        else {
            if(User.getUsername() != null)
            {
                Log.e(TAG, "getting the last order...");
                JSONObject user =  new JSONObject();
                try {
                     user.put("username", User.getUsername());
                     Log.e(TAG, "username: " + user.getString("username"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                RequestQueue rQueue = Volley.newRequestQueue(view.getContext());
                JsonObjectRequest lastOrderRequest = new JsonObjectRequest(Request.Method.POST, URLgetLastOrder, user,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try
                                {
                                    if(response.getBoolean("status"))
                                    {
                                        phoneNumber = response.getString("phoneNumber");
                                        Log.e(TAG, "phone number: " + phoneNumber);
                                        et_phoneNumber.setText(phoneNumber);
                                        address = response.getString("address");
                                        ward = response.getString("ward");
                                        ac_address.setText(address + ", " + ward);

                                        lat = response.getJSONObject("pos").getDouble("lat");
                                        lng = response.getJSONObject("pos").getDouble("lng");

//                                        lastOrderBundle = new Bundle();
//                                        lastOrderBundle.putDouble("lat", lat);
//                                        lastOrderBundle.putDouble("lng", lng);
//                                        lastOrderBundle.putString("address", address);
//                                        lastOrderBundle.putString("ward", ward);

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(TAG, "error");
                            }
                        }
                );

                requestQueue.add(lastOrderRequest);
            }
        }

        btn_openMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapActivity.class);
//                if(lastOrderBundle != null)
//                {
//                    intent.putExtra("lastOrder", lastOrderBundle);
//                }
                startActivity(intent);
            }
        });

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ac_address.setText("");
                showSoftKeyboard();
            }
        });

        ibtn_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject order = new JSONObject();
                JSONObject pos = new JSONObject();
                gasType = new StringBuffer();
                switch (String.valueOf(sp_gasCode.getSelectedItem())){
                    case "12 Kg Petrolimex Cylinder (325.000 VND)":
                        gasCode = 100000;
                        gasType.append("12 Kg Petrolimex Cylinder (325.000 VND)");
                        break;
                    case "2 Kg Petrolimex Cylinder (70.000VND)":
                        gasCode = 100100;
                        gasType.append("2 Kg Petrolimex Cylinder (70.000VND)");
                        break;
                    case "48 Kg Petrolimex Cylinder (1.150.000 VND)":
                        gasCode = 100200;
                        gasType.append("48 Kg Petrolimex Cylinder (1.150.000 VND)");
                        break;
                }

                try {

                    order.put("gasCode", gasCode);
                    details = et_details.getText().toString();
                    if(details.trim().length() == 0)
                    {
                        order.put("address",address);
                    } else {
                        order.put("address",  details + ", " + address);
                    }
                    order.put("ward", ward);
                    phoneNumber = et_phoneNumber.getText().toString();
                    order.put("phoneNumber", phoneNumber);
                    pos.put("lat", lat);
                    pos.put("lng", lng);
                    order.put("pos", pos);

                    if(User.getUsername() != null)
                    {
                        order.put("username", User.getUsername());
                    }

                    Log.e(TAG, "order: " + order.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, order,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                try {

                                    // create a alterDialog Builder
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                                    StringBuffer mess = new StringBuffer();

                                    if(response.getBoolean("status")){
                                        Toast.makeText(getContext(), "Success", Toast.LENGTH_LONG).show();

                                        // set title
                                        alertDialogBuilder.setTitle("Successful Order");

                                        // set content message in the dialog
                                        mess.append("You just ordered ").append(gasType.toString()).append(" at\n").append(details).append(", ").append(address).append(", ").append("\nWe will call you back at ").append(phoneNumber);
                                        alertDialogBuilder.setMessage(mess).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                replaceFragment(new FragmentMain());
                                            }
                                        });

                                    }
                                    else {

                                        alertDialogBuilder.setTitle("Error");
                                        mess.append("Invalid input / Opps, Sorry, Your location is out of our service scope.");
                                        alertDialogBuilder.setMessage(mess).setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });

//                                        Toast.makeText(getContext(), "Invalid input/Sorry Your location is not in our service scope.", Toast.LENGTH_LONG).show();
                                    }

                                    // create alert dialog from alerDialogBuilder
                                    AlertDialog alertDialog = alertDialogBuilder.create();

                                    // show dialog
                                    alertDialog.show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getContext(), "Error", Toast.LENGTH_LONG).show();
                            }
                        }
                );

                requestQueue.add(jsonObjectRequest);

            }
        });

        // Place Google API
        GoogleApiClient = new GoogleApiClient
                .Builder(this.getContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this.getActivity(), this)
                .build();

        ac_address.setOnItemClickListener(AutocompleteClickListener);

        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(this.getContext(), GoogleApiClient, LAT_LNG_BOUNDS, null);

        ac_address.setAdapter(placeAutocompleteAdapter);

        ac_address.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                String searchString = ac_address.getText().toString();
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    geoLocate(searchString);
                }
                return false;
            }
        });

        btn_gps = (Button) view.findViewById(R.id.btn_gps);

        btn_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationPermission();
                getDeviceLocation();
            }
        });

        return view;
    }

    // get address using searchString
    private void geoLocate(String searchString){
        Log.d(TAG, "geoLocate: geolocating by String");
        Geocoder geocoder = new Geocoder(getContext());
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
        }

        if(list.size() > 0){
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: found a location: " + address.getFeatureName() + ", " + address.getThoroughfare() + ", " + address.getLocality());
        }
    }

    private void geoLocate(LatLng latLng){
        Log.d(TAG, "geoLocate: geoLocating by LatLng");
        Geocoder geocoder = new Geocoder(getContext());

        List<Address> list = new ArrayList<>();

        try{
            list = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
        }
        if(list.size() > 0) {
            Address location = list.get(0);
            Log.d(TAG, "geoLocate: found a location: " + location.toString());

            address = location.getFeatureName() + " " + location.getThoroughfare();
            ward = location.getLocality();
        }
    }

    // Google places API autocomplete suggestions

    private AdapterView.OnItemClickListener AutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hideSoftKeyboard();

            final AutocompletePrediction item = placeAutocompleteAdapter.getItem(position);
            final String placeID = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(GoogleApiClient, placeID);

            placeResult.setResultCallback(UpdatePlaceDetailsCallback);

        }
    };

    private ResultCallback<PlaceBuffer> UpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                Log.d(TAG, "onResult: place query did not complete");
                places.release();
                return;
            }
            final Place place = places.get(0);

            Log.d(TAG, "onResult: place details: " + place.getName());

            address = place.getName().toString();
            pos = place.getLatLng();
            lat = pos.latitude;
            lng = pos.longitude;

            Geocoder geocoder = new Geocoder(getContext());

            List<Address> list = new ArrayList<>();

            try{
                list = geocoder.getFromLocation(pos.latitude, pos.longitude, 1);

            }catch (IOException e){
                Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
            }
            if(list.size() > 0) {
                Address location = list.get(0);
                Log.d(TAG, "geoLocate: found a location: " + address.toString());

                ward = location.getLocality();
                Log.d(TAG, "ward: " + ward);
            }

            places.release();
        }
    };

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try {
            if (locationPermissionsGranted) {

                locationManager = (LocationManager) this.getActivity().getSystemService(getContext().LOCATION_SERVICE);
                criteria = new Criteria();
                bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

                Location location = locationManager.getLastKnownLocation(bestProvider);
                if (location != null) {
                    Log.e(TAG, "GPS is on");
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                    pos = new LatLng(lat, lng);
                    geoLocate(pos);

                    ac_address.setText(address + ", " + ward);
                } else {
                    locationManager.requestLocationUpdates(bestProvider, 100, 0, this);
                }
            }
            else {
                getLocationPermission();
        }

//                final Task location = fusedLocationProviderClient.getLastLocation();
//                location.addOnCompleteListener(new OnCompleteListener() {
//                    @Override
//                    public void onComplete(@NonNull Task task) {
//                        if(task.isSuccessful()){
//                            Log.d(TAG, "onComplete: found location!");
//                            Location currentLocation = (Location) task.getResult();
//                            double lat = currentLocation.getLatitude();
//                            double lng = currentLocation.getLongitude();
//                            pos = new LatLng(lat, lng);
//                            geoLocate(pos);
//
//                            ac_address.setText(address + ", " + ward);
//                        }else{
//                            Log.d(TAG, "onComplete: current location is null");
//                            Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }

        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getActivity().getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getActivity().getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationPermissionsGranted = true;
            }else{
                ActivityCompat.requestPermissions(this.getActivity(),
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this.getActivity(),
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        locationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            locationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    locationPermissionsGranted = true;
                }
            }
        }
    }


    private void hideSoftKeyboard(){
        View v = this.getActivity().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void showSoftKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    @Override
    public void onPause() {

        super.onPause();
//        locationManager.removeUpdates(this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void replaceFragment(Fragment fragment){
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.content_main, fragment).commit();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getContext(), "You choose: " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        parent.getFirstVisiblePosition();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (GoogleApiClient != null)
            GoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (GoogleApiClient != null && GoogleApiClient.isConnected()) {
            GoogleApiClient.stopAutoManage(getActivity());
            GoogleApiClient.disconnect();
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




}
