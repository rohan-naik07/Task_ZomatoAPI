package com.example.task_app;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.task_app.adapters.RestaurantViewAdapter;
import com.example.task_app.models.Restaurant;
import com.example.task_app.repositories.LocationRepository;
import com.example.task_app.viewmodels.HotelViewModel;
import com.example.task_app.viewmodels.LocationViewModel;
import com.example.task_app.viewmodels.MyViewModelFactory;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    // location settings enabled
    private boolean settingsEnabled = false;
    // permission to access location
    private final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12; // passed back to you on completion to differentiate on request from other

    private static final String[] LOCATION_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private LocationViewModel locationViewModel;
    private HotelViewModel hotelViewModel;
    private EditText editText;
    private ProgressDialog progressDialog;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        editText = (EditText)findViewById(R.id.search);

        checkLocationSettings();
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && settingsEnabled) {
            // if user has already given permission
            locationViewModel.startLocationUpdates();
        } else {
            requestPermissions(LOCATION_PERMS, MY_PERMISSION_ACCESS_FINE_LOCATION); // request to allow location
            // request results are returned in onRequestPermissionsResult function
        }

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        progressDialog = new ProgressDialog(MainActivity.this);


        locationViewModel.getLatitude().observe(this,latitude->{
            // fetch hotels from server
          locationViewModel.getLongitude().observe(this,longitude->{
              hotelViewModel = new ViewModelProvider(this,new MyViewModelFactory(
                      locationViewModel.getLatitude().getValue()
                      ,locationViewModel.getLongitude().getValue())
              ).get(HotelViewModel.class);

              /*editText.addTextChangedListener(new TextWatcher() {
                  @Override
                  public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                  @Override
                  public void onTextChanged(CharSequence s, int start, int before, int count) {
                        hotelViewModel.getSearchList(editText.getText().toString());
                  }
                  @Override
                  public void afterTextChanged(Editable s) {}
              });*/ // search as you type

              findViewById(R.id.imgButton).setOnClickListener(this);
              progressDialog.setMessage("Getting Restaurants Info..."); // show progess dialog till server responds
              progressDialog.show();
              hotelViewModel.getHotelsList().observe(this,hotelsList->{
                  progressDialog.dismiss();
                  if(!hotelsList.isStatus()){
                      AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                      alertDialogBuilder.setTitle("Error")
                              .setMessage(hotelsList.getMessage())
                              .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              dialog.cancel();
                          }
                      }).show();
                  } else {
                      RestaurantViewAdapter adapter = new RestaurantViewAdapter(getApplicationContext(),
                              (List< Restaurant >) hotelsList.getObj());
                      recyclerView.setAdapter(adapter);
                  }

              });
          });
        });
        //set adapter
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_FINE_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    locationViewModel.startLocationUpdates();
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

    @Override
    public void onPause() {
        super.onPause();
        locationViewModel.stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        locationViewModel.startLocationUpdates();
    }

    @Override
    public void onStop() {
        super.onStop();
        locationViewModel.stopLocationUpdates();
    }


    //check if location settings are enabled on device
    private void checkLocationSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        SettingsClient client = LocationServices.getSettingsClient(getApplicationContext());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                settingsEnabled = true;
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    settingsEnabled = false;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        progressDialog.setMessage("Getting Search Info..."); // show progess dialog till server responds
        progressDialog.show();
        hotelViewModel.getSearchList(editText.getText().toString());
        progressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        editText.setText("");
        hotelViewModel.getHotelsList();
    }
}