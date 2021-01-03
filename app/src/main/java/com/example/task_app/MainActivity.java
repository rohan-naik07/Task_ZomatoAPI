package com.example.task_app;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.task_app.adapters.RestaurantViewAdapter;
import com.example.task_app.models.Restaurant;
import com.example.task_app.viewmodels.HotelViewModel;
import com.example.task_app.viewmodels.LocationViewModel;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.google.gson.reflect.TypeToken.get;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    private boolean settingsEnabled = true; // location settings enabled
    private final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12; // passed back to you on completion to differentiate on request from other
    private static final String[] LOCATION_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private LocationViewModel locationViewModel;
    private HotelViewModel hotelViewModel;
    private EditText editText;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private static MarkerOptions markerOptions;
    private Marker markerYourLocation;
    private List<Marker> markerList;
    private static boolean markerSetFlag = true;
    private static boolean hasMarkerMoved = true;
    private static Marker movableMarker;
    private Address address;
    private String locality;
    private Double latitude;
    private Double longitude;
    private RestaurantViewAdapter adapter;
    private AlertDialog.Builder alertDialogBuilder;
    private CoordinatorLayout coordinatorLayout;
    private InputMethodManager inputMethodManager;

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        editText = findViewById(R.id.search);
        coordinatorLayout = findViewById(R.id.main);
        inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setHasFixedSize(true);
        progressDialog = new ProgressDialog(MainActivity.this);
        alertDialogBuilder = new AlertDialog.Builder(this);
        markerList = new ArrayList<Marker>();

        checkLocationSettings();
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && settingsEnabled) {
            // if user has already given permission
            locationViewModel.startLocationUpdates();
            StartMap();
        } else {
            requestPermissions(LOCATION_PERMS, MY_PERMISSION_ACCESS_FINE_LOCATION); // request to allow location
            // request results are returned in onRequestPermissionsResult function
        }

    }

    public void StartMap(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
                    StartMap();
                } else {
                    alertDialogBuilder.setTitle("Error")
                            .setMessage("You must allow your location access")
                            .setNegativeButton("Ok", (dialog, which) -> dialog.cancel()).show();
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
        markerSetFlag = true;
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
        task.addOnSuccessListener(this, locationSettingsResponse -> {
            // All location settings are satisfied.
           return;
        });

        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                alertDialogBuilder.setTitle("Error")
                        .setMessage("You must enable your location settings")
                        .setNegativeButton("Ok", (dialog, which) -> dialog.cancel()).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        progressDialog.setMessage("Getting Search Info..."); // show progess dialog till server responds
        progressDialog.show();
        inputMethodManager.hideSoftInputFromWindow(coordinatorLayout.getWindowToken(), 0);
        if(editText.getText().toString().equals("")){
            alertDialogBuilder.setTitle("Error")
                    .setMessage("You must provide a query!")
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                }).show();
            progressDialog.dismiss();
            return;
        }
        hotelViewModel.getSearchList(latitude,longitude,editText.getText().toString());
        adapter.notifyDataSetChanged();
        progressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        editText.setText("");
        hotelViewModel.getHotelsList(latitude,longitude,locality);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        locationViewModel.getLatitude().observe(this,latitude->{
            // fetch hotels from server
            locationViewModel.getLongitude().observe(this,longitude->{

                setLatitude(locationViewModel.getLatitude().getValue());
                setLongitude(locationViewModel.getLongitude().getValue());

                Geocoder geocoder = new Geocoder(getApplicationContext());
                try {
                    address = geocoder.getFromLocation(
                            latitude,
                            longitude,
                            1).get(0);
                    locality = address.getSubLocality();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(movableMarker!=null){
                    movableMarker.remove();
                }


                movableMarker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude+1,longitude+1))
                        .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(this,R.drawable.ic_baseline_edit_location_alt_24)))
                        .draggable(true));


                googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker arg0) {
                        // TODO Auto-generated method stub
                        Log.d("System out", "onMarkerDragStart..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
                    }

                    @SuppressWarnings("unchecked")
                    @Override
                    public void onMarkerDragEnd(Marker arg0) {
                        try {
                            Address addressTemp = geocoder.getFromLocation(
                                    arg0.getPosition().latitude,
                                    arg0.getPosition().longitude,
                                     1).get(0);
                            String localitytemp = addressTemp.getSubLocality();
                            hotelViewModel.getHotelsList(arg0.getPosition().latitude,arg0.getPosition().longitude,localitytemp);
                            adapter.notifyDataSetChanged();
                            googleMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onMarkerDrag(Marker arg0) {
                        // TODO Auto-generated method stub
                        Log.i("System out", "onMarkerDrag...");
                    }
                });

                googleMap.setOnMapLongClickListener(latLng -> {
                    movableMarker.setPosition(latLng);
                    Address addressTemp = null;
                    try {
                        addressTemp = geocoder.getFromLocation(
                                latLng.latitude,
                                latLng.longitude,
                                1).get(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String localitytemp = addressTemp.getSubLocality();
                    hotelViewModel.getHotelsList(latLng.latitude,latLng.longitude,localitytemp);
                    adapter.notifyDataSetChanged();
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                });

                hotelViewModel = new ViewModelProvider(this).get(HotelViewModel.class);

                findViewById(R.id.imgButton).setOnClickListener(this);
                progressDialog.setMessage("Getting Restaurants Info..."); // show progess dialog till server responds
                progressDialog.show();

                hotelViewModel.getHotelsList(latitude,longitude,locality).observe(this,hotelsList->{
                    progressDialog.dismiss();
                    if(!hotelsList.isStatus()){
                        alertDialogBuilder.setTitle("Error")
                                .setMessage(hotelsList.getMessage())
                                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }).show();
                    } else {
                        for(Marker marker : markerList){
                            marker.remove();
                        }
                        markerList.clear();
                        List<Restaurant> restaurantList =  (List< Restaurant >) hotelsList.getObj();
                        adapter = new RestaurantViewAdapter(getApplicationContext(),restaurantList);
                        recyclerView.setAdapter(adapter);

                        if(markerYourLocation!=null){
                            markerYourLocation.remove();
                        }

                        LatLng coordinates = new LatLng(latitude,longitude);
                        markerOptions = new MarkerOptions().position(coordinates).title("Current Position")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        markerYourLocation = googleMap.addMarker(markerOptions);
                        markerList.add(markerYourLocation);

                        if(markerSetFlag){
                            CameraPosition camPos = new CameraPosition.Builder()
                                    .target(coordinates)
                                    .zoom(18)
                                    .tilt(70)
                                    .build();
                            CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
                            googleMap.animateCamera(camUpd3);
                            markerSetFlag = false;
                        }

                        for(Restaurant restaurant : restaurantList){
                            LatLng hotelcoordinates = new LatLng(Double.parseDouble(restaurant.getLatitude())
                                    ,Double.parseDouble(restaurant.getLongitude()));
                            markerOptions = new MarkerOptions()
                                    .position(hotelcoordinates).title(restaurant.getName())
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                            Marker markerHotelLocation = googleMap.addMarker(markerOptions);
                            markerList.add(markerHotelLocation);
                        }
                    }
                });
            });
        });
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}