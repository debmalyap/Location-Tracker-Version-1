package com.qbent.realtimetrackerv1;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.qbent.realtimetrackerv1.GPSTracker.GPSTracker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    GoogleMap googleMa;
    double latitude;
    private GoogleApiClient mGoogleApiClient;
    double longitude;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    String mPermission = android.Manifest.permission.ACCESS_FINE_LOCATION;
    protected LocationManager locationManager;
    private FusedLocationProviderClient mFusedLocationClient;
    private ArrayList<LatLng> points;
    Polyline line;
    Marker mCurrLocationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        points = new ArrayList<LatLng>();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                2);

        locationManager = (LocationManager) MapsActivity.this
                .getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (checkPlayServices()) {

            buildGoogleApiClient();
            createLocationRequest();
            displayLocation();

        }
        initailizeMap();
    }

    public void initailizeMap() {
        if (googleMa == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMa = googleMap;
        //Log.d("aaaaaa", " on map    --->" + latitude + " " + longitude);
        displayLocation();

    }

    public void displayLocation()
    {

        try {
            GPSTracker gps = new GPSTracker(MapsActivity.this);
            if (gps.canGetLocation()) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                Toast.makeText(getApplicationContext(), "Latitude: " + latitude + " Longitude: " + longitude, Toast.LENGTH_LONG).show();
                final LatLng loc = new LatLng(latitude, longitude);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(loc);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                mCurrLocationMarker = googleMa.addMarker(markerOptions);

                //move map camera
                googleMa.moveCamera(CameraUpdateFactory.newLatLng(loc));
                googleMa.animateCamera(CameraUpdateFactory.zoomTo(11));


                googleMa.addMarker(new MarkerOptions().position(loc).title("My Location"));

//                points.add(loc);
//                redrawLine();
                //Marker ham = googleMa.addMarker(new MarkerOptions().position(loc).title("My Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.greenpointer)));
                //googleMa.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

//    private void redrawLine()
//    {
//        googleMa.clear();
//
//        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
//        for (int i = 0; i < points.size(); i++) {
//            LatLng point = points.get(i);
//            options.add(point);
//        }
//        addMarker(); //add Marker in current position
//        line = googleMa.addPolyline(options); //add Polyline
//    }

//    private void addMarker()
//    {
//        MarkerOptions options = new MarkerOptions();
//
//
//        Marker mapMarker = googleMa.addMarker(options);
//        long atTime = mLastLocation.getTime();
//        //mLastUpdateTime = DateFormat.getTimeInstance().format(new Date(atTime));
//
//    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

    }

    @Override
    public void onConnected(Bundle arg0) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int arg0)
    {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;
        //Log.d("aaaaaaaa===>", "" + String.valueOf(location.getLatitude()) + "\n" + String.valueOf(location.getLongitude()));
        Toast.makeText(getApplicationContext(), "Location changed!",
                Toast.LENGTH_SHORT).show();
        displayLocation();
//        mLastLocation = location;
//        // use latitude and longitude given by
//        // location.getLatitude(), location.getLongitude()
//        // for updated location marker
//
//        // displayLocation();
//
//        // to remove old markers
//        googleMa.clear();
//        final LatLng loc = new LatLng(location.getLongitude(), location.getLongitude());
//
//        Marker ham = googleMa.addMarker(new MarkerOptions().position(loc).title("This is Me").icon(BitmapDescriptorFactory.fromResource(R.drawable.greenpointer)));
//        googleMa.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
    }


    private boolean checkPlayServices() {

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this, resultCode,
                        1000).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {

                final Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(MapsActivity.this, 2000);

                        } catch (IntentSender.SendIntentException e) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000); // 10 sec
        mLocationRequest.setFastestInterval(5000); // 5 sec
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(10); // 10 meters
    }

    protected void startLocationUpdates()
    {
        if (android.support.v4.app.ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && android.support.v4.app.ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_maps);
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//    }
//
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//    }
}
