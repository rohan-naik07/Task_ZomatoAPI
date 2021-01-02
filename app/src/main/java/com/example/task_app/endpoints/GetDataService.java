package com.example.task_app.endpoints;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;


public interface GetDataService {
    @GET("geocode")
    @Headers("Content-Type: application/json")
    Call<JsonObject> getRestaurants(@Query("lat") String latitude, @Query("lon") String longitude );

    @GET("locations")
    @Headers("Content-Type: application/json")
    Call<JsonObject> getLocation(
            @Query("query") String query,
            @Query("lat") String latitude,
            @Query("lon") String longitude,
            @Query("count") String count
    );

    @GET("location_details")
    @Headers("Content-Type: application/json")
    Call<JsonObject> getNearbyRestaurants(
            @Query("entity_id") String entity_id,
            @Query("entity_type") String entity_type
    );

    @GET("search")
    @Headers("Content-Type: application/json")
    Call<JsonObject> getSearchResults(
        @Query("lat") String latitude,
        @Query("lon") String longitude,
        @Query("entity_type") String entityType,
        @Query("q") String query,
        @Query("count") String count,
        @Query("sort") String sort,
        @Query("order") String order
    );
}
