package com.example.task_app.adapters;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.task_app.R;
import com.example.task_app.WebViewActivity;
import com.example.task_app.models.Restaurant;
import com.squareup.picasso.Picasso;

public class RestaurantActivity extends AppCompatActivity {
    TextView name,address,ratings,link,delivry,cuisine,cost,timings;
    ImageView imageView;
    private Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        name = findViewById(R.id.rest_name);
        address = findViewById(R.id.address);
        ratings = findViewById(R.id.ratings);
        cuisine = findViewById(R.id.cuisines);
        delivry = findViewById(R.id.delivery);
        timings = findViewById(R.id.timings);
        cost = findViewById(R.id.cost);
        imageView = findViewById(R.id.image);

        try{
            Intent intent = getIntent();
            restaurant = (Restaurant)intent.getSerializableExtra("info");
            ActionBar actionBar = getSupportActionBar();
            if(actionBar!=null){
                actionBar.setTitle(restaurant.getName());
            }
            name.setText(restaurant.getName());
            address.setText(restaurant.getAddress());
            Picasso.with(RestaurantActivity.this).load(restaurant.getPhotoUrl()).into(imageView);
            ratings.setText(restaurant.getAvgRating().toString());
            cuisine.setText(restaurant.getCuisines());
            cost.setText("Average cost for two is Rs" + restaurant.getCostforTwo().toString());
            if(restaurant.isHasOnlinedelivery()){
            delivry.setText("Delivery Available");
            }else{
            delivry.setText("Delivery Unavailable");
            }
            if(!restaurant.getTimings().equals(""))
                timings.setText(restaurant.getTimings());
        }catch(Exception e){

        }

        findViewById(R.id.visit).setOnClickListener(v -> {
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("link",restaurant.getUrl());
            startActivity(intent);
        });
        findViewById(R.id.menu).setOnClickListener(v -> {
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("link",restaurant.getMenuUrl());
            startActivity(intent);
        });
        findViewById(R.id.call).setOnClickListener(v->{
            if(restaurant.getPhoneNumbers()==null){
                Toast.makeText(RestaurantActivity.this,"Number unavailable",Toast.LENGTH_SHORT).show();
                return;
            }
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + restaurant.getPhoneNumbers().split(",")[0]));//change the number
            startActivity(callIntent);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}