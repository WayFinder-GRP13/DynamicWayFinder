package com.group13.dynamicwayfinder.Activities.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatCallback;

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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.group13.dynamicwayfinder.R;
import com.group13.dynamicwayfinder.Utils.RestAPIRequestInformation;
import com.group13.dynamicwayfinder.Utils.UserSettings.BusSettings;
import com.group13.dynamicwayfinder.Utils.UserSettings.CarSettings;
import com.group13.dynamicwayfinder.Utils.UserSettings.CycleSettings;
import com.group13.dynamicwayfinder.Utils.UserSettings.RailSettings;
import com.group13.dynamicwayfinder.Utils.UserSettings.ScaleSettings;
import com.group13.dynamicwayfinder.Utils.UserSettings.UserSettings;
import com.group13.dynamicwayfinder.Utils.UserSettings.WalkSettings;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MapActivity extends AppCompatActivity implements AppCompatCallback, LocationListener, OnMapReadyCallback  {
    private GoogleMap mMap;
    private android.location.LocationManager lm;
    private Marker markerLocation;
    private Marker AddressmarkerLocation;
    private AddressFetcher addressFetcher;
    FloatingActionButton floatingActionButton;
    private BottomSheetBehavior mBottomSheetBehavior1;
    private TopSheetBehavior mTopSheetBehavior1;
    private ImageView backArrow;
    private ListView listView;
    private Location loc;
    public double currentLong;
    public double currentLat;
    private ServerFetcher serverFetcher;

    LocationManager locationManager;

    LinearLayout tapactionlayout;
    LinearLayout toplayout;
    private SeekBar seekBarSpeed,seekBarEnv,seekBarCost;
    View bottomSheet;
    View topSheet;
    private SearchView sv;
    //private LinearLayout linearEnv,linearCost,linearTime;
    private ImageView linearEnv,linearCost,linearTime;
    private SearchView sbar;
    private Boolean firstTime = true;

    private List<LatLng> routeList = new ArrayList<>();

    private TextView startingLocation,destinationLocation;

    private boolean isChecked = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        //how to call the switches

        //Switch bicycleSwitch = (Switch) findViewById(R.id.bicycleSwitch);



        destinationLocation = findViewById(R.id.search3);
        startingLocation = findViewById(R.id.search);



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
//
//                                        listOfAddresses(startingLocation.getText().toString()+" Ireland");
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





        // starting location once they press enter a tag will show that location



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


                            mTopSheetBehavior1.setState(TopSheetBehavior.STATE_COLLAPSED);

                            // server code called here
                            callServer();


                            onMapSearch(destinationLocation);



                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });





        seekBarSpeed = findViewById(R.id.simpleSeekBarSpeed);
        seekBarEnv= findViewById(R.id.simpleSeekBarEnv);
        seekBarCost = findViewById(R.id.simpleSeekBarCost);

        linearEnv = findViewById(R.id.enviormentImage);
        linearCost = findViewById(R.id.costImage);
        linearTime= findViewById(R.id.timeImage);

        backArrow = findViewById(R.id.arrow_back);

        floatingActionButton =  findViewById(R.id.fab);
        tapactionlayout =  findViewById(R.id.tap_action_layout);
        toplayout = findViewById(R.id.top_tap_action);



        topSheet = findViewById(R.id.searchbardisplay);
        bottomSheet = findViewById(R.id.bottom_sheet1);


// program the topSheet for starting point and destination

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
                LatLng CurrentLatLngClick = new LatLng(arg0.latitude,arg0.longitude);
                addAddressMarker(CurrentLatLngClick);
                addressFetcher.sendlocationsAddressRequest(CurrentLatLngClick);
            }
        });
        startGettingLocations();

    }



    public void onMapSearch(TextView view) {

        String location = view.getText().toString()+" Ireland";
        List<Address> addressList = null;

        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 5);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
//            CameraUpdate zoom=CameraUpdateFactory.zoomOut();


            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
//            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//            mMap.animateCamera(zoom);



            // Get the startinglocation value and implement zoom on route


            TextView startingLocation2 = findViewById(R.id.search);



            ////// Need to get current location method


            if(startingLocation2.getText().toString().equals("")){

//              You can use both ways

                System.out.println(currentLong);
                System.out.println(currentLat);

                Location location2 = mMap.getMyLocation();
                double logi = location2.getLongitude();
                double lata = location2.getLatitude();
                LatLng loca = new LatLng(lata,logi);

                System.out.println(loca);
//
                List<LatLng> routeList2 = new ArrayList<>();
//
                routeList2.add(loca);
                routeList2.add(latLng);
                zoomRoute(mMap,routeList2);


            }
            else {


                String startingLoc = startingLocation2.getText().toString() + " Ireland";

                List<Address> addressList2 = null;
                Geocoder geocoder2 = new Geocoder(this);


                try {
                    addressList2 = geocoder2.getFromLocationName(startingLoc, 5);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Address address2 = addressList2.get(0);
                LatLng latLngStart = new LatLng(address2.getLatitude(), address2.getLongitude());


                List<LatLng> routeList = new ArrayList<>();
                routeList.add(latLngStart);
                routeList.add(latLng);

                //System.out.println(routeList);
                zoomRoute(mMap, routeList);
            }

        }
    }
    public void onMapSearchStarting(TextView view) {

        String location = view.getText().toString()+" Ireland";
        List<Address> addressList = null;

        if (location != null || !location.equals("Current Location")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
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

        int routePadding = 150;
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
    }
    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public String listOfAddresses(String str) {

        List<Address> addressList = null;



        Geocoder geocoder = new Geocoder(this);
        try {
            addressList = geocoder.getFromLocationName(str, 5);

//            ArrayAdapter adapter = new ArrayAdapter<Address>(this,
//                    android.R.layout.simple_list_item_1,
//                    addressList);
//
//            listView.setAdapter(adapter);

            for(Address location:addressList){
                System.out.println(location.getAddressLine(0));

                return location.getAddressLine(0);



                //location.getAddressLine(0);
            }
//                CharSequence text = addressList.toString();
//
//
//
//                int duration = Toast.LENGTH_LONG;
//
//                Toast toast = Toast.makeText(this, text, duration);
//                toast.show();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
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
        System.out.println("Response is: "+response);
    }

//    public static Bitmap createCustomMarker(Context context, @DrawableRes int resource, String _name) {
//
//        @SuppressLint("ResourceType") View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.drawable.current_location_icon, null);
//
//
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
//        return bitmap;
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
}