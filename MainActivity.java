package com.example.displaymap;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;


public class MainActivity extends AppCompatActivity {

    private MapView mMapView;
    private LocationDisplay mLocationDisplay;
    private void setupLocationDisplay(){
       mLocationDisplay = mMapView.getLocationDisplay();
       /* ** ADD ** */
       mLocationDisplay.addDataSourceStatusChangedListener(dataSourceStatusChangedEvent -> {
           if (dataSourceStatusChangedEvent.isStarted() || dataSourceStatusChangedEvent.getError() == null) {
               return;
           }

           int requestPermissionsCode = 2;
           String[] requestPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

           if (!(ContextCompat.checkSelfPermission(MainActivity.this, requestPermissions[0]) == PackageManager.PERMISSION_GRANTED
                   && ContextCompat.checkSelfPermission(MainActivity.this, requestPermissions[1]) == PackageManager.PERMISSION_GRANTED)) {
               ActivityCompat.requestPermissions(MainActivity.this, requestPermissions, requestPermissionsCode);
           } else {
               String message = String.format("Error in DataSourceStatusChangedListener: %s",
                       dataSourceStatusChangedEvent.getSource().getLocationDataSource().getError().getMessage());
               Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
           }
       });
        mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.COMPASS_NAVIGATION);
        mLocationDisplay.startAsync();
   }
   // @Override
    public void onLocationChange(Location location){
        Log.e("latitude", location.getLatitude() +  " ");
        Log.e("longitude", location.getLongitude() + " ");
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mLocationDisplay.startAsync();
        } else {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show();
        }
    }
    //overrides default create. creates the app and adds tamu layer to base layer.
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = findViewById(R.id.mapView);
        setupLocationDisplay();
        ArcGISMap map = new ArcGISMap(Basemap.Type.TOPOGRAPHIC, 30.636295, -96.355800, 16);
        //My imported tamu map layer with accessibility entrances
        ArcGISMapImageLayer layer1 = new ArcGISMapImageLayer("http://gis.tamu.edu/arcgis/rest/services/FCOR/ADA_120717/MapServer");
        map.getBasemap().getBaseLayers().add(layer1);

        mMapView.setMap(map);

    }
    @Override
    protected void onPause(){
        mMapView.pause();
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mMapView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.dispose();
    }
}
