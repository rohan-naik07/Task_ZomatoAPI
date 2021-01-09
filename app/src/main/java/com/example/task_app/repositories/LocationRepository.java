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
import com.google.android.gms.maps.model.LatLng;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


public class LocationRepository {
    private FusedLocationProviderClient fusedLocationClient;
    private MutableLiveData<LatLng> coordinates;
    private Context context;

    public LocationRepository(Context context) {
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.coordinates = new MutableLiveData<LatLng>();
        this.context = context;
    }

    public MutableLiveData<LatLng> getCoordinates(){
        return coordinates;
    }

    // callback to update location after specified interval
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location lastLocation = locationResult.getLastLocation();
            coordinates.setValue(new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude()));
        }
    };

    protected LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
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