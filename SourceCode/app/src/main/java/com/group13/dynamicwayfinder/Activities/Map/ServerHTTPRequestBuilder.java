package com.group13.dynamicwayfinder.Activities.Map;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.group13.dynamicwayfinder.Utils.HTTPRequestBuilder;

import java.util.List;

import static android.content.ContentValues.TAG;

public class ServerHTTPRequestBuilder  extends HTTPRequestBuilder {

    @Override
    public String URLStringBuilder(List inputList,boolean BusRequest) {
        //Creates a string in the form of http request for google api
        System.out.println("The type of this class is: " + inputList.get(0).getClass().getName());
        System.out.println("Array size is: " + inputList.size());
        System.out.println("Array size is: " + inputList.isEmpty());
        //Checks if the list isint empty and is the correct type and its not greater than one point
        if (inputList.isEmpty() || !inputList.get(0).getClass().getName().contains("RestAPIRequestInformation") || (inputList.size() < 1)) {
            Log.d(TAG, "URLStringBuilder: The list passed to the URLStringBuilder method was not satisfactory ");
            return null;
        }

        StringBuilder serverHTTPRequest = new StringBuilder();

        if (BusRequest){
            //serverHTTPRequest.append("https://group13aseserver.herokuapp.com/");
            serverHTTPRequest.append("http://192.168.0.122:9009?");
        }else{
            serverHTTPRequest.append("http://192.168.0.122:9009/luasroute");
        }



        return serverHTTPRequest.toString();
    }

    @Override
    public String URLStringBuilderDistance(List serverRequestList) {
        StringBuilder serverHTTPRequest = new StringBuilder();
        serverHTTPRequest.append("http://192.168.0.122:9009/closeststopluas");
        return serverHTTPRequest.toString();
    }
}