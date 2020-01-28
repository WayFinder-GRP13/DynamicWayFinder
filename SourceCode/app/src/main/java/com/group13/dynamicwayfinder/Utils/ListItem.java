package com.group13.dynamicwayfinder.Utils;

import android.app.Activity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListItem {

    public static final String URL_DATA = "https://api.jcdecaux.com/vls/v1/stations?contract=dublin&apiKey=215780862e2545500c919b4f5e8e2419ddc36b6c";


    private String head;               //assign the variable
    private String desc;
    private String status;
    private String lat;
    private String lng;
    private String bike_stands;
    private String available_bikes;

    private ArrayList<ListItem> arrayList = new ArrayList<>();    //assign arraylist containing our list items.
    private static ListItem instance; //create a variable for listItelm instance


//    get the instance iof listitem and set the condition if its nothing return

    public static ListItem getInstance() {
        if (instance == null)
            return instance = new ListItem();
        else
            return instance;
    }

    public ArrayList<ListItem> getArrayList() {
        return arrayList;
    }

    //    constructor for  Empty listItem
    public ListItem() {
    }

    public ListItem(String head, String desc, String status, String lat, String lng, String bike_stands, String available_bikes) {
        this.head = head;
        this.desc = desc;
        this.status = status;
        this.lat = lat;
        this.lng = lng;
        this.bike_stands = bike_stands;
        this.available_bikes = available_bikes;

    }

    public String getHead() {
        return head;
    }

    public String getDesc() {
        return desc;
    }

    public String getStatus() {
        return status;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }


    public String getBike_stands() {
        return bike_stands;
    }

    public String getAvailable_bikes() {
        return available_bikes;
    }

    public void loadRecyclerViewData(Activity context, final ListItemListener listener) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_DATA,

                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
//                        Log.d(TAG," TEST onResponse: "+s);
                        try {
                            JSONArray jsonArray = new JSONArray(s);


                            for (int i = 0; i < jsonArray.length(); i++) {

//                              Parsing json data array
                                JSONObject jsonPart = jsonArray.getJSONObject(i);

//                                Parsing json object
                                JSONObject jsonObject2 = jsonPart.getJSONObject("position");

                                //these 2 object to get the lat and lng for the next activity to get markers
                                jsonObject2.getString("lat");
                                jsonObject2.getString("lng");

//                                Adding all data is a list
                                arrayList.add(new ListItem(

                                        "" + jsonPart.getString("name"),
                                        "" + jsonPart.getString("bike_stands"),
                                        "" + jsonPart.getString("status"),
                                        "" + jsonObject2.getString("lat"),
                                        "" + jsonObject2.getString("lng"),
                                        "" + jsonPart.getString("available_bike_stands"),
                                        "" + jsonPart.getString("available_bikes")

                                ));


                                if (listener != null) {

                                    listener.onComplete();
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },

                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        //Volley request queue object
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        //queuing the request by passing the parameter string request
        requestQueue.add(stringRequest);
    }

    //put the interface in the listener and calling this method in the main activity as main activity start
    //data start fetcing.
    interface ListItemListener {
        void onComplete();
    }
}