package com.example.task_app.repositories;

import com.example.task_app.endpoints.GetDataService;
import com.example.task_app.models.ObjectModel;
import com.example.task_app.models.Restaurant;
import com.example.task_app.network_instance.ClientInstance;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import kotlin.io.TextStreamsKt;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HotelInfoRepository {
    final MutableLiveData<ObjectModel> hotelsList;
    final GetDataService getDataService;

    public HotelInfoRepository(){
        hotelsList = new MutableLiveData<ObjectModel>();
        getDataService = ClientInstance.getRetrofitInstance().create(GetDataService.class);
    }

    public MutableLiveData<ObjectModel> getHotelsList(Double latitude, Double longitude, String locality){

        getDataService.getLocation(locality,latitude.toString(),longitude.toString(),"1").enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()){
                    JsonObject jsonbody = response.body().get("location_suggestions").getAsJsonArray().get(0).getAsJsonObject();
                    String entity_id = jsonbody.get( "entity_id").getAsString();
                    String entity_type = jsonbody.get( "entity_type").getAsString();

                    getDataService.getNearbyRestaurants(entity_id,entity_type).enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if (response.isSuccessful()) {
                                JsonObject jsonbody = response.body();
                                List<Restaurant> hotelslist = new ArrayList<>();
                                JsonArray jsonArray = jsonbody.get("best_rated_restaurant").getAsJsonArray();
                                for(int i=0;i<jsonArray.size();i++) {
                                    JsonObject jsonobject = jsonArray.get(i).getAsJsonObject();
                                    JsonObject object = jsonobject.get("restaurant").getAsJsonObject();
                                    hotelslist.add(new Restaurant(
                                            object.get("id").getAsString(),
                                            object.get("name").getAsString(),
                                            object.get("url").getAsString(),
                                            object.get("featured_image").getAsString(),
                                            object.get("location").getAsJsonObject().get("address").getAsString(),
                                            object.get("cuisines").getAsString(),
                                            object.get("timings").getAsString(),
                                            object.get("phone_numbers").getAsString(),
                                            object.get("menu_url").getAsString(),
                                            object.get("location").getAsJsonObject().get("latitude").getAsString(),
                                            object.get("location").getAsJsonObject().get("longitude").getAsString(),
                                            object.get("average_cost_for_two").getAsInt(),
                                            object.get("user_rating").getAsJsonObject().get("aggregate_rating").getAsFloat(),
                                            object.get("has_table_booking").getAsBoolean()
                                    ));
                                }
                                hotelsList.postValue(new ObjectModel(true,hotelslist, response.message()));
                            } else {
                                try {
                                    if (response.errorBody() != null) {
                                        JSONObject errObj = new JSONObject(TextStreamsKt.readText(response.errorBody().charStream()));
                                        hotelsList.postValue(new ObjectModel(false, response.body(), errObj.optString("message")));
                                    } else{
                                        hotelsList.postValue(new ObjectModel(false, response.body(), response.message()));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    hotelsList.postValue(new ObjectModel(false, response.body(), response.message()));
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            hotelsList.postValue(new ObjectModel(false, null, t.getMessage()));
                        }
                    });

                } else {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject errObj = new JSONObject(TextStreamsKt.readText(response.errorBody().charStream()));
                            hotelsList.postValue(new ObjectModel(false, response.body(), errObj.optString("message")));
                        } else{
                            hotelsList.postValue(new ObjectModel(false, response.body(), response.message()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        hotelsList.postValue(new ObjectModel(false, response.body(), response.message()));
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hotelsList.postValue(new ObjectModel(false, null, t.getMessage()));
            }
        });


        return hotelsList;
    }

    public MutableLiveData<ObjectModel> getSearchList(Double latitude, Double longitude, String query){
        String entityType = "zone";
        String count = "5";
        String sort = "rating";
        String order = "asc";
        getDataService.getSearchResults(latitude.toString(),
                longitude.toString(),entityType,query,count,sort,order).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    JsonObject jsonbody = response.body();
                    List<Restaurant> hotelslist = new ArrayList<>();
                    JsonArray jsonArray = jsonbody.get("restaurants").getAsJsonArray();
                    for(int i=0;i<jsonArray.size();i++) {
                        JsonObject jsonobject = jsonArray.get(i).getAsJsonObject();
                        JsonObject object = jsonobject.get("restaurant").getAsJsonObject();
                        hotelslist.add(new Restaurant(
                                object.get("id").getAsString(),
                                object.get("name").getAsString(),
                                object.get("url").getAsString(),
                                object.get("featured_image").getAsString(),
                                object.get("location").getAsJsonObject().get("address").getAsString(),
                                object.get("cuisines").getAsString(),
                                object.get("timings").getAsString(),
                                object.get("phone_numbers").getAsString(),
                                object.get("menu_url").getAsString(),
                                object.get("location").getAsJsonObject().get("latitude").getAsString(),
                                object.get("location").getAsJsonObject().get("longitude").getAsString(),
                                object.get("average_cost_for_two").getAsInt(),
                                object.get("user_rating").getAsJsonObject().get("aggregate_rating").getAsFloat(),
                                object.get("has_table_booking").getAsBoolean()
                        ));
                    }
                    hotelsList.postValue(new ObjectModel(true,hotelslist, response.message()));
                } else {
                    try {
                        if (response.errorBody() != null) {
                            JSONObject errObj = new JSONObject(TextStreamsKt.readText(response.errorBody().charStream()));
                            hotelsList.postValue(new ObjectModel(false, response.body(), errObj.optString("message")));
                        } else{
                            hotelsList.postValue(new ObjectModel(false, response.body(), response.message()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        hotelsList.postValue(new ObjectModel(false, response.body(), response.message()));
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                hotelsList.postValue(new ObjectModel(false, null, t.getMessage()));
            }
        });
        return hotelsList;
    }

}
