package com.group13.dynamicwayfinder.Activities.Map;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatCallback;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.maps.android.PolyUtil;
import com.group13.dynamicwayfinder.R;
import com.group13.dynamicwayfinder.Utils.HTTPGetRequest;
import com.group13.dynamicwayfinder.Utils.RestAPIRequestInformation;
import com.group13.dynamicwayfinder.Utils.ServerClasses.FinalRoute;
import com.group13.dynamicwayfinder.Utils.ServerClasses.Node;
import com.group13.dynamicwayfinder.Utils.UserSettings.BusSettings;
import com.group13.dynamicwayfinder.Utils.UserSettings.CarSettings;
import com.group13.dynamicwayfinder.Utils.UserSettings.CycleSettings;
import com.group13.dynamicwayfinder.Utils.UserSettings.RailSettings;
import com.group13.dynamicwayfinder.Utils.UserSettings.ScaleSettings;
import com.group13.dynamicwayfinder.Utils.UserSettings.UserSettings;
import com.group13.dynamicwayfinder.Utils.UserSettings.WalkSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MapActivity extends AppCompatActivity implements AppCompatCallback, LocationListener, OnMapReadyCallback  {
    private GoogleMap mMap;
    private android.location.LocationManager lm;
    private Marker markerLocation;
    private Marker AddressmarkerLocation;
    private AddressFetcher addressFetcher;
    FloatingActionButton floatingActionButton, exploreButton;
    private BottomSheetBehavior mBottomSheetBehavior1, mBottomSheetBehavior2;
    private TopSheetBehavior mTopSheetBehavior1;
    private ImageView backArrow;
    private ListView listView;

    private HashMap<Integer,Float> TransportColour;

    private Address location;
    private Location loc;
    public double currentLong;
    public double currentLat;
    private ServerFetcher serverFetcher;
    public double valueEnv;
    public double valueSpeed;
    public double valueCost;
    private ListView searchList;

    int step = 1;
    int max = 10;
    int min = 1;

    LinearLayout tapactionlayout, toplayout;
    private SeekBar seekBarSpeed, seekBarEnv, seekBarCost;
    View bottomSheet, topSheet, bottomSheet2;
    private SearchView sv;
    //private LinearLayout linearEnv,linearCost,linearTime;
    private ImageView linearEnv, linearCost, linearTime;
    private SearchView sbar;
    private Boolean firstTime = true;

    private List<LatLng> routeList = new ArrayList<>();

    private TextView startingLocation, destinationLocation;
    private TextView carTime, trainTime, busTime, walkTime, bicycleTime;


    private boolean isChecked = true;

    private Switch carSwitch, trainSwitch, busSwitch, walkSwitch, bicycleSwitch;


    private String popup_address;
    private ImageView weather_icon;
    private ImageButton btn_clear1, btn_clear2;
    long backKeyPressedTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);



        TransportColour = new HashMap<>();
        TransportColour.put(1,BitmapDescriptorFactory.HUE_VIOLET); //bus
        TransportColour.put(2,BitmapDescriptorFactory.HUE_ORANGE);  //train
        TransportColour.put(3,BitmapDescriptorFactory.HUE_YELLOW); //cycle


        floatingActionButton = findViewById(R.id.fab);
        exploreButton = findViewById(R.id.modesMap);
        destinationLocation = findViewById(R.id.search3);
        startingLocation = findViewById(R.id.search);
        searchList = findViewById(R.id.searchListView);

        carSwitch = findViewById(R.id.carSwitch);
        busSwitch = findViewById(R.id.busSwitch);
        trainSwitch = findViewById(R.id.trainSwitch);
        walkSwitch = findViewById(R.id.walkSwitch);
        bicycleSwitch = findViewById(R.id.bicycleSwitch);

        carTime = findViewById(R.id.carTime);
        busTime = findViewById(R.id.busTime);
        trainTime = findViewById(R.id.trainTime);
        walkTime = findViewById(R.id.walkTime);
        bicycleTime = findViewById(R.id.bicycleTime);


        seekBarSpeed = findViewById(R.id.simpleSeekBarSpeed);
        seekBarEnv = findViewById(R.id.simpleSeekBarEnv);
        seekBarCost = findViewById(R.id.simpleSeekBarCost);

        linearEnv = findViewById(R.id.enviormentImage);
        linearCost = findViewById(R.id.costImage);
        linearTime = findViewById(R.id.timeImage);

        backArrow = findViewById(R.id.arrow_back);

        weather_icon = findViewById(R.id.weather_icon);
        btn_clear1 = findViewById(R.id.clear_button1);
        btn_clear1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startingLocation.setText(null);
            }
        });
        btn_clear2 = findViewById(R.id.clear_button2);
        btn_clear2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                destinationLocation.setText(null);
            }
        });

        floatingActionButton =  findViewById(R.id.fab);
        tapactionlayout =  findViewById(R.id.tap_action_layout);
        toplayout = findViewById(R.id.top_tap_action);



        topSheet = findViewById(R.id.searchbardisplay);
        bottomSheet = findViewById(R.id.bottom_sheet1);
        bottomSheet2 = findViewById(R.id.bottom_sheet2);


// program the topSheet for starting point and destination

        seekBarSpeed.setMax( (max - min) / step );
        seekBarEnv.setMax( (max - min) / step );
        seekBarCost.setMax( (max - min) / step );




        // starting location once they press enter a tag will show that location


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();

                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }
                Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

                if (null != location) {
                    // map is an instance of GoogleMap
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                            .zoom(17)                   // Sets the zoom
                            // Sets the tilt of the camera to 30 degrees
                            .build();                   // Creates a CameraPosition from the builder
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                }
            }
        });


        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior1.setPeekHeight(120);
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);

        mBottomSheetBehavior2 = BottomSheetBehavior.from(bottomSheet2);
        mBottomSheetBehavior2.setPeekHeight(120);
        mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_COLLAPSED);



        exploreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tapactionlayout.setVisibility(LinearLayout.INVISIBLE);

                bottomSheet.setVisibility(LinearLayout.INVISIBLE);
                mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_EXPANDED);

                mBottomSheetBehavior2.addBottomSheetCallback (new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet1, int newState) {
                        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {

                            mBottomSheetBehavior2.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            mBottomSheetBehavior2.setPeekHeight(0);
                            tapactionlayout.setVisibility(LinearLayout.VISIBLE);
                            bottomSheet.setVisibility(LinearLayout.VISIBLE);



                        }

                        if (newState == BottomSheetBehavior.STATE_EXPANDED) {

                        }

                        if (newState == BottomSheetBehavior.STATE_DRAGGING) {

                        }
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {


                    }
                });



            }

        });


        carSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position

                if(isChecked){

                    carTime.setText("20 Mins");



                } else{

                    carTime.setText("------");
                }
            }
        });
        trainSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position

                if(isChecked){

                    trainTime.setText("7 Mins");



                } else{

                    trainTime.setText("------");
                }
            }
        });
        busSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position

                if(isChecked){

                    busTime.setText("12 Mins");



                } else{

                    busTime.setText("------");
                }
            }
        });
        walkSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position

                if(isChecked){

                    walkTime.setText("6 Mins");


                } else{

                    walkTime.setText("------");
                }
            }
        });
        bicycleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position

                if(isChecked){


                    bicycleTime.setText("4 Mins");

                } else{

                    bicycleTime.setText("------");
                }
            }
        });



        startingLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startingLocation.setText(null);
            }
        });

        startingLocation.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:


                            onMapSearchStarting(startingLocation);

                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });


        //once the user clicks enter on the destination textView a map tag will be created

        destinationLocation.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:

                            mMap.clear();
                            mTopSheetBehavior1.setState(TopSheetBehavior.STATE_COLLAPSED);

                            // server code called here
                            //callServer();

                            try {
                                onMapSearch(destinationLocation);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        seekBarEnv.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                double valueEnv = min + (progress * step);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {




            }
        });

        seekBarCost.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                double valueCost = min + (progress * step);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {



            }
        });

        seekBarSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                double valueSpeed = min + (progress * step);

                //System.out.println(valueSpeed);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {




            }
        });



        mTopSheetBehavior1 = TopSheetBehavior.from(topSheet);
        mTopSheetBehavior1.setPeekHeight(150);
        mTopSheetBehavior1.setState(TopSheetBehavior.STATE_COLLAPSED);
        mTopSheetBehavior1.setTopSheetCallback(new TopSheetBehavior.TopSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View topSheet, int newState) {

                if (newState == TopSheetBehavior.STATE_COLLAPSED) {
                    toplayout.setVisibility(View.VISIBLE);
                }

                if (newState == TopSheetBehavior.STATE_EXPANDED) {
                    toplayout.setVisibility(View.GONE);
                }

                if (newState == TopSheetBehavior.STATE_DRAGGING) {
                    toplayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset, @Nullable Boolean isOpening) {

            }
        });
        toplayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTopSheetBehavior1.getState()==TopSheetBehavior.STATE_COLLAPSED)
                {
                    mTopSheetBehavior1.setState(TopSheetBehavior.STATE_EXPANDED);
                    ImageView weatehr_icon = findViewById(R.id.weather_icon);

                    String imageUrl = "https://openweathermap.org/img/wn/" + getWeather() + "@2x.png";

                    Glide.with(getApplicationContext()).load(imageUrl).into(weatehr_icon);
                    startingLocation.setText(null);
                    destinationLocation.setText(null);

                    backArrow.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            mTopSheetBehavior1.setState(TopSheetBehavior.STATE_COLLAPSED);
                            mMap.clear();

                            startingLocation.setText(null);
                            destinationLocation.setText(null);




                        }

                    });
                }
            }
        });

        //Program the bottom sheet for user preference

        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior1.setPeekHeight(120);
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior1.addBottomSheetCallback (new BottomSheetBehavior.BottomSheetCallback() {
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
                if(mBottomSheetBehavior1.getState()==BottomSheetBehavior.STATE_COLLAPSED)
                {
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });




        mapFragment.getMapAsync(this);

        addressFetcher = new AddressFetcher(this);
        serverFetcher = new ServerFetcher(this);





    }

    // this method calls the server
    private void callServer() {
        BusSettings busSettings = new BusSettings(true,1);
        RailSettings railSettings = new RailSettings(true,1);
        CarSettings carSettings = new CarSettings(true,1);
        CycleSettings cycleSettings = new CycleSettings(true,1);
        WalkSettings walkSettings = new WalkSettings(true,1);
        ScaleSettings scaleSettings = new ScaleSettings(5,5,5);
        UserSettings userSettings = new UserSettings(1,busSettings,railSettings,carSettings,walkSettings,cycleSettings,scaleSettings);
        serverFetcher.sendServerRequest(new RestAPIRequestInformation(1,"mike","55.5","55.5","55.5","55.5",userSettings));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //sets the map as clickable
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng arg0) {
                // TODO Auto-generated method stub
                LatLng CurrentLatLngClick = new LatLng(arg0.latitude, arg0.longitude);
                addAddressMarker(CurrentLatLngClick);
                addressFetcher.sendlocationsAddressRequest(CurrentLatLngClick);

                Popup_window(arg0);

            }
        });
        startGettingLocations();
        mMap.getUiSettings().setMyLocationButtonEnabled(false);


//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//
//                Popup_window(marker.getPosition());
//                return false;
//            }
//        });

    }



    public void onMapSearch(TextView view) throws Exception {

        String location = view.getText().toString() + " Dublin";
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 5);


                if (addressList == null) throw new Exception("No results for Geocoder");

                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
//            CameraUpdate zoom=CameraUpdateFactory.zoomOut();


                mMap.addMarker(new MarkerOptions().position(latLng).title(address.getAddressLine(0)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
//            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//            mMap.animateCamera(zoom);


                // Get the startinglocation value and implement zoom on route


                TextView startingLocation2 = findViewById(R.id.search);


                ////// Need to get current location method


                if (startingLocation2.getText().toString().equals("")) {

//              You can use both ways

                    System.out.println(currentLong);
                    System.out.println(currentLat);

                    Location location2 = mMap.getMyLocation();
                    double logi = location2.getLongitude();
                    double lata = location2.getLatitude();
                    LatLng loca = new LatLng(lata, logi);

                    System.out.println(loca);
//
                    //List<LatLng> routeList2 = new ArrayList<>();
                    //routeList2.add(loca);
                    //routeList2.add(latLng);
                    //zoomRoute(mMap,roufteList2);
                    startingLocation.setText("Current Location");
                    getRoute(loca, latLng);


                } else {


                    String startingLoc = startingLocation2.getText().toString() + " Ireland";

                    List<Address> addressList2 = null;
                    Geocoder geocoder2 = new Geocoder(this);


                    try {
                        addressList2 = geocoder2.getFromLocationName(startingLoc, 5);
                        Address address2 = addressList2.get(0);
                        LatLng latLngStart = new LatLng(address2.getLatitude(), address2.getLongitude());

                        List<LatLng> routeList = new ArrayList<>();
                        routeList.add(latLngStart);
                        routeList.add(latLng);

                        //System.out.println(routeList);
                        zoomRoute(mMap, routeList);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void onMapSearchStarting(TextView view) {

        String location = view.getText().toString()+" Ireland";
        List<Address> addressList = null;

        if (location != null || !location.equals("Current Location")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 5);

                if (addressList == null) throw new Exception ("No results for Geocoder");

                Address address = addressList.get(0);



                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng).title(address.getAddressLine(0)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else{

            view.setText("Current Location");
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

    public void zoomRoute(GoogleMap googleMap, List<LatLng> lstLatLngRoute) {

        if (googleMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 200;
        LatLngBounds latLngBounds = boundsBuilder.build();

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
    }

    //controlling imageview turning on and off for seekbar for user preference
    public void TurnOnOff(View v) throws IOException, XmlPullParserException {

        Resources res = getResources();


        if (isChecked == true) {

            isChecked = false;

            switch (v.getId()) {


                case R.id.costImage:
                    seekBarCost.setEnabled(isChecked);
                    linearCost.setBackground(Drawable.createFromXml(res, res.getXml(R.xml.border)));
                    valueCost=1;
                    System.out.println(valueCost);
                    break;

                case R.id.enviormentImage:
                    seekBarEnv.setEnabled(isChecked);
                    linearEnv.setBackground(Drawable.createFromXml(res, res.getXml(R.xml.border)));
                    valueEnv=1;


                    break;
                case R.id.timeImage:
                    seekBarSpeed.setEnabled(isChecked);
                    linearTime.setBackground(Drawable.createFromXml(res, res.getXml(R.xml.border)));
                    valueSpeed=1;

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

        currentLong = location.getLongitude();
        currentLat = location.getLatitude();

        if(firstTime) {
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            addMarker(latlng);
        }
        firstTime=false;
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

        mMap.setMyLocationEnabled(true);


        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latLng.latitude, latLng.longitude))
                .zoom(16)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);

    }
    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public List<Address> listOfAddresses(String str) {

        List<Address> addressList = null;

        Geocoder geocoder = new Geocoder(this);
        try {
            addressList = geocoder.getFromLocationName(str, 5);

//            ArrayAdapter adapter = new ArrayAdapter<Address>(this,
//                    android.R.layout.simple_list_item_1,
//                    addressList);
//
//            listView.setAdapter(adapter);

            return addressList;


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateAddressOnMap(String address) {
        LatLng location = AddressmarkerLocation.getPosition();

        if (AddressmarkerLocation != null) {
            AddressmarkerLocation.remove();
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(location);
        markerOptions.title(address);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        if (mMap != null)

            AddressmarkerLocation = mMap.addMarker(markerOptions);


        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.latitude, location.longitude))
                //removed zoom cause it kept zooming to current gps location
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
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        if (mMap != null)
            AddressmarkerLocation = mMap.addMarker(markerOptions);


        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.latitude, location.longitude))
                .zoom(16)
                .build();

        if (mMap != null)
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    // this method draws the route on the map
    public void updateRouteOnMap(String response) {
        System.out.println("Response is: " + response);
        ArrayList<FinalRoute> finalRouteList = new ArrayList();

        JSONArray scores = null;
        try {
            scores = new JSONArray(response);

            for (int i = 0; i < scores.length(); i++) {
                JSONObject element = scores.getJSONObject(i);
                System.out.println(element);
                String PolyLine = element.getString("overviewPolyline");
                JSONObject destination = element.getJSONObject("destination");
                Node destNode = new Node(destination.getString("name"), destination.getInt("stopId"), destination.getInt("transportType"), destination.getDouble("latitude"), destination.getDouble("longitudue"), destination.getDouble("score"));
                JSONObject origin = element.getJSONObject("origin");
                Node originNode = new Node(origin.getString("name"), origin.getInt("stopId"), origin.getInt("transportType"), origin.getDouble("latitude"), origin.getDouble("longitudue"), origin.getDouble("score"));
                finalRouteList.add(new FinalRoute(destNode, originNode, PolyLine));

            }
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Error couldn't parse result");
        }
        boolean firstTime=true;
        for (FinalRoute route : finalRouteList) {
            if(firstTime==true) {
                addTransportMarker(route.getOrigin());
                firstTime=false;
            }
            addTransportMarker(route.getDestination());
            decodePolyline(route.getOverviewPolyline());

            System.out.println(route.getOverviewPolyline());
            System.out.println(route.getOrigin().getName());
            System.out.println(route.getDestination().getName());
        }
    }

    public void addTransportMarker(Node point){

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(point.getLatitude(),point.getLongitudue()));
        markerOptions.title(point.getName()+"\n"+
                "Stop ID: "+point.getStopId());

        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(TransportColour.get(point.getTransportType())));
        if (mMap != null)
            mMap.addMarker(markerOptions);


        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(point.getLatitude(), point.getLongitudue()))
                .zoom(16)
                .build();

        if (mMap != null)
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void decodePolyline(String Polyline){
        List<LatLng> decodedPath = PolyUtil.decode(Polyline);
        com.google.android.gms.maps.model.Polyline line = null;
        if (line == null) {
            line = mMap.addPolyline(new PolylineOptions()
                    .width(8)
                    .color(Color.rgb(25, 151, 152))
                    .geodesic(true)
                    .addAll(decodedPath));
        }

    }

    @Override
    public void onBackPressed() {
        if(mTopSheetBehavior1.getState() == TopSheetBehavior.STATE_EXPANDED)
            mTopSheetBehavior1.setState(TopSheetBehavior.STATE_COLLAPSED);
        else{
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                mMap.clear();
                startingLocation.setText(null);
                destinationLocation.setText(null);

            }
            else{
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }
    }

    public String getWeather(){

        HTTPGetRequest weather_API = new HTTPGetRequest();
        String response = null;
        JsonParser parser = new JsonParser();

        try {
            response = weather_API.execute("https://group13aseserver.herokuapp.com/byCity/IE/dublin").get();
            Object object = parser.parse(response);
            JsonObject jsonObject = (JsonObject)object;
            JsonArray all_weather = (JsonArray) jsonObject.get("weather");

            JsonObject weather = (JsonObject) all_weather.get(0);
            String icon = weather.get("icon").toString();

            String unique_char =  "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
            icon  =icon.replaceAll(unique_char, "");
            Log.d("weathericon", icon.toString());

            return icon;

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getRoute(LatLng start, LatLng end){

        ArrayList<LatLng> point_list = new ArrayList<>();
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLUE);
        polylineOptions.width(15);

        String tomtom = "https://api.tomtom.com/routing/1/calculateRoute/" +start.latitude+"%2C"+start.longitude +"%3A"
                +end.latitude + "%2C"+ end.longitude +"/json?routeRepresentation=polyline&avoid=unpavedRoads&travelMode=pedestrian&key=hsG3k8dTKXUpcbecSrGn3Gx4MWrCGAJG";

        Log.d("route_api", tomtom);

        String response = null;
        HTTPGetRequest routeAPI = new HTTPGetRequest();
        JsonParser parser = new JsonParser();

        try {
            response = routeAPI.execute(tomtom).get();
            Log.d("route_api", response);
            if(response != null){
                Log.d("route_api", tomtom);
                Object object = parser.parse(response);
                JsonObject jsonObject = (JsonObject) object;
                JsonArray jsonArray = (JsonArray) jsonObject.get("routes");
                JsonObject object2 = (JsonObject) jsonArray.get(0);
                JsonArray routeArray = (JsonArray) object2.get("legs");
                JsonObject summary = (JsonObject)((JsonObject) routeArray.get(0)).get("summary");
                JsonArray routePoint = (JsonArray)((JsonObject) routeArray.get(0)).get("points");

                int min,hour =0;
                int sec;
                sec = summary.get("travelTimeInSeconds").getAsInt();

                hour = sec / 3600;
                min =  sec % 3600 / 60;

                if(hour == 0){
                    walkTime.setText(min + " Mins");

                }else{
                    walkTime.setText(hour + " H " + min + " M");
                }
                for (int i=0; i<routePoint.size(); i++){
                    JsonObject point = (JsonObject) routePoint.get(i);
                    point_list.add(new LatLng(Double.parseDouble(point.get("latitude").toString()), (Double.parseDouble(point.get("longitude").toString()))));
                }
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        polylineOptions.addAll(point_list);
        mMap.addPolyline(polylineOptions);
        zoomRoute(mMap,point_list);
    }

    public void Popup_window(final LatLng position){

        View dialogView = getLayoutInflater().inflate(R.layout.popup,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setView(dialogView);
        builder.setCancelable(false);

        TextView address_popup = (TextView)dialogView.findViewById(R.id.popup_address);
        Button direction_popup = (Button)dialogView.findViewById(R.id.popup_direction);

        Geocoder geocoder = new Geocoder(getApplicationContext());
        List<Address> addressList = null;

        try {
            addressList = geocoder.getFromLocation(position.latitude,position.longitude, 1);
            popup_address = addressList.get(0).getAddressLine(0);
            address_popup.setText(popup_address);

        } catch (IOException e) {
            e.printStackTrace();
        }

        direction_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startingLocation.setText("Current Location");
                destinationLocation.setText(popup_address);
                LatLng cur_loc = new LatLng(mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude());
                getRoute(cur_loc,position);
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                if(keyCode == keyEvent.KEYCODE_BACK){
                    alertDialog.dismiss();
                }
                return false;
            }
        });
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        alertDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
        params.y = 700;
        alertDialog.getWindow().setAttributes(params);
        alertDialog.show();
    }

//    public static Bitmap createCustomMarker(Context context, @DrawableRes int resource, String _name) {



///   Once user types into the textbar and finalizes location display a list of the top 5 locations

//        startingLocation.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void afterTextChanged(Editable s) {}
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start,
//                                          int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start,
//                                      int before, int count) {
//                if(s.length() != 0) {
//
//                    startingLocation.setOnKeyListener(new View.OnKeyListener()
//                    {
//                        public boolean onKey(View v, int keyCode, KeyEvent event)
//                        {
//                            if (event.getAction() == KeyEvent.ACTION_DOWN)
//                            {
//                                switch (keyCode)
//                                {
//                                    case KeyEvent.KEYCODE_DPAD_CENTER:
//                                    case KeyEvent.KEYCODE_ENTER:
//
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
//        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
//        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
//        marker.buildDrawingCache();
//        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        marker.draw(canvas);
//
//                                        List<Address> loca = listOfAddresses(startingLocation.getText().toString());
//
//                                        ArrayList<String> ar = new ArrayList<String>();
//
//                                        for(Address location:loca) {
//
//                                            ar.add(location.getAddressLine(0));
//                                            System.out.println(ar.size());
//
//                                            System.out.println(Arrays.toString(ar.toArray()));
//
//                                        }
//
//
//                                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(),
//                                                android.R.layout.simple_list_item_1, ar);
//
//                                        searchList.setAdapter(adapter);
//
//
//                                        return true;
//                                    default:
//                                        break;
//                                }
//                            }
//                            return false;
//                        }
//                    });
//                }
//            }
//        });






}