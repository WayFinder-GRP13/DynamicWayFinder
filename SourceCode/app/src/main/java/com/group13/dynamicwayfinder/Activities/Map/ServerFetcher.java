package com.group13.dynamicwayfinder.Activities.Map;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.group13.dynamicwayfinder.Utils.LocationPoint;
import com.group13.dynamicwayfinder.Utils.RestAPIRequestInformation;
import com.group13.dynamicwayfinder.Utils.ServerClasses.Node;
import com.group13.dynamicwayfinder.Utils.ServerWrapperClass;
import com.group13.dynamicwayfinder.Utils.ServerWrapperDistanceClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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



    public void HTTPServerDistanceResult(String JSONFile){
        //set the distance points
        System.out.println("Result is : "+JSONFile);
        System.out.println("RESULT FROM SERVER:        "+JSONFile);
        mapActivity.ParseDistanceResult(JSONFile);

    }

    public void HTTPServerResult(String JSONFile){
        //String Address = JSONParser.ParseJSON(JSONFile);
        System.out.println("RESULT FROM SERVER:        "+JSONFile);

        if(JSONFile!=null) {
            mapActivity.updateRouteOnMap(JSONFile);
        }else{
            Log.d("ErrorParsingAddress","The address was not parsed sucessfully and returned null");
        }
    }


    public void sendDistanceRequest(double midPointLatitude, double midPointLogitude) {
        //create a list and add the current location
        List serverRequestList = new ArrayList();
        serverRequestList.add(new LocationPoint(midPointLatitude,midPointLogitude));

        String HTTPRequest = serverHTTPRequestBuilder.URLStringBuilderDistance(serverRequestList);

        ServerWrapperDistanceClass serverWrapperDistanceClass= new ServerWrapperDistanceClass(HTTPRequest,new LocationPoint(midPointLatitude,midPointLogitude));

        HTTPServerRequest serverRequest = new HTTPServerRequest(serverWrapperDistanceClass,this);

        serverRequest.execute();

    }
}
