package com.group13.dynamicwayfinder.Activities.Map;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.group13.dynamicwayfinder.Utils.RestAPIRequestInformation;
import com.group13.dynamicwayfinder.Utils.ServerWrapperClass;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

public class ServerFetcher {
    private ServerHTTPRequestBuilder serverHTTPRequestBuilder;
    private JSONAddressParser JSONParser;
    private MapActivity mapActivity;

    ServerFetcher(MapActivity mapActivity){
        this.mapActivity=mapActivity;
        serverHTTPRequestBuilder = new ServerHTTPRequestBuilder();
        JSONParser = new JSONAddressParser();
    }

    //gets the address for the current passed in location
    public void sendServerRequest (RestAPIRequestInformation restAPIRequestInformation,boolean BusRequest){
        //create a list and add the current location
        List serverRequestList = new ArrayList();
        serverRequestList.add(restAPIRequestInformation);

        String HTTPRequest = serverHTTPRequestBuilder.URLStringBuilder(serverRequestList,BusRequest);

        ServerWrapperClass serverWrapperClass= new ServerWrapperClass(HTTPRequest,restAPIRequestInformation);

        HTTPServerRequest serverRequest = new HTTPServerRequest(serverWrapperClass,this);

        serverRequest.execute();

    }

    public void HTTPServerResult(String JSONFile){
        //String Address = JSONParser.ParseJSON(JSONFile);
        if(JSONFile!=null) {
            mapActivity.updateRouteOnMap(JSONFile);
        }else{
            Log.d("ErrorParsingAddress","The address was not parsed sucessfully and returned null");
        }
    }
}
