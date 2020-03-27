package com.group13.dynamicwayfinder.Activities.Map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.maps.android.PolyUtil;
import com.group13.dynamicwayfinder.Activities.Authentication.MainActivity;
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


public class MapActivity extends AppCompatActivity implements AppCompatCallback, LocationListener, OnMapReadyCallback {
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
    private Button logOutButton;

    private HashMap<Integer, Integer> TransportColour;

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

    LinearLayout tapactionlayout, toplayout, linearSearch, swappingLinear,envLinear, costLinear, timeLinear;
    private SeekBar seekBarSpeed, seekBarEnv, seekBarCost;
    View bottomSheet, topSheet, bottomSheet2;
    private SearchView sv;
    //private LinearLayout linearEnv,linearCost,linearTime;
    private ImageView linearEnv, linearCost, linearTime;
    private SearchView sbar;
    private Boolean firstTime = true;

    private List<LatLng> routeList = new ArrayList<>();

    private TextView startingLocation, destinationLocation;
    private TextView trainTime, busTime, walkTime, bicycleTime;
    private TextView weather_status, weather_temperature;
    private TextView costPrio,envPrio,speedPrio;

    //private TextView carTime;

    private boolean isChecked = true;

    private Switch trainSwitch, busSwitch, walkSwitch, bicycleSwitch;

    //private Switch carSwitch;
    private boolean busChecked = false;
    private boolean walkChecked = true;
    private boolean bicycleChecked = false;
    //private boolean carChecked = false;
    private boolean trainChecked = false;


    private Switch s;


    private String popup_address;
    private ImageView weather_icon;
    private ImageButton btn_clear1, btn_clear2;
    long backKeyPressedTime;
    private AlertDialog alertDialog;
    private LatLng serverRequestStartPos;
    private LatLng serverRequestEndPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        TransportColour = new HashMap<>();
        TransportColour.put(0, Color.GREEN); //walk
        TransportColour.put(1, Color.BLUE); //bus
        TransportColour.put(2, Color.YELLOW);  //train
        TransportColour.put(3, Color.CYAN); //cycle

        exploreButton = findViewById(R.id.modesMap);
        destinationLocation = findViewById(R.id.search3);
        startingLocation = findViewById(R.id.search);
        searchList = findViewById(R.id.searchListView);
        logOutButton = findViewById(R.id.logOutButton);

        //carSwitch = findViewById(R.id.carSwitch);
        busSwitch = findViewById(R.id.busSwitch);
        trainSwitch = findViewById(R.id.trainSwitch);
        walkSwitch = findViewById(R.id.walkSwitch);
        bicycleSwitch = findViewById(R.id.bicycleSwitch);

        // carSwitch.setChecked(carChecked);
        busSwitch.setChecked(busChecked);
        trainSwitch.setChecked(trainChecked);
        walkSwitch.setChecked(walkChecked);
        bicycleSwitch.setChecked(bicycleChecked);


        // carTime = findViewById(R.id.carTime);
        busTime = findViewById(R.id.busTime);
        trainTime = findViewById(R.id.trainTime);
        walkTime = findViewById(R.id.walkTime);
        bicycleTime = findViewById(R.id.bicycleTime);


        seekBarSpeed = findViewById(R.id.simpleSeekBarSpeed);
        seekBarEnv = findViewById(R.id.simpleSeekBarEnv);
        seekBarCost = findViewById(R.id.simpleSeekBarCost);

        costPrio = findViewById(R.id.costPrio);
        envPrio = findViewById(R.id.envPrio);
        speedPrio = findViewById(R.id.timePrio);

        swappingLinear = findViewById(R.id.swappingLinear);
        envLinear = findViewById(R.id.EnvLinear);
        costLinear = findViewById(R.id.CostLinear);
        timeLinear = findViewById(R.id.TimeLinear);
        linearEnv = findViewById(R.id.enviormentImage);
        linearCost = findViewById(R.id.costImage);
        linearTime = findViewById(R.id.timeImage);

        backArrow = findViewById(R.id.arrow_back);

        weather_icon = findViewById(R.id.weather_icon);
        weather_status = findViewById(R.id.weatherString);
        weather_temperature = findViewById(R.id.weatherString2);
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

        floatingActionButton = findViewById(R.id.fab);
        tapactionlayout = findViewById(R.id.tap_action_layout);
        toplayout = findViewById(R.id.top_tap_action);
        linearSearch = findViewById(R.id.LinearSearch);


        topSheet = findViewById(R.id.searchbardisplay);
        bottomSheet = findViewById(R.id.bottom_sheet1);
        bottomSheet2 = findViewById(R.id.bottom_sheet2);


// program the topSheet for starting point and destination

        seekBarSpeed.setMax((max - min) / step);
        seekBarEnv.setMax((max - min) / step);
        seekBarCost.setMax((max - min) / step);


        // starting location once they press enter a tag will show that location


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            //@RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                zoomToCurrentLocation();
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

                mBottomSheetBehavior2.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
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

//
//        carSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                // do something, the isChecked will be
//                // true if the switch is in the On position
//
//
//
//                if(isChecked){
//
//
//                    carTime.setText("------");
//                    carChecked=true;
//
//
//                } else{
//
//
//                    carTime.setText("------");
//                    carChecked=false;
//                }
//            }
//        });
        trainSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position


                if (isChecked) {

                    trainTime.setText("------");
                    trainChecked = true;


                } else {

                    trainTime.setText("------");
                    trainChecked = false;
                }
            }
        });
        busSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position


                if (isChecked) {

                    busTime.setText("------");
                    busChecked = true;


                } else {

                    busTime.setText("------");
                    busChecked = false;
                }
            }
        });
        walkSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position


                if (isChecked) {

                    walkTime.setText("------");
                    walkChecked = true;
                    walkSwitch.setChecked(true);


                } else {

                    walkTime.setText("------");
                    walkSwitch.setChecked(true);

                }
            }
        });
        bicycleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position


                if (isChecked) {


                    bicycleTime.setText("------");
                    bicycleChecked = true;

                } else {

                    bicycleTime.setText("------");
                    bicycleChecked = false;
                }
            }
        });


        startingLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startingLocation.setText(null);
            }
        });

        startingLocation.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
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

        destinationLocation.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:

                            mMap.clear();
                            mTopSheetBehavior1.setState(TopSheetBehavior.STATE_COLLAPSED);

                            // server code called here
                            //callServer();

                            try {
                                onMapSearch(destinationLocation);



                                mTopSheetBehavior1.setState(TopSheetBehavior.STATE_EXPANDED);
                                mTopSheetBehavior1.setPeekHeight(380);

                                //toplayout.setVisibility(View.GONE);
                                mTopSheetBehavior1.setTopSheetCallback(new TopSheetBehavior.TopSheetCallback() {
                                    @Override
                                    public void onStateChanged(@NonNull View topSheet, int newState) {

                                        if (newState == TopSheetBehavior.STATE_EXPANDED) {
                                            toplayout.setVisibility(LinearLayout.GONE);
                                        }

                                        if (newState == TopSheetBehavior.STATE_COLLAPSED) {
                                            toplayout.setVisibility(LinearLayout.GONE);
                                        }

                                        if (newState == TopSheetBehavior.STATE_DRAGGING) {
                                            toplayout.setVisibility(LinearLayout.GONE);
                                        }

                                    }

                                    @Override
                                    public void onSlide(@NonNull View bottomSheet, float slideOffset, @Nullable Boolean isOpening) {

                                    }
                                });


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

                int env = seekBar.getProgress();
                int cost = seekBarCost.getProgress();
                int speed = seekBarSpeed.getProgress();


                if(speed >= env && speed >= cost && env <= cost){


                    swappingLinear.removeView(envLinear);
                    swappingLinear.removeView(costLinear);
                    swappingLinear.removeView(timeLinear);



                    speedPrio.setText("1");
                    costPrio.setText("2");
                    envPrio.setText("3");

                    swappingLinear.addView(timeLinear);
                    swappingLinear.addView(costLinear);
                    swappingLinear.addView(envLinear);



                }else if(speed >= env && speed >= cost  && env >= cost) {

                    swappingLinear.removeView(envLinear);
                    swappingLinear.removeView(costLinear);
                    swappingLinear.removeView(timeLinear);



                    speedPrio.setText("1");
                    envPrio.setText("2");
                    costPrio.setText("3");

                    swappingLinear.addView(timeLinear);
                    swappingLinear.addView(envLinear);
                    swappingLinear.addView(costLinear);

                }
                else if(env >= speed && env >= cost && speed <= cost) {

                    swappingLinear.removeView(envLinear);
                    swappingLinear.removeView(costLinear);
                    swappingLinear.removeView(timeLinear);

                    envPrio.setText("1");
                    costPrio.setText("2");
                    speedPrio.setText("3");

                    swappingLinear.addView(envLinear);
                    swappingLinear.addView(costLinear);
                    swappingLinear.addView(timeLinear);



                } else if(env >= speed && env >= cost && speed >= cost) {

                    swappingLinear.removeView(envLinear);
                    swappingLinear.removeView(costLinear);
                    swappingLinear.removeView(timeLinear);

                    envPrio.setText("1");
                    costPrio.setText("3");
                    speedPrio.setText("2");

                    swappingLinear.addView(envLinear);
                    swappingLinear.addView(timeLinear);
                    swappingLinear.addView(costLinear);


                }
                else if(cost >= speed && cost >= env && speed <= env) {

                    swappingLinear.removeView(envLinear);
                    swappingLinear.removeView(costLinear);
                    swappingLinear.removeView(timeLinear);



                    costPrio.setText("1");
                    envPrio.setText("2");
                    speedPrio.setText("3");

                    swappingLinear.addView(costLinear);
                    swappingLinear.addView(envLinear);
                    swappingLinear.addView(timeLinear);






                } else if(cost >= speed && cost >= env && speed >= env) {


                    swappingLinear.removeView(envLinear);
                    swappingLinear.removeView(costLinear);
                    swappingLinear.removeView(timeLinear);

                    costPrio.setText("1");
                    envPrio.setText("3");
                    speedPrio.setText("2");

                    swappingLinear.addView(costLinear);
                    swappingLinear.addView(timeLinear);
                    swappingLinear.addView(envLinear);

                }

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

                int env = seekBarEnv.getProgress();
                int cost = seekBar.getProgress();
                int speed = seekBarSpeed.getProgress();

                if(speed >= env && speed >= cost && env <= cost){


                    swappingLinear.removeView(envLinear);
                    swappingLinear.removeView(costLinear);
                    swappingLinear.removeView(timeLinear);



                    speedPrio.setText("1");
                    costPrio.setText("2");
                    envPrio.setText("3");

                    swappingLinear.addView(timeLinear);
                    swappingLinear.addView(costLinear);
                    swappingLinear.addView(envLinear);



                }else if(speed >= env && speed >= cost  && env >= cost) {

                    swappingLinear.removeView(envLinear);
                    swappingLinear.removeView(costLinear);
                    swappingLinear.removeView(timeLinear);



                    speedPrio.setText("1");
                    envPrio.setText("2");
                    costPrio.setText("3");

                    swappingLinear.addView(timeLinear);
                    swappingLinear.addView(envLinear);
                    swappingLinear.addView(costLinear);

                }
                else if(env >= speed && env >= cost && speed <= cost) {

                    swappingLinear.removeView(envLinear);
                    swappingLinear.removeView(costLinear);
                    swappingLinear.removeView(timeLinear);

                    envPrio.setText("1");
                    costPrio.setText("2");
                    speedPrio.setText("3");

                    swappingLinear.addView(envLinear);
                    swappingLinear.addView(costLinear);
                    swappingLinear.addView(timeLinear);



                } else if(env >= speed && env >= cost && speed >= cost) {

                    swappingLinear.removeView(envLinear);
                    swappingLinear.removeView(costLinear);
                    swappingLinear.removeView(timeLinear);

                    envPrio.setText("1");
                    costPrio.setText("3");
                    speedPrio.setText("2");

                    swappingLinear.addView(envLinear);
                    swappingLinear.addView(timeLinear);
                    swappingLinear.addView(costLinear);


                }
                else if(cost >= speed && cost >= env && speed <= env) {

                    swappingLinear.removeView(envLinear);
                    swappingLinear.removeView(costLinear);
                    swappingLinear.removeView(timeLinear);



                    costPrio.setText("1");
                    envPrio.setText("2");
                    speedPrio.setText("3");

                    swappingLinear.addView(costLinear);
                    swappingLinear.addView(envLinear);
                    swappingLinear.addView(timeLinear);






                } else if(cost >= speed && cost >= env && speed >= env) {


                    swappingLinear.removeView(envLinear);
                    swappingLinear.removeView(costLinear);
                    swappingLinear.removeView(timeLinear);

                    costPrio.setText("1");
                    envPrio.setText("3");
                    speedPrio.setText("2");

                    swappingLinear.addView(costLinear);
                    swappingLinear.addView(timeLinear);
                    swappingLinear.addView(envLinear);

                }

            }

        });

        seekBarSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                double valueSpeed = min + (progress * step);


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                int env = seekBarEnv.getProgress();
                int cost = seekBarCost.getProgress();
                int speed = seekBar.getProgress();

                if(speed >= env && speed >= cost && env <= cost){


                    swappingLinear.removeView(envLinear);
                    swappingLinear.removeView(costLinear);
                    swappingLinear.removeView(timeLinear);



                    speedPrio.setText("1");
                    costPrio.setText("2");
                    envPrio.setText("3");

                    swappingLinear.addView(timeLinear);
                    swappingLinear.addView(costLinear);
                    swappingLinear.addView(envLinear);



                }else if(speed >= env && speed >= cost  && env >= cost) {

                    swappingLinear.removeView(envLinear);
                    swappingLinear.removeView(costLinear);
                    swappingLinear.removeView(timeLinear);



                    speedPrio.setText("1");
                    envPrio.setText("2");
                    costPrio.setText("3");

                    swappingLinear.addView(timeLinear);
                    swappingLinear.addView(envLinear);
                    swappingLinear.addView(costLinear);

                }
                else if(env >= speed && env >= cost && speed <= cost) {

                    swappingLinear.removeView(envLinear);
                    swappingLinear.removeView(costLinear);
                    swappingLinear.removeView(timeLinear);

                    envPrio.setText("1");
                    costPrio.setText("2");
                    speedPrio.setText("3");

                    swappingLinear.addView(envLinear);
                    swappingLinear.addView(costLinear);
                    swappingLinear.addView(timeLinear);



                } else if(env >= speed && env >= cost && speed >= cost) {

                    swappingLinear.removeView(envLinear);
                    swappingLinear.removeView(costLinear);
                    swappingLinear.removeView(timeLinear);

                    envPrio.setText("1");
                    costPrio.setText("3");
                    speedPrio.setText("2");

                    swappingLinear.addView(envLinear);
                    swappingLinear.addView(timeLinear);
                    swappingLinear.addView(costLinear);


                }
                else if(cost >= speed && cost >= env && speed <= env) {

                    swappingLinear.removeView(envLinear);
                    swappingLinear.removeView(costLinear);
                    swappingLinear.removeView(timeLinear);



                    costPrio.setText("1");
                    envPrio.setText("2");
                    speedPrio.setText("3");

                    swappingLinear.addView(costLinear);
                    swappingLinear.addView(envLinear);
                    swappingLinear.addView(timeLinear);






                } else if(cost >= speed && cost >= env && speed >= env) {


                    swappingLinear.removeView(envLinear);
                    swappingLinear.removeView(costLinear);
                    swappingLinear.removeView(timeLinear);

                    costPrio.setText("1");
                    envPrio.setText("3");
                    speedPrio.setText("2");

                    swappingLinear.addView(costLinear);
                    swappingLinear.addView(timeLinear);
                    swappingLinear.addView(envLinear);

                }


            }
        });




        mTopSheetBehavior1 = TopSheetBehavior.from(topSheet);
        mTopSheetBehavior1.setPeekHeight(125);
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
                if (mTopSheetBehavior1.getState() == TopSheetBehavior.STATE_COLLAPSED) {

                    mTopSheetBehavior1.setState(TopSheetBehavior.STATE_EXPANDED);

                    ImageView weatehr_icon = findViewById(R.id.weather_icon);

                    String imageUrl = "https://openweathermap.org/img/wn/" + getWeather() + "@2x.png";

                    Glide.with(getApplicationContext()).load(imageUrl).into(weatehr_icon);
                    //startingLocation.setText(null);
                    //destinationLocation.setText(null);

                    backArrow.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            ///When back arrow is clicked resets the old TopSheet State



                            mTopSheetBehavior1.setState(TopSheetBehavior.STATE_COLLAPSED);

                            //toplayout.setVisibility(View.GONE);
                            mTopSheetBehavior1.setTopSheetCallback(new TopSheetBehavior.TopSheetCallback() {
                                @Override
                                public void onStateChanged(@NonNull View topSheet, int newState) {

                                    if (newState == TopSheetBehavior.STATE_EXPANDED) {
                                        toplayout.setVisibility(LinearLayout.GONE);
                                    }

                                    if (newState == TopSheetBehavior.STATE_COLLAPSED) {
                                        toplayout.setVisibility(LinearLayout.VISIBLE);
                                    }

                                    if (newState == TopSheetBehavior.STATE_DRAGGING) {
                                        toplayout.setVisibility(LinearLayout.GONE);
                                    }

                                }

                                @Override
                                public void onSlide(@NonNull View bottomSheet, float slideOffset, @Nullable Boolean isOpening) {

                                }
                            });

                            mTopSheetBehavior1.setPeekHeight(125);
                            mMap.clear();

                            startingLocation.setText(null);
                            destinationLocation.setText(null);

                            trainTime.setText("------");
                            walkTime.setText("------");
                            busTime.setText("------");
                            bicycleTime.setText("------");


                        }

                    });
                }
            }
        });

        //Program the bottom sheet for user preference

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

        logOutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                LogOut();
            }
        });

        mapFragment.getMapAsync(this);

        addressFetcher = new AddressFetcher(this);
        serverFetcher = new ServerFetcher(this);


    }

    // this method calls the server
    private void callServer () {
        System.out.println("here");
        if (serverRequestStartPos != null && serverRequestEndPos != null) {
            System.out.println("inside if statement");
            BusSettings busSettings = new BusSettings(true, 1);
            RailSettings railSettings = new RailSettings(true, 1);
            CarSettings carSettings = new CarSettings(true, 1);
            CycleSettings cycleSettings = new CycleSettings(true, 1);
            WalkSettings walkSettings = new WalkSettings(true, 1);
            ScaleSettings scaleSettings = new ScaleSettings(5, 5, 5);
            UserSettings userSettings = new UserSettings(1, busSettings, railSettings, carSettings, walkSettings, cycleSettings, scaleSettings);
            serverFetcher.sendServerRequest(new RestAPIRequestInformation(1, "mike", String.valueOf(serverRequestStartPos.latitude), String.valueOf(serverRequestStartPos.longitude), String.valueOf(serverRequestEndPos.latitude), String.valueOf(serverRequestEndPos.longitude), userSettings));
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //sets the map as clickable
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng arg0) {
                if (alertDialog != null) {
                    alertDialog.dismiss();
                    mMap.clear();
                } else {
                    System.out.println("nullXXXX");
                }
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

        String location = view.getText().toString();
        List<Address> addressList = null;
        TextView destinationView = findViewById(R.id.search3);


        if (!location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            String locationDublin = view.getText().toString() + "Dublin";


            try {
                addressList = geocoder.getFromLocationName(locationDublin, 5);


                if (addressList == null) throw new Exception("No results for Geocoder");

                Address address = addressList.get(0);

                //latLng is the destination lat/long
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
//            CameraUpdate zoom=CameraUpdateFactory.zoomOut();


                mMap.addMarker(new MarkerOptions().position(latLng).title(address.getAddressLine(0)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
//            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
//            mMap.animateCamera(zoom);


                // Get the startinglocation value and implement zoom on route


                TextView startingLocation2 = findViewById(R.id.search);


                ////// Need to get current location method


                if ((startingLocation2.getText().toString().equals(""))) {

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
                    serverRequestStartPos = loca;
                    serverRequestEndPos = latLng;
                    //callServer();
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

                        mMap.addMarker(new MarkerOptions().position(latLngStart).title(address.getAddressLine(0)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));


                        //System.out.println(routeList);
                        getRoute(latLngStart, latLng);

                        serverRequestStartPos = latLngStart;
                        serverRequestEndPos = latLng;
                        //callServer();

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
        } else {
            zoomToCurrentLocation();


        }
    }

    public void onMapSearchStarting(TextView view) {

        String location = view.getText().toString();
        List<Address> addressList = null;


        if (!location.equals("")) {
            Geocoder geocoder = new Geocoder(this);

            try {
                String location2 = view.getText().toString() + " ireland";

                addressList = geocoder.getFromLocationName(location2, 5);

                if (addressList == null) throw new Exception("No results for Geocoder");

                Address address = addressList.get(0);


                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng).title(address.getAddressLine(0)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            zoomToCurrentLocation();


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
        } catch (SecurityException e) {

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

    public void zoomToCurrentLocation() {


        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude()), 13));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude()))      // Sets the center of the map to location user
                .zoom(17)                   // Sets the zoom
                // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


    }

    //controlling imageview turning on and off for seekbar for user preference
    public void TurnOnOff(View v) throws IOException, XmlPullParserException {

        Resources res = getResources();


        if (isChecked) {

            isChecked = false;

            switch (v.getId()) {


                case R.id.costImage:
                    seekBarCost.setEnabled(isChecked);
                    linearCost.setBackground(Drawable.createFromXml(res, res.getXml(R.xml.border)));
                    valueCost = 1;
                    System.out.println(valueCost);
                    break;

                case R.id.enviormentImage:
                    seekBarEnv.setEnabled(isChecked);
                    linearEnv.setBackground(Drawable.createFromXml(res, res.getXml(R.xml.border)));
                    valueEnv = 1;


                    break;
                case R.id.timeImage:
                    seekBarSpeed.setEnabled(isChecked);
                    linearTime.setBackground(Drawable.createFromXml(res, res.getXml(R.xml.border)));
                    valueSpeed = 1;

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

        if (firstTime) {
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            addMarker(latlng);
        }
        firstTime = false;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @SuppressLint("MissingPermission")
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
        int walking = 0;
        int bus = 0;
        int luas = 0;
        int cycling = 0;
        try {
            scores = new JSONArray(response);
            for (int i = 0; i < scores.length(); i++) {
                JSONObject element = scores.getJSONObject(i);
                System.out.println("Element: "+element);
                String PolyLine = element.getString("overviewPolyline");
                JSONObject destination = element.getJSONObject("destination");
                System.out.println("Destination: "+destination.toString());
                Node destNode = new Node(destination.getString("name"), destination.getInt("stopId"), destination.getInt("transportType"), destination.getDouble("latitude"), destination.getDouble("longitudue"), destination.getDouble("score"));
                System.out.println(destNode.getStopId());
                JSONObject origin = element.getJSONObject("origin");
                Node originNode = new Node(origin.getString("name"), origin.getInt("stopId"), origin.getInt("transportType"), origin.getDouble("latitude"), origin.getDouble("longitudue"), origin.getDouble("score"));
                int routeType = element.getInt("routeType");
                int lengthMinutes = element.getInt("lengthMinutes");
                String routeNumber = element.getString("routeNumber");
                String departureTime = element.getString("departureTime");
                if(routeType==0){
                    walking+=lengthMinutes;
                }
                if(routeType==1){
                    bus+=lengthMinutes;
                }
                if(routeType==2){

                }
                if(routeType==3){

                }
                finalRouteList.add(new FinalRoute(destNode, originNode, PolyLine,routeType,lengthMinutes,routeNumber, departureTime));

                // update ui with route times
                if(i==scores.length()-1){
                    busTime.setText((bus/60)+" Mins");
                    walkTime.setText((walking/60)+" Mins");
                    bicycleTime.setText((cycling/60)+" Mins");
                    trainTime.setText((luas/60)+" Mins");

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Error couldn't parse result");
        }
        boolean firstTime=true;
        for (FinalRoute route : finalRouteList) {

            addTransportMarker(route.getDestination(),route);

            //addTransportMarker(route.getDestination(),route);
            decodePolyline(route.getOverviewPolyline(),route.getRouteType());
            System.out.println("polyline: "+route.getOverviewPolyline());
            System.out.println("origin id: "+route.getOrigin().getStopId());
            System.out.println("destination id: "+route.getDestination().getStopId());
            System.out.println("route number: "+route.getRouteNumber());
        }
    }


    public void addTransportMarker(Node point,FinalRoute route){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(point.getLatitude(),point.getLongitudue()));
        //walking
        if(route.getRouteType()==0) {
            markerOptions.title("Route: " + route.getRouteNumber()+" length: "+(route.getLengthMinutes()/60)+" Mins");
        }
        //bus
        if(route.getRouteType()==1) {
            markerOptions.title(point.getStopId() + " " + " Route: " + route.getRouteNumber()+ " departs: " + route.getDepartureTime()+" length: "+(route.getLengthMinutes()/60)+" Mins");
        }
        //cycling
        if(route.getRouteType()==2) {
            markerOptions.title(point.getStopId() + " " + " Route: " + route.getRouteNumber());
        }
        //train
        if(route.getRouteType()==3) {
            markerOptions.title(point.getStopId() + " " + " Route: " + route.getRouteNumber());
        }
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        if (mMap != null)
            mMap.addMarker(markerOptions);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(point.getLatitude(), point.getLongitudue()))
                .zoom(16)
                .build();
        if (mMap != null)
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    public void decodePolyline(String Polyline,int TransportType) {
        int colour = TransportColour.get(TransportType);
        List<LatLng> decodedPath = PolyUtil.decode(Polyline);

        final int PATTERN_DASH_LENGTH_PX = 20;
        final int PATTERN_GAP_LENGTH_PX = 20;
        final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
        final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
        final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);
        if(TransportType==0) {
            com.google.android.gms.maps.model.Polyline line = mMap.addPolyline(new PolylineOptions()
                    .width(8)
                    .color(colour)
                    .geodesic(true)
                    .addAll(decodedPath)
                    .pattern(PATTERN_POLYGON_ALPHA));
        }
        else{
            com.google.android.gms.maps.model.Polyline line = mMap.addPolyline(new PolylineOptions()
                    .width(8)
                    .color(colour)
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
            JsonObject jsonObject2 = (JsonObject)object;

            JsonArray all_weather = (JsonArray) jsonObject.get("weather");

            //get temperature from api
            JsonObject all_weather2 = (JsonObject) jsonObject2.get("main");


            JsonObject weather = (JsonObject) all_weather.get(0);
            JsonObject weatherTemp = (JsonObject) all_weather2;
            String icon = weather.get("icon").toString();

            //Get String status and temperature
            String weather_str = weather.get("main").toString();
            Double weather_temp = weatherTemp.get("temp").getAsDouble();

            weather_temp = weather_temp- 273.15;

            weather_temp = Math.ceil(weather_temp) ;

            String unique_char =  "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
            icon  =icon.replaceAll(unique_char, "");

            //remove the "" in the api string
            String weather_str_final=weather_str.substring(1,weather_str.length()-1);
            weather_status.setText(weather_str_final);
            weather_temperature.setText(weather_temp.toString() + "°C");


            Log.d("weathericon", icon.toString());

            return icon;

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }


    //////Added indivisual if statement for tomtom api for singular switch mode of transport

    public void getRoute(final LatLng start, final LatLng end){


        final String walktom = "https://api.tomtom.com/routing/1/calculateRoute/" +start.latitude+"%2C"+start.longitude +"%3A"
                +end.latitude + "%2C"+ end.longitude +"/json?routeRepresentation=polyline&avoid=unpavedRoads&travelMode=pedestrian&key=hsG3k8dTKXUpcbecSrGn3Gx4MWrCGAJG";

        final String bustom = "https://api.tomtom.com/routing/1/calculateRoute/" +start.latitude+"%2C"+start.longitude +"%3A"
                +end.latitude + "%2C"+ end.longitude +"/json?routeRepresentation=polyline&avoid=unpavedRoads&travelMode=bus&key=hsG3k8dTKXUpcbecSrGn3Gx4MWrCGAJG";

//        final String cartom = "https://api.tomtom.com/routing/1/calculateRoute/" +start.latitude+"%2C"+start.longitude +"%3A"
//                +end.latitude + "%2C"+ end.longitude +"/json?routeRepresentation=polyline&avoid=unpavedRoads&travelMode=car&key=hsG3k8dTKXUpcbecSrGn3Gx4MWrCGAJG";

        final String bicycletom = "https://api.tomtom.com/routing/1/calculateRoute/" +start.latitude+"%2C"+start.longitude +"%3A"
                +end.latitude + "%2C"+ end.longitude +"/json?routeRepresentation=polyline&avoid=unpavedRoads&travelMode=bicycle&key=hsG3k8dTKXUpcbecSrGn3Gx4MWrCGAJG";

        final String traintom = "https://api.tomtom.com/routing/1/calculateRoute/" +start.latitude+"%2C"+start.longitude +"%3A"
                +end.latitude + "%2C"+ end.longitude +"/json?routeRepresentation=polyline&avoid=unpavedRoads&travelMode=van&key=hsG3k8dTKXUpcbecSrGn3Gx4MWrCGAJG";





        /// walking switch is always on therefore doesn't need to be added around should always be on.

        //boolean walkSwitchChecker = onCheckedChanged(walkSwitch,walkChecked);
        boolean busSwitchChecker = onCheckedChanged(busSwitch,busChecked);
        boolean bicycleChecker = onCheckedChanged(bicycleSwitch,bicycleChecked);
        //boolean carChecker = onCheckedChanged(carSwitch,carChecked);
        boolean trainChecker = onCheckedChanged(trainSwitch,trainChecked);






        //BUS SWITCH ONLY
        if(busSwitchChecker && !bicycleChecker  && !trainChecker){


            ArrayList<LatLng> point_list = new ArrayList<>();
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.color(Color.RED);
            polylineOptions.width(15);


            Log.d("route_api", bustom);

            String response = null;
            HTTPGetRequest routeAPI = new HTTPGetRequest();
            JsonParser parser = new JsonParser();


            try {
                response = routeAPI.execute(bustom).get();
                Log.d("route_api", response);
                if(response != null){
                    Log.d("route_api", bustom);
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
                        busTime.setText(min + " Mins");

                    }else{
                        busTime.setText(hour + " H " + min + " M");
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


        //// BICYCLE SWITCH ONLY

       else if(!busSwitchChecker && bicycleChecker && !trainChecker){


            ArrayList<LatLng> point_list = new ArrayList<>();
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.color(Color.YELLOW);
            polylineOptions.width(15);


            Log.d("route_api", bicycletom);

            String response = null;
            HTTPGetRequest routeAPI = new HTTPGetRequest();
            JsonParser parser = new JsonParser();


            try {
                response = routeAPI.execute(bicycletom).get();
                Log.d("route_api", response);
                if(response != null){
                    Log.d("route_api", walktom);
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
                        bicycleTime.setText(min + " Mins");

                    }else{
                        bicycleTime.setText(hour + " H " + min + " M");
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

        // TRAIN SWITCH ONLY

        else if(!busSwitchChecker && !bicycleChecker && trainChecker){


           // Toast.makeText(getApplicationContext(),"Hello Javatpoint",Toast.LENGTH_SHORT).show();

            ArrayList<LatLng> point_list = new ArrayList<>();
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.color(Color.GREEN);
            polylineOptions.width(15);


            Log.d("route_api", traintom);

            String response = null;
            HTTPGetRequest routeAPI = new HTTPGetRequest();
            JsonParser parser = new JsonParser();


            try {
                response = routeAPI.execute(traintom).get();
                Log.d("route_api", response);
                if(response != null){
                    Log.d("route_api", walktom);
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
                        trainTime.setText(min + " Mins");

                    }else{
                        trainTime.setText(hour + " H " + min + " M");
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
        ////////Default for now will be walking
        else {


            ArrayList<LatLng> point_list = new ArrayList<>();
            final int PATTERN_DASH_LENGTH_PX = 20;
            final int PATTERN_GAP_LENGTH_PX = 20;
            final PatternItem DOT = new Dot();
            final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
            final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
            final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.color(Color.BLUE);
            polylineOptions.width(10);
            polylineOptions.pattern(PATTERN_POLYGON_ALPHA);



            Log.d("route_api", walktom);

            String response = null;
            HTTPGetRequest routeAPI = new HTTPGetRequest();
            JsonParser parser = new JsonParser();


            try {
                response = routeAPI.execute(walktom).get();
                Log.d("route_api", response);
                if(response != null){
                    Log.d("route_api", walktom);
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


           //add popup to the route

//            View dialogView = getLayoutInflater().inflate(R.layout.route_popup,null);
//            AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
//            builder.setView(dialogView);
//
//            TextView time_pop = dialogView.findViewById(R.id.title1);
//            TextView mode_pop = dialogView.findViewById(R.id.description);
//
//            ImageView image_route = dialogView.findViewById(R.id.img);
//
//
//            time_pop.setText(min + " Mins");
//            mode_pop.setText("walking");
//            image_route.setBackgroundResource(R.drawable.iconfinder_ic_directions_walk_48px_352319);
//            alertDialog = builder.create();
//
//
//            alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//
//            WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
//            params.y = 700;
//            alertDialog.getWindow().setAttributes(params);
//            alertDialog.show();


        }








    }

    public boolean onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        Toast.makeText(this, buttonView.toString() + (isChecked ? "on" : "off"),
//                Toast.LENGTH_SHORT).show();
        if(isChecked) {
            return true;
        } else {
            //do stuff when Switch if OFF

        }
        return false;
    }

    public void Popup_window(final LatLng position){

        View dialogView = getLayoutInflater().inflate(R.layout.popup,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setView(dialogView);

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

                startingLocation.setText("");
                destinationLocation.setText(popup_address);
                LatLng cur_loc = new LatLng(mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude());
                getRoute(cur_loc,position);
                alertDialog.dismiss();
            }
        });
        alertDialog = builder.create();


        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
        params.y = 700;
        alertDialog.getWindow().setAttributes(params);
        alertDialog.show();
    }


    private void LogOut(){


        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);


    }



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