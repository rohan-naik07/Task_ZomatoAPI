package com.example.task_app.viewmodels;

import android.app.Application;

import com.example.task_app.repositories.LocationRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LocationViewModel extends AndroidViewModel {

    private LocationRepository repository;
    private LiveData<Double> latitude;
    private LiveData<Double> longitude;

    public LiveData<Double> getLatitude() {
        return latitude;
    }

    public LiveData<Double> getLongitude() {
        return longitude;
    }

    public LocationViewModel(@NonNull Application application) {
        super(application);
        repository = new LocationRepository(application);
        latitude = repository.getLatitude();
        longitude = repository.getLongitude();
    }

    public void startLocationUpdates(){
        repository.startLocationUpdates();
    }
    public void stopLocationUpdates(){
        repository.stopLocationUpdates();
    }

}
