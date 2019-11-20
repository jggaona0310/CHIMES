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
       /* y1 = 1.0508*wayLatitude - 128.516151;
        y2 = -1.47199*wayLatitude - 51.264739;
        y3 = 1.37847*wayLatitude - 138.558410;
        y4 = -.938798*wayLatitude - 67.59399;*/
        //New ZACH REGION
//        y1 = 2.15442*wayLatitude - 162.31332;
//        y2 = -.24166*wayLatitude - 88.95483;
//        y3 = 1.16156*wayLatitude - 131.90745;
//        y4 = -1.04382*wayLatitude - 94.37783;
        //NEW NEW NEW ZACH Region (Excel)
        y1 = 0.827137546*wayLongitude + 110.30921;
        y2 = -0.805755396*wayLongitude - 47.00435493;
        y3 = 1.213740458*wayLongitude + 147.55188;
        y4 = -0.867010975*wayLongitude - 52.90756489;

        //ETB Numbers
//        etb1 = 1.11952*wayLatitude - 130.623;
//        etb2 = -1.17594*wayLatitude - 60.3279;
//        etb3 = 1.62281*wayLatitude - 146.032;
//        etb4 = -1.60398*wayLatitude - 47.2225;

        //NEW ETB NUMS EXCEL
        etb1 = 1.00641*wayLongitude + 127.58038;
        etb2 = -0.923240938*wayLongitude -58.32087133;
        etb3 = 0.437828371*wayLongitude +72.80168497;
        etb4 = -0.713572023*wayLongitude -38.1231237;
        //WEB values
//        web1 = .96747*wayLatitude - 125.96418;
////        web2 = -.97244*wayLatitude - 99.56152;
////        web3 = 1.59796*wayLatitude - 145.26840;
////        web4 = -.88838*wayLatitude - 69.13630;

        //NEW WEB VALUES (Excel)
        web1 = 0.605122732*wayLongitude + 88.917921;
        web2 = -1.172366621*wayLongitude - 82.322904;
        web3 = .5053381*wayLongitude + 79.303582;
        web4 = -.31783658*wayLongitude;

        /////Inside these if statements, you can handle each case however is necessary/////
        if((wayLatitude <= etb1) && (wayLatitude <= etb2) && (wayLatitude >= etb3) && (wayLatitude >= etb4)){
                //Inside ETB region
            Transition = "You're Inside ETB Region";
            txtTransition.setText(Transition);
        }
        else if(wayLatitude <= y1 && wayLatitude <= y2 && wayLatitude >= y3 && wayLatitude >= y4){
            //inside Zach region
            Transition = "You're Inside Zachry Region";
            txtTransition.setText(Transition);
        }
        else if(wayLatitude <= web1 && wayLatitude <= web2 && wayLatitude >= web3 && wayLatitude >= web4){
            //inside WEB region
            Transition = "You're Inside WEB Region";
            txtTransition.setText(Transition);
        }
      /*  else if (wayLatitude > 30.622693 && wayLatitude < 30.624885 && wayLongitude < -96.338332 && wayLongitude > -96.346089){      //inside square
                    Transition = "You are inside Ucentre";
                    txtTransition.setText(Transition);
        }*/
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
