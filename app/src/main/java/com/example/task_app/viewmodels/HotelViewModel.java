package com.example.task_app.viewmodels;

import com.example.task_app.models.ObjectModel;
import com.example.task_app.models.Restaurant;
import com.example.task_app.repositories.HotelInfoRepository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class HotelViewModel extends ViewModel {
    private final HotelInfoRepository hotelInfoRepository;
    public HotelViewModel (){
        super();
        hotelInfoRepository = new HotelInfoRepository();
    }

    public LiveData<ObjectModel> getHotelsList(Double latitude, Double longitude, String locality){
        return hotelInfoRepository.getHotelsList(latitude,longitude,locality);
    };

    public LiveData<ObjectModel> getSearchList(Double latitude, Double longitude,String query){
        return hotelInfoRepository.getSearchList(latitude,longitude,query);
    }



}


