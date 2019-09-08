package com.example.chimes;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.Locale;

public class SilentModeActivity extends AppCompatActivity {

    //interface
    private TextToSpeech mTTS;
    private Button mSilentLocation;
    private Button mSilentNorth;
    private Button mSilentChime;

    //location variables
    private FusedLocationProviderClient mFusedLocationClient;

    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private TextView txt_silentlocation;

    private boolean isGPS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_silent_mode);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //Keeps screen on

        //Speak capabilities
        mSilentLocation = findViewById(R.id.button_silentlocation);
        mSilentNorth = findViewById(R.id.button_silentnorth);
        mSilentChime = findViewById(R.id.button_silentchime);

        //Location variables
        this.txt_silentlocation = (TextView) findViewById(R.id.txtLocation);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds

        //TTS for buttons
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.US);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        //Enabled all 3 here
                        mSilentLocation.setEnabled(true);
                        mSilentNorth.setEnabled(true);
                        mSilentChime.setEnabled(true);
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

        //listens for Location button to be pressed
        mSilentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "longitude";
                speak(text);
                if (!isGPS) {
                    //FIX THIS LATER
                    //Toast.makeText(this, "Please turn on GPS", Toast.LENGTH_SHORT).show();
                    return;
                }
                getLocation();
            }
        });
        //listens for North button to be pressed
        mSilentNorth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "North";
                speak(text);
            }
        });
        //listens for Chime Button to be pressed
        mSilentChime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "ding ding";
                speak(text);
            }
        });
        //GPSUtils for Location
        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });

        //Location Callback
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
                        txt_silentlocation.setText(String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude));
                        if (mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            }
        };
    }//end of onCreate

    //called when want app to speak, text is whatever the app is saying
    private void speak(String text) {
        float pitch = 1; //1.0 is normal pitch
        float speed = 1; //normal speech 1.0

        mTTS.setPitch(pitch);
        mTTS.setSpeechRate(speed);
        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override //override this method and destroy mTTS when done w it
    protected void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume(); //always call super first

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTTS.speak("Silent Mode", TextToSpeech.QUEUE_FLUSH, null);
            }
        }, 100);
    }

    //Functions for Location services below
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(SilentModeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(SilentModeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SilentModeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstants.LOCATION_REQUEST);

        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(SilentModeActivity.this, location -> {
                if (location != null) {
                    wayLatitude = location.getLatitude();
                    wayLongitude = location.getLongitude();
                    txt_silentlocation.setText(String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude));
                } else {
                    mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                }
            });
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