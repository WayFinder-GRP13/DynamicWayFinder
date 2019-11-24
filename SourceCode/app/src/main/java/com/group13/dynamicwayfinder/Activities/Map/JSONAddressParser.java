package com.group13.dynamicwayfinder.Activities.Map;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONAddressParser {

    public String ParseJSON(String JSONFile) {
        String address = null;

        try {
                JSONObject obj = new JSONObject(JSONFile);
            System.out.println("JSONFile:"+obj.toString());
                JSONArray geodataArray = obj.getJSONArray("results");
                for (int i = 0;i<geodataArray.length();i++){
                    if(geodataArray.getJSONObject(i).has("formatted_address")){
                        address = geodataArray.getJSONObject(i).getString("formatted_address");
                        System.out.println("Address is:"+address);
                        break;
                    }
                }
        } catch (
                JSONException e) {
            e.printStackTrace();
        }

        return address;
    }
}

