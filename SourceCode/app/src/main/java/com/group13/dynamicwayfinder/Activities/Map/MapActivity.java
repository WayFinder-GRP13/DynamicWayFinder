package com.group13.dynamicwayfinder.Activities.Map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.group13.dynamicwayfinder.R;
import com.group13.dynamicwayfinder.Utils.HTTPGetRequest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MapActivity extends AppCompatActivity implements AppCompatCallback, LocationListener, OnMapReadyCallback {
    private GoogleMap mMap;
    private android.location.LocationManager lm;
    private Marker markerLocation;
    private Marker AddressmarkerLocation;
    private AddressFetcher addressFetcher;
    FloatingActionButton floatingActionButton;
    private BottomSheetBehavior mBottomSheetBehavior1;
    LinearLayout tapactionlayout;
    private SeekBar seekBarSpeed, seekBarEnv, seekBarCost;
    View bottomSheet;
    //private LinearLayout linearEnv,linearCost,linearTime;
    private ImageView linearEnv, linearCost, linearTime;
    private boolean isChecked = true;

   //------------------------------
    String lat_s;
    String long_s;
    String lat_e;
    String long_e;
    String http_response2;
    JsonParser parser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        //how to call the switches

        //Switch bicycleSwitch = (Switch) findViewById(R.id.bicycleSwitch);
        seekBarSpeed = findViewById(R.id.simpleSeekBarSpeed);
        seekBarEnv = findViewById(R.id.simpleSeekBarEnv);
        seekBarCost = findViewById(R.id.simpleSeekBarCost);

        linearEnv = findViewById(R.id.enviormentImage);
        linearCost = findViewById(R.id.costImage);
        linearTime = findViewById(R.id.timeImage);

        //floating action button : refresh current location
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGettingLocations();
            }
        });

        tapactionlayout = (LinearLayout) findViewById(R.id.tap_action_layout);
        bottomSheet = findViewById(R.id.bottom_sheet1);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior1.setPeekHeight(120);
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior1.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    tapactionlayout.setVisibility(View.VISIBLE);
                }

                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    tapactionlayout.setVisibility(View.GONE);
                }

                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    tapactionlayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        tapactionlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });
        mapFragment.getMapAsync(this);
        addressFetcher = new AddressFetcher(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //sets the map as clickable
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {
                // TODO Auto-generated method stub
                 LatLng CurrentLatLngClick = new LatLng(arg0.latitude,arg0.longitude);
                 addAddressMarker(CurrentLatLngClick);
                 addressFetcher.sendlocationsAddressRequest(CurrentLatLngClick);
            }
        });
        startGettingLocations();

        //--------------------------------------------------------------

        GetLuase();
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {

                mMap.clear();
                addMarker(markerLocation.getPosition());
                GetLuase();

                Log.d("dd1123213213","Marker Click");
                lat_s = String.valueOf(markerLocation.getPosition().latitude);
                long_s = String.valueOf(markerLocation.getPosition().longitude);

                lat_e = String.valueOf(marker.getPosition().latitude);
                long_e = String.valueOf(marker.getPosition().longitude);

                try {
                    HTTPGetRequest TomTom = new HTTPGetRequest();
                    http_response2 = TomTom.execute("https://api.tomtom.com/routing/1/calculateRoute/"+lat_s+"%2C"+long_s+"%3A" +lat_e + "%2C" + long_e +"/json?routeRepresentation=polyline&routeType=fastest&travelMode=pedestrian&key=hsG3k8dTKXUpcbecSrGn3Gx4MWrCGAJG").get();
                    if (!http_response2.isEmpty()) {
                        Object object = parser.parse(http_response2);

                        JsonObject all_route = (JsonObject) object;
                        JsonArray routes = (JsonArray)all_route.get("routes");

                        JsonObject points = (JsonObject) routes.get(0);
                        JsonArray points2 = (JsonArray)points.get("legs");

                        JsonObject dd = (JsonObject)points2.get(0);
                        JsonArray d1d = (JsonArray)dd.get("points");

                        ArrayList<LatLng> arrayPoints = new ArrayList<>();
                        arrayPoints.clear();
                        for(int i=0; i<d1d.size(); i++){
                            JsonObject pointObject = d1d.get(i).getAsJsonObject();
                            Log.d("dddddd", pointObject.toString());
                            arrayPoints.add(new LatLng(Double.parseDouble(pointObject.get("latitude").toString()),Double.parseDouble(pointObject.get("longitude").toString())));
                        }
                        drawTestRoute(arrayPoints);
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                CameraPosition.Builder CamerBuilder = new CameraPosition.Builder();

                Location start = new Location("start");
                Location end = new Location("end");
                start.setLatitude(markerLocation.getPosition().latitude);
                start.setLongitude(markerLocation.getPosition().longitude);
                end.setLatitude(marker.getPosition().latitude);
                end.setLongitude(marker.getPosition().longitude);

                float distace = start.distanceTo(end);

                CamerBuilder.target(new LatLng((Double.parseDouble(lat_e)+Double.parseDouble(lat_s))/2, (Double.parseDouble(long_e)+Double.parseDouble(long_s))/2));

                int cal_distance = (int) ((distace-2400)/400);

                switch (cal_distance){
                    case -2 :
                        CamerBuilder.zoom(16);
                        Log.d("distacne", distace + " zoom : 16");
                        break;

                    case -1 :
                        CamerBuilder.zoom(15);
                        Log.d("distacne", distace + " zoom : 16");
                        break;

                    case 1:
                        CamerBuilder.zoom(13);
                        Log.d("distacne", distace + " zoom : 16");
                        break;

                    case 2:
                        CamerBuilder.zoom(12);
                        break;

                    case 3:
                        CamerBuilder.zoom(11);
                        break;

                    case 4 :
                        CamerBuilder.zoom(10);
                        Log.d("distacne", distace + " zoom : 10");
                        break;

                    case 5:
                        CamerBuilder.zoom(9);
                        Log.d("distacne", distace + " zoom : 9");
                        break;

                    case 6 :
                        CamerBuilder.zoom(8);
                        Log.d("distacne", distace + " zoom : 8");
                        break;


                    default:
                        CamerBuilder.zoom(14);
                        Log.d("distacne", distace + " zoom : 14");
                        break;
                }

                CamerBuilder.build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CamerBuilder.build()));
                return true;
            }
        });
    }

    public void GetLuase() {

        String http_response = null;
        HTTPGetRequest API_server = new HTTPGetRequest();
        try {
            http_response = API_server.execute("https://group13aseserver.herokuapp.com/luas").get();
            Log.d("ddd", http_response);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        parser = new JsonParser();

        if (!http_response.isEmpty()) {
            Object object = parser.parse(http_response);
            JsonArray luas_station = (JsonArray) object;

            for (int i = 0; i < luas_station.size(); i++) {
                JsonObject jsonObject = luas_station.get(i).getAsJsonObject();
                addTestMarker(jsonObject);
            }
        }
    }

    private void startGettingLocations() {
        lm = (android.location.LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isGPS = lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        boolean isNetwork = lm.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);
        long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;// Distance in meters
        long MIN_TIME_BW_UPDATES = 100;// Time in milliseconds

        //Starts requesting location updates
        //This line stops gps being used as its not useful indoors comment out for gps location
        //over network location
        try {
            isGPS = false;
            if (isGPS) {
                lm.requestLocationUpdates(
                        android.location.LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

            } else if (isNetwork) {
                // from Network Provider
                lm.requestLocationUpdates(
                        android.location.LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            }
        }catch(SecurityException e){

        }
    }

    public void TurnOnOff(View v) throws IOException, XmlPullParserException {

        Resources res = getResources();
        if (isChecked == true) {
            isChecked = false;

            switch (v.getId()) {
                case R.id.costImage:
                    seekBarCost.setEnabled(isChecked);
                    linearCost.setBackground(Drawable.createFromXml(res, res.getXml(R.xml.border)));

                    break;

                case R.id.enviormentImage:
                    seekBarEnv.setEnabled(isChecked);
                    linearEnv.setBackground(Drawable.createFromXml(res, res.getXml(R.xml.border)));

                    break;
                case R.id.timeImage:
                    seekBarSpeed.setEnabled(isChecked);
                    linearTime.setBackground(Drawable.createFromXml(res, res.getXml(R.xml.border)));

                    break;

                default:

                    break;
            }
        } else {
            isChecked = true;

            switch (v.getId()) {

                case R.id.costImage:
                    seekBarCost.setEnabled(isChecked);
                    linearCost.setBackground(Drawable.createFromXml(res, res.getXml(R.xml.border2)));

                    break;
                case R.id.enviormentImage:
                    seekBarEnv.setEnabled(isChecked);
                    linearEnv.setBackground(Drawable.createFromXml(res, res.getXml(R.xml.border2)));

                    break;
                case R.id.timeImage:
                    seekBarSpeed.setEnabled(isChecked);
                    linearTime.setBackground(Drawable.createFromXml(res, res.getXml(R.xml.border2)));

                    break;

                default:

                    break;
            }
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        LatLng latlng = new LatLng(location.getLatitude(),location.getLongitude());
        addMarker(latlng);
        System.out.println("location happened");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    private void addMarker(LatLng latLng) {
        if (latLng == null) {
            return;
        }
        if (markerLocation != null) {
            markerLocation.remove();
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("New Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        if (mMap != null)
            markerLocation = mMap.addMarker(markerOptions);

       CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latLng.latitude, latLng.longitude))
                .zoom(16)
                .build();


       if (mMap != null)
           mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }
    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void updateAddressOnMap(String address) {
        LatLng location = AddressmarkerLocation.getPosition();

        if (AddressmarkerLocation != null) {
            AddressmarkerLocation.remove();
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(location);
        markerOptions.title(address);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        if (mMap != null)

            AddressmarkerLocation = mMap.addMarker(markerOptions);


        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.latitude, location.longitude))
                .zoom(16)
                .build();

        if (mMap != null)
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void addAddressMarker(LatLng location) {

        if (AddressmarkerLocation != null) {
            AddressmarkerLocation.remove();
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(location);
        markerOptions.title("Getting Address");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        if (mMap != null)
            AddressmarkerLocation = mMap.addMarker(markerOptions);


        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.latitude, location.longitude))
                .zoom(16)
                .build();

        if (mMap != null)
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    //-----------------------------------------------------------------------------------------
    public void addTestMarker(JsonObject jsonObject){

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng( Double.parseDouble(jsonObject.get("Latitude").toString()), Double.parseDouble(jsonObject.get("Longitude").toString())));
        markerOptions.title(jsonObject.get("Pronunciation").toString());
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        if (mMap != null)
            mMap.addMarker(markerOptions);
    }

    public void drawTestRoute(ArrayList<LatLng> arrayPoints){

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLUE); polylineOptions.width(20);
        polylineOptions.addAll(arrayPoints);
        mMap.addPolyline(polylineOptions);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}