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
    public HotelViewModel (Double latitude,Double longitude,String locality){
        super();
        hotelInfoRepository = new HotelInfoRepository(latitude,longitude,locality);
    }

    public LiveData<ObjectModel> getHotelsList(){
        return hotelInfoRepository.getHotelsList();
    };

    public LiveData<ObjectModel> getSearchList(String query){
        return hotelInfoRepository.getSearchList(query);
    }

}


