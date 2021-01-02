package com.example.task_app.viewmodels;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MyViewModelFactory implements ViewModelProvider.Factory {
    private Double latitude;
    private Double longitude;
    private String locality;


    public MyViewModelFactory(Double latitude,Double longitude,String locality) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.locality = locality;
    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new HotelViewModel(latitude,longitude,locality);
    }
}
