package com.group13.dynamicwayfinder.Activities.Map;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.group13.dynamicwayfinder.Utils.HTTPRequestBuilder;

import java.util.Iterator;
import java.util.List;

import static android.content.ContentValues.TAG;

public class LocationHTTPRequestBuilder extends HTTPRequestBuilder {

    @Override
    public String URLStringBuilder(List inputList) {
        //Creates a string in the form of http request for google api
        System.out.println("The type of this class is: "+inputList.get(0).getClass().getName());
        System.out.println("Array size is: "+inputList.size());
        System.out.println("Array size is: "+inputList.isEmpty());
        //Checks if the list isint empty and is the correct type and its not greater than one point
        if(inputList.isEmpty()||!inputList.get(0).getClass().getName().contains("LatLng")||(inputList.size()>1)){
            Log.d(TAG, "URLStringBuilder: The list passed to the URLStringBuilder method was not satisfactory ");
            return null;
        }

        StringBuilder LocationHTTPRequest = new StringBuilder();

        LocationHTTPRequest.append("https://maps.googleapis.com/maps/api/geocode/json?latlng=");

        LatLng location = (LatLng) inputList.get(0);

        LocationHTTPRequest.append(location.latitude+",");
        LocationHTTPRequest.append(location.longitude+"&");

        LocationHTTPRequest.append("key=AIzaSyCqCdlPmegML3DEtc7BL9X1RFVsm8lEBbE");

        return LocationHTTPRequest.toString();
    }
}
