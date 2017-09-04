package com.example.android.locationtracker;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Date;


public class MainActivity extends AppCompatActivity {


    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private FusedLocationProviderClient mFusedLOcationClient;
    TextView longitude, latitude, altitude, timeText;
    private Double longitudeValue, latitudeValue, altitudeValue;
    Long timeValue;
    Location mCurrentLocation;
    LocationRequest mLocationRequest = new LocationRequest();
    private LocationCallback mLocationCallback;

    private boolean mRequestingLocationUpdates= true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        longitude = (TextView) findViewById(R.id.longitude);
        latitude = (TextView) findViewById(R.id.latitude);
        altitude = (TextView) findViewById(R.id.altitude);
        timeText = (TextView) findViewById(R.id.timeText);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},99);
            }
        }

        mFusedLOcationClient = LocationServices.getFusedLocationProviderClient(this);

       /* mFusedLOcationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        mCurrentLocation = location;
                        if (location != null) {
                            longitudeValue = location.getLongitude();
                            longitude.setText(longitudeValue.toString());
                            Log.v("Longitude",longitudeValue.toString());

                            latitudeValue = location.getLatitude();
                            latitude.setText(latitudeValue.toString());
                            Log.v("Latitude",latitudeValue.toString());

                            altitudeValue = location.getAltitude();
                            altitude.setText(altitudeValue.toString());
                            Log.v("Altitude",altitudeValue.toString());

                        }
                        else{
                            latitude.setText("Location Null");
                        }
                    }
                });*/
        createLocationRequest();
        mLocationCallback= new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                for(Location location : locationResult.getLocations()){

                    longitudeValue = location.getLongitude();
                    longitude.setText(longitudeValue.toString());
                    Log.v("Longitude",longitudeValue.toString());

                    latitudeValue = location.getLatitude();
                    latitude.setText(latitudeValue.toString());
                    Log.v("Latitude",latitudeValue.toString());

                    altitudeValue = location.getAltitude();
                    altitude.setText(altitudeValue.toString());
                    Log.v("Altitude",altitudeValue.toString());

                    timeValue = location.getTime();
                    Date date = new Date(timeValue);
                    timeText.setText(date.toString());

                }
            }
        };
    }




    protected void createLocationRequest(){

        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

            }
        });


        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode){
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        try{
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        }
                        catch (IntentSender.SendIntentException sendEx) {

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:LocationUpdates:
                        break;
                }
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(mRequestingLocationUpdates){
            startLocationUpdates();
        }
    }

    private void startLocationUpdates(){
        mFusedLOcationClient.requestLocationUpdates(mLocationRequest,mLocationCallback,null);
    }


    protected void onPause(){
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates(){
        mFusedLOcationClient.removeLocationUpdates(mLocationCallback);
    }

}

