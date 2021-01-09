package com.example.task_app;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.task_app.adapters.RestaurantViewAdapter;
import com.example.task_app.async.DirectionsJSONParser;
import com.example.task_app.async.TaskRunner;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

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
    private static Marker movableMarker;
    private Address address;
    private String locality;
    private Double latitude;
    private Double longitude;
    private RestaurantViewAdapter adapter;
    private AlertDialog.Builder alertDialogBuilder;
    private CoordinatorLayout coordinatorLayout;
    private InputMethodManager inputMethodManager;
    private TaskRunner taskRunner;

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
        taskRunner = new TaskRunner();

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
                    displayError("You must allow your location access");
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
        task.addOnSuccessListener(this, locationSettingsResponse -> {
            // All location settings are satisfied.
           return;
        });

        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                displayError("You must enable your location settings");
            }
        });
    }

    @Override
    public void onClick(View v) {
        progressDialog.setMessage("Getting Search Info..."); // show progess dialog till server responds
        progressDialog.show();
        inputMethodManager.hideSoftInputFromWindow(coordinatorLayout.getWindowToken(), 0);
        if(editText.getText().toString().equals("")){
            displayError("You must provide a query!");
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
        locationViewModel.getCoordinates().observe(this,coordinates->{

            setLatitude(coordinates.latitude);
            setLongitude(coordinates.longitude);

            Geocoder geocoder = new Geocoder(getApplicationContext());

            try {
                address = geocoder.getFromLocation(
                        latitude,
                        longitude,
                        1).get(0);
                locality = address.getSubLocality();
            } catch (IOException e) {
                e.printStackTrace();
                displayError(e.getMessage());
            }

            markerOptions = new MarkerOptions().position(coordinates).title("Current Position")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            markerYourLocation = googleMap.addMarker(markerOptions);
            CameraPosition camPos = new CameraPosition.Builder()
                    .target(coordinates)
                    .zoom(18)
                    .tilt(70)
                    .build();
            CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
            googleMap.animateCamera(camUpd3);

            if(movableMarker!=null){
                movableMarker.remove();
            }

            movableMarker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude+1,longitude+1))
                    .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(this,R.drawable.ic_baseline_edit_location_alt_24)))
                    .draggable(true));

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
                try{
                    progressDialog.dismiss();
                    if(!hotelsList.isStatus()){
                        displayError(hotelsList.getMessage());
                    } else {

                        for(Marker marker : markerList){
                            marker.remove();
                        }

                        markerList.clear();
                        List<Restaurant> restaurantList =  (List< Restaurant >) hotelsList.getObj();
                        adapter = new RestaurantViewAdapter(getApplicationContext(),restaurantList);
                        recyclerView.setAdapter(adapter);

                        for(Restaurant restaurant : restaurantList){
                            LatLng hotelcoordinates = new LatLng(Double.parseDouble(restaurant.getLatitude())
                                    ,Double.parseDouble(restaurant.getLongitude()));
                            markerOptions = new MarkerOptions()
                                    .position(hotelcoordinates).title(restaurant.getName())
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                            Marker markerHotelLocation = googleMap.addMarker(markerOptions);
                            markerList.add(markerHotelLocation);
                        }

                        googleMap.setOnMarkerClickListener(marker -> {
                            LatLng origin = coordinates;
                            Toast.makeText(this,marker.getTitle(),Toast.LENGTH_SHORT).show();
                            LatLng destination = marker.getPosition();
                            String url = getDirectionsUrl(origin, destination);
                            try {
                                taskRunner.executeAsync(new DownloadUrlTask(url),(data)->{
                                    Log.d("data",data);
                                    taskRunner.executeAsync(new PathParserTask(data),(result)->{
                                        ArrayList points = null;
                                        PolylineOptions lineOptions = null;
                                        MarkerOptions markerOptions = new MarkerOptions();
                                        for (int i = 0; i < result.size(); i++) {
                                            points = new ArrayList();
                                            lineOptions = new PolylineOptions();

                                            List<HashMap<String,String>> path = result.get(i);

                                            for (int j = 0; j < path.size(); j++) {
                                                HashMap point = path.get(j);

                                                double lat = Double.parseDouble(point.get("lat").toString());
                                                double lng = Double.parseDouble(point.get("lng").toString());
                                                LatLng position = new LatLng(lat, lng);

                                                points.add(position);
                                            }

                                            lineOptions.addAll(points);
                                            lineOptions.width(12);
                                            lineOptions.color(Color.RED);
                                            lineOptions.geodesic(true);
                                        }
                                        googleMap.addPolyline(lineOptions);
                                    });
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                                displayError(e.getMessage());
                            }
                            return false;
                        });
                    }
                }catch(Exception e) {
                    displayError(e.getMessage());
                }
            });
        });
    }

    public void displayError(String message){
        alertDialogBuilder.setTitle("Error")
                .setMessage(message)
                .setNegativeButton("Ok", (dialog, which) -> dialog.cancel()).show();
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

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;

        try{
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        }catch(Exception e){
            displayError(e.getMessage());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    class DownloadUrlTask implements Callable<String> {
        private final String url;

        public DownloadUrlTask(String url) {
            this.url = url;
        }

        @Override
        public String call() {
            // Some long running task
            String data = "";
            try {
                data = downloadUrl(url);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }
    }

    class PathParserTask implements Callable<List<List<HashMap<String,String>>>>{
        private final String jsonData;

        public PathParserTask(String jsonData) {
            this.jsonData = jsonData;
        }

        @Override
        public List<List<HashMap<String, String>>> call() throws Exception {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }
    }
}

/*
* W/System.err:     at org.json.JSONTokener.syntaxError(JSONTokener.java:449)
        at org.json.JSONTokener.nextValue(JSONTokener.java:97)
        at org.json.JSONObject.<init>(JSONObject.java:159)
        at org.json.JSONObject.<init>(JSONObject.java:176)
        at com.example.task_app.MainActivity$PathParserTask.call(MainActivity.java:459)
        at com.example.task_app.MainActivity$PathParserTask.call(MainActivity.java:447)
        at com.example.task_app.async.TaskRunner.lambda$executeAsync$1$TaskRunner(TaskRunner.java:21)
        at com.example.task_app.async.-$$Lambda$TaskRunner$1_b7gPvWRi6ID-cvwnaxSvcKcaI.run(Unknown Source:6)
        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1167)
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:641)
        at java.lang.Thread.run(Thread.java:764)
W/System.err: java.lang.NullPointerException: Attempt to invoke interface method 'int java.util.List.size()' on a null object reference
W/System.err:     at com.example.task_app.MainActivity.lambda$null$3(MainActivity.java:325)
        at com.example.task_app.-$$Lambda$MainActivity$Gmxn9nN7-tjP3eZMyhwRkbBTywk.onComplete(Unknown Source:4)
        at com.example.task_app.async.TaskRunner.lambda$null$0(TaskRunner.java:28)
        at com.example.task_app.async.-$$Lambda$TaskRunner$igo_2bh6NtS0mJ5ivwNxCVtRHno.run(Unknown Source:4)
        at android.os.Handler.handleCallback(Handler.java:873)
        at android.os.Handler.dispatchMessage(Handler.java:99)
        at android.os.Looper.loop(Looper.java:216)
        at android.app.ActivityThread.main(ActivityThread.java:7258)
        at java.lang.reflect.Method.invoke(Native Method)
        at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:494)
        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:975)
* */

