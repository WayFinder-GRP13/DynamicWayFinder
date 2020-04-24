package com.group13.dynamicwayfinder.Activities.Map;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

public class AddressFetcher {

    private LocationHTTPRequestBuilder locationHTTPRequestBuilder;
    private LatLng location;
    private BufferedReader reader;
    private String JSONFile;
    private JSONAddressParser JSONParser;
    private MapActivity mapActivity;

    AddressFetcher(MapActivity mapActivity){
        this.mapActivity=mapActivity;
        locationHTTPRequestBuilder = new LocationHTTPRequestBuilder();
        JSONParser = new JSONAddressParser();
    }

    //gets the address for the current passed in location
    public void sendlocationsAddressRequest (LatLng CurrentLocation){
        //create a list and add the current location
        List locationList = new ArrayList();
        locationList.add(CurrentLocation);

        String HTTPRequest = locationHTTPRequestBuilder.URLStringBuilder(locationList,true);

        HTTPLocationRequest locationRequest = new HTTPLocationRequest(this);

        locationRequest.execute(HTTPRequest);

    }

    public void HTTPLocationResult(String JSONFile){
        String Address = JSONParser.ParseJSON(JSONFile);
        if(Address!=null) {
            mapActivity.updateAddressOnMap(Address);
        }else{
            Log.d("ErrorParsingAddress","The address was not parsed sucessfully and returned null");
        }
    }





}




