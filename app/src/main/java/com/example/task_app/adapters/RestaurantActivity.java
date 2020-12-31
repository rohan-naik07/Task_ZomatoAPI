package com.example.task_app.adapters;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.task_app.R;
import com.example.task_app.models.Restaurant;
import com.squareup.picasso.Picasso;

public class RestaurantActivity extends AppCompatActivity {
    TextView name,address,ratings,link,delivry,cuisine,cost;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        name = findViewById(R.id.rest_name);
        address = findViewById(R.id.address);
        ratings = findViewById(R.id.ratings);
        cuisine = findViewById(R.id.cuisines);
        link = findViewById(R.id.link);
        delivry = findViewById(R.id.delivery);
        cost = findViewById(R.id.cost);
        imageView = findViewById(R.id.image);

        try{
            Intent intent = getIntent();
            Restaurant restaurant = (Restaurant)intent.getSerializableExtra("info");
            name.setText(restaurant.getName());
            address.setText(restaurant.getAddress());
            Picasso.with(RestaurantActivity.this).load(restaurant.getPhotoUrl()).into(imageView);
            ratings.setText(restaurant.getAvgRating().toString());
            cuisine.setText(restaurant.getCuisines());
            link.setText(restaurant.getUrl());
            cost.setText("Average cost for two is Rs" + restaurant.getCostforTwo().toString());
            if(restaurant.isHasOnlinedelivery()){
            delivry.setText("Delivery Available");
            }else{
            delivry.setText("Delivery Unavailable");
            }
        }catch(Exception e){

        }

    }
}