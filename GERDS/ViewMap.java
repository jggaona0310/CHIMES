package com.example.chimes;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer;
import com.esri.arcgisruntime.location.LocationDataSource;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;

public class ViewMap extends AppCompatActivity {

    private MapView mMapView;
    private LocationDisplay mLocationDisplay;
    String loc = "location test";
    Double Latitude;
    Double Longitude;

    private void setupLocationDisplay(){
        mLocationDisplay = mMapView.getLocationDisplay();
        /* ** ADD ** */
        mLocationDisplay.addDataSourceStatusChangedListener(dataSourceStatusChangedEvent -> {
            if (dataSourceStatusChangedEvent.isStarted() || dataSourceStatusChangedEvent.getError() == null) {
                return;
            }

            int requestPermissionsCode = 2;
            String[] requestPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

            if (!(ContextCompat.checkSelfPermission(ViewMap.this, requestPermissions[0]) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(ViewMap.this, requestPermissions[1]) == PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(ViewMap.this, requestPermissions, requestPermissionsCode);
            } else {
                String message = String.format("Error in DataSourceStatusChangedListener: %s",
                        dataSourceStatusChangedEvent.getSource().getLocationDataSource().getError().getMessage());
                Toast.makeText(ViewMap.this, message, Toast.LENGTH_LONG).show();
            }
        });
        mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.COMPASS_NAVIGATION);
        mLocationDisplay.startAsync();
        //get location in GIS form
        //LocationDataSource.Location location = mLocationDisplay.getLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mLocationDisplay.startAsync();
        } else {
            Toast.makeText(ViewMap.this, getResources().getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show();
        }
    }
    //overrides default create. creates the app and adds tamu layer to base layer.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_map);

        mMapView = findViewById(R.id.mapView);
        setupLocationDisplay();
        ArcGISMap map = new ArcGISMap(Basemap.Type.TOPOGRAPHIC, 30.636295, -96.355800, 16);
        //My imported tamu map layer with accessibility entrances
        ArcGISMapImageLayer layer1 = new ArcGISMapImageLayer("http://gis.tamu.edu/arcgis/rest/services/FCOR/ADA_120717/MapServer");
        map.getBasemap().getBaseLayers().add(layer1);

        mMapView.setMap(map);
    }//End of onCreate

    //Used to pull data to access in other activities
    //NOTDONE
    protected void getlocation(){
        LocationDataSource.Location location = mLocationDisplay.getLocation();
        Point point = location.getPosition();
        Longitude = point.getX();
        Latitude = point.getY();
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
