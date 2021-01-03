package com.example.task_app.repositories;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


public class LocationRepository {
    private FusedLocationProviderClient fusedLocationClient;
    private MutableLiveData<Double> latitude;
    private MutableLiveData<Double> longitude;
    private Context context;

    public LocationRepository(Context context) {
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.latitude = new MutableLiveData<Double>();
        this.longitude = new MutableLiveData<Double>();
        this.context = context;
    }

    public MutableLiveData<Double> getLatitude() {
        return latitude;
    }

    public MutableLiveData<Double> getLongitude() {
        return longitude;
    }


    // callback to update location after specified interval
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location lastLocation = locationResult.getLastLocation();
            Toast.makeText(context,"Location",Toast.LENGTH_LONG);
            latitude.setValue(lastLocation.getLatitude());
            longitude.setValue(lastLocation.getLongitude());
        }
    };


    protected LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(600000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }


    @SuppressLint("MissingPermission")
    public void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(createLocationRequest(),
                this.locationCallback,
                Looper.getMainLooper());
    }

    public void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(this.locationCallback);
    }

}