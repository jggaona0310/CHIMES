package com.coders.locationChime;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient mFusedLocationClient;

    public double wayLatitude = 0.0, wayLongitude = 0.0;
    ///////ADDED THESE1      /////////////////////////
    public double y1, y2, y3, y4, etb1, etb2, etb3, etb4;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private android.widget.Button btnContinueLocation;
    private android.widget.Button btnactivity_geofence; ///////Additional button added
    private TextView txtContinueLocation;
    private TextView txtTransition;     ///////Text added for display
    private StringBuilder stringBuilder;
    String showLocation, Transition;

    //for show location button
    private TextView txtShowLocation;
    private android.widget.Button btnShowLocation;

    private boolean isContinue = false;
    private boolean isGPS = false;

    //Merge oncreate next
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.txtContinueLocation = (TextView) findViewById(R.id.txtContinueLocation);
        this.btnContinueLocation = (Button) findViewById(R.id.btnContinueLocation);

        //Show Location Stuff
        this.txtShowLocation = (TextView) findViewById(R.id.txtShowLocation);
        this.btnShowLocation = (Button) findViewById(R.id.btnShowLocation);
        this.btnactivity_geofence = (Button) findViewById(R.id.btnactivity_geofence);
        this.txtTransition = (TextView) findViewById(R.id.txtTransition);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 15 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds

        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });


        //Action when Location button is clicked
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();

                        //Add cont. location to textbox
                        //stringBuilder.delete(0,46);
                        stringBuilder.append(wayLatitude);
                        stringBuilder.append(",");
                        stringBuilder.append(wayLongitude);
                        stringBuilder.append("\n\n");

                        txtContinueLocation.setText(stringBuilder.toString());

                        if (!isContinue && mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            }
        };

        btnContinueLocation.setOnClickListener(v -> {
            if (!isGPS) {
                Toast.makeText(this, "Please turn on GPS", Toast.LENGTH_SHORT).show();
                return;
            }
            isContinue = true;
            stringBuilder = new StringBuilder();
            getLocation();
        });

        //Show Location
        btnShowLocation.setOnClickListener(v -> {
            showLocation = wayLatitude + " , " + wayLongitude; //string with updated lat / long
            txtShowLocation.setText(showLocation);
        });
        //////////////New button Listener that calls function that handles regions/////////
        //this txt should display whether or not device is inside ucenter square
        btnactivity_geofence.setOnClickListener(v -> {
                checkLocation();
        });
        //////////////////////////////////////////////////////////////////////
    } //End of onCreate

//    public void opengeofence(){
//        Intent intent = new Intent(this, geofence.class);
//        startActivity(intent);
//    }
    /////////Function on its own so it can be called from anywhere////////////////
    //logic for checking Zach and ETB regions
    public void checkLocation() {
        //Zachary numbers
        y1 = 1.0508*wayLatitude - 128.516151;
        y2 = -1.47199*wayLatitude - 51.264739;
        y3 = 1.37847*wayLatitude - 138.558410;
        y4 = -.938798*wayLatitude - 67.59399;
        //ETB Numbers
        etb1 = 1.11952*wayLatitude - 130.623;
        etb2 = -1.17594*wayLatitude - 60.3279;
        etb3 = 1.62281*wayLatitude - 146.032;
        etb4 = -1.60398*wayLatitude - 47.2225;
        /*
        etb1 = -1.58754*wayLatitude - 47.7256;
        etb2 = 1.38211*wayLatitude - 138.664;
        etb3 = -1.21429*wayLatitude - 61.9097;
        etb4 = 2.19616*wayLatitude - 163.59;*/ //old values that are messed up
        /////Inside these if statements, you can handle each case however is necessary/////
        if((wayLongitude <= etb1) && (wayLongitude <= etb2) && (wayLongitude >= etb3) && (wayLongitude >= etb4)){
                //Inside ETB region
            Transition = "You're Inside ETB Region";
            txtTransition.setText(Transition);
        }
        else if(wayLongitude <= y1 && wayLongitude <= y2 && wayLongitude >= y3 && wayLongitude >= y4){
            //inside Zach region
            Transition = "You're Inside Zachry Region";
            txtTransition.setText(Transition);
        }
        else if (wayLatitude > 30.622693 && wayLatitude < 30.624885 && wayLongitude < -96.338332 && wayLongitude > -96.346089){      //inside square
                    Transition = "You are inside Ucentre";
                    txtTransition.setText(Transition);
        }
        else{
            Transition = "Unknown Region";
            txtTransition.setText(Transition);
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstants.LOCATION_REQUEST);

        } else {
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
            }
        }
    }
}
