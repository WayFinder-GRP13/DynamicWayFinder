package com.group13.dynamicwayfinder.Activities.Map;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.group13.dynamicwayfinder.Utils.HTTPGetRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LuasRouteCalculate {

    JsonArray redLuas;
    JsonArray greenLuas;
    JsonArray redTal = new JsonArray();
    JsonArray redSag = new JsonArray();
    ;
    JsonArray greenUpper = new JsonArray();
    JsonArray greenLower = new JsonArray();
    ;
    JsonArray luas_all = new JsonArray();
    final int PATTERN_DASH_LENGTH_PX = 20;
    final int PATTERN_GAP_LENGTH_PX = 20;
    final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);


    int sizeOfRed, red_inter, red_Bel;
    int greenup, greendown;
    JsonObject inter_red, red_belStop;
    JsonObject inter_greenup, inter_greendown;

    GoogleMap mMap;

    Context context;

    public LuasRouteCalculate(GoogleMap map, Context context) {

        this.mMap = map;
        this.context = context;
    }


    public void getLuasStops() {

        HTTPGetRequest getLuas = new HTTPGetRequest();
        String response = null;
        JsonParser parser = new JsonParser();
        int branch_red = -1;

        try {
            response = getLuas.execute("https://group13aseserver.herokuapp.com/luas").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Object object = parser.parse(response);
        JsonArray allArray = (JsonArray) object;

        JsonObject red_all = (JsonObject) allArray.get(0);
        JsonObject green_all = (JsonObject) allArray.get(1);

        redLuas = (JsonArray) red_all.getAsJsonArray("Stops");
        greenLuas = (JsonArray) green_all.getAsJsonArray("Stops");

        sizeOfRed = redLuas.size();

        for (int i = 0; i < redLuas.size(); i++) {
            JsonObject in = (JsonObject) redLuas.get(i);

            if (in.get("Pronunciation").getAsString().equals("Cookstown")) {
                branch_red = i;
                break;
            }

            if (in.get("Pronunciation").getAsString().equals("Abbey Street")) {
                red_inter = i;
                inter_red = in;
            }

            if (in.get("Pronunciation").getAsString().equals("Belgard")) {
                red_Bel = i;
                red_belStop = in;
            }

            redTal.add(in);
            redSag.add(in);
            luas_all.add(in);
        }

        for (int i = branch_red; i < redLuas.size(); i++) {
            JsonObject in = (JsonObject) redLuas.get(i);

            redTal.add(in);
            luas_all.add(in);

            if (in.get("Pronunciation").getAsString().equals("Tallaght")) {
                branch_red = i;
                break;
            }
        }

        for (int i = branch_red + 1; i < redLuas.size(); i++) {
            JsonObject in = (JsonObject) redLuas.get(i);
            redSag.add(in);
            luas_all.add(in);
        }

        for (int i = 0; i < greenLuas.size(); i++) {
            JsonObject in = (JsonObject) greenLuas.get(i);
            String name = in.get("Pronunciation").getAsString();

            switch (name) {
                case "O'Connell - Upper":
                    greenUpper.add(in);
                    break;
                case "O'Connell - GPO":
                    greenUpper.add(in);
                    break;
                case "Westmoreland":
                    greenUpper.add(in);
                    break;
                case "Parnell":
                    greenLower.add(in);
                    break;
                case "Marlborough":
                    greenLower.add(in);
                    break;
                case "Trinity":
                    greenLower.add(in);
                    break;

                default:
                    greenUpper.add(in);
                    greenLower.add(in);
                    break;

            }
            luas_all.add(in);
        }

        for (int i = 0; i < greenUpper.size(); i++) {
            JsonObject in = (JsonObject) greenUpper.get(i);
            String name = in.get("Pronunciation").getAsString();
            if (name.equals("O'Connell - GPO")) {
                greenup = i;
                inter_greenup = in;
            }
        }

        for (int i = 0; i < greenLower.size(); i++) {
            JsonObject in = (JsonObject) greenLower.get(i);
            String name = in.get("Pronunciation").getAsString();
            if (name.equals("Marlborough")) {
                greendown = i;
                inter_greendown = in;
            }
        }
    }


    public void getLuasDirection(LatLng start, LatLng end) {

        Log.d("luas", "시작");

        JsonObject nearest_start = new JsonObject();
        JsonObject nearest_end = new JsonObject();
        int start_index = -1;
        int end_index = -1;

        ArrayList<LatLng> red_route = new ArrayList<>();
        ArrayList<LatLng> green_route = new ArrayList<>();

        double distance_start = CalculateDistance(start, end);
        double distance_end = distance_start;

        for (int i = 0; i < luas_all.size(); i++) {

            JsonObject luas = (JsonObject) luas_all.get(i);
            LatLng cur_luas = new LatLng(luas.get("Latitude").getAsDouble(), luas.get("Longitude").getAsDouble());

            double distance1 = CalculateDistance(start, cur_luas);
            double distance2 = CalculateDistance(end, cur_luas);

            if (distance_start > distance1) {
                nearest_start = luas;
                start_index = i;
                distance_start = distance1;
            }

            if (distance_end > distance2) {
                nearest_end = luas;
                end_index = i;
                distance_end = distance2;
            }
        }

        //Red Line - Red Line
        if (start_index < sizeOfRed && end_index < sizeOfRed) {

            boolean flag_start = false;
            boolean flag_end = false;

            if (start_index > red_Bel || end_index > red_Bel) {
                if (start_index < end_index) {
                    if (start_index > red_Bel) {
                        for (int i = red_Bel; i < redTal.size(); i++) {

                            if (redTal.get(i).equals(nearest_start)) {
                                start_index = i;
                                flag_start = true;
                            }

                            if (redTal.get(i).equals(nearest_end)) {
                                end_index = i;
                                flag_end = true;
                            }
                        }

                        if (flag_start == true && flag_end == true) {
                            for (int i = start_index; i <= end_index; i++) {
                                JsonObject aaa = (JsonObject) redTal.get(i);
                                red_route.add(new LatLng(aaa.get("Latitude").getAsDouble(), aaa.get("Longitude").getAsDouble()));
                            }
                        }

                        for (int i = red_Bel; i < redSag.size(); i++) {
                            if (redSag.get(i).equals(nearest_start)) {
                                start_index = i;
                                flag_start = true;
                            }

                            if (redSag.get(i).equals(nearest_end)) {
                                end_index = i;
                                flag_end = true;
                            }
                        }

                        if (flag_start == true && flag_end == true) {
                            for (int i = start_index; i <= end_index; i++) {
                                JsonObject aaa = (JsonObject) redSag.get(i);
                                red_route.add(new LatLng(aaa.get("Latitude").getAsDouble(), aaa.get("Longitude").getAsDouble()));
                            }
                        }
                    }

                    if (end_index > red_Bel) {

                        for (int i = red_Bel; i < redTal.size(); i++) {
                            if (redTal.get(i).equals(nearest_end)) {
                                end_index = i;
                                flag_end = true;
                            }
                        }

                        if (flag_end == true) {
                            for (int i = start_index; i <= end_index; i++) {
                                JsonObject aaa = (JsonObject) redTal.get(i);
                                red_route.add(new LatLng(aaa.get("Latitude").getAsDouble(), aaa.get("Longitude").getAsDouble()));
                                //Log.d("luas","redTal, END만 기준점보다 큰 경우  " + aaa );
                                flag_end = false;
                            }
                        }

                        for (int i = red_Bel; i < redSag.size(); i++) {
                            if (redSag.get(i).equals(nearest_end)) {
                                end_index = i;
                                flag_end = true;
                            }
                        }

                        if (flag_end == true) {
                            for (int i = start_index; i <= end_index; i++) {
                                JsonObject aaa = (JsonObject) redSag.get(i);
                                red_route.add(new LatLng(aaa.get("Latitude").getAsDouble(), aaa.get("Longitude").getAsDouble()));
                                //Log.d("luas","redSAG, END만 기준점보다 큰 경우  " + aaa );
                            }
                        }
                    }
                } else {
                    //saggat -> the point or tallght -> the point
                    flag_start = false;
                    for (int i = 0; i < redTal.size(); i++) {
                        if (redTal.get(i).equals(nearest_start)) {
                            flag_start = true;
                            start_index = i;
                        }
                    }

                    if (flag_start == true) {
                        flag_start = false;
                        for (int i = end_index; i <= start_index; i++) {
                            JsonObject aaa = (JsonObject) redTal.get(i);
                            red_route.add(new LatLng(aaa.get("Latitude").getAsDouble(), aaa.get("Longitude").getAsDouble()));
                        }
                    }

                    for (int i = 0; i < redSag.size(); i++) {
                        if (redSag.get(i).equals(nearest_start)) {
                            flag_start = true;
                            start_index = i;
                        }
                    }

                    if (flag_start == true) {
                        for (int i = end_index; i <= start_index; i++) {
                            JsonObject aaa = (JsonObject) redSag.get(i);
                            red_route.add(new LatLng(aaa.get("Latitude").getAsDouble(), aaa.get("Longitude").getAsDouble()));
                        }
                    }
                }
            } else {

                for (int i = 0; i < redTal.size(); i++) {
                    if (redTal.get(i).equals(nearest_start))
                        start_index = i;

                    if (redTal.get(i).equals(nearest_end))
                        end_index = i;

                }

                if (start_index > end_index) {
                    int temp = start_index;
                    start_index = end_index;
                    end_index = temp;
                }

                for (int i = start_index; i <= end_index; i++) {
                    JsonObject stop = (JsonObject) redTal.get(i);
                    red_route.add(new LatLng(stop.get("Latitude").getAsDouble(), stop.get("Longitude").getAsDouble()));
                }
            }

            LatLng start_stop = new LatLng(nearest_start.get("Latitude").getAsDouble(), nearest_start.get("Longitude").getAsDouble());
            LatLng end_stop = new LatLng(nearest_end.get("Latitude").getAsDouble(), nearest_end.get("Longitude").getAsDouble());

            mMap.addPolyline(new PolylineOptions().width(20).color(Color.RED).addAll(red_route));
            mMap.addMarker(new MarkerOptions().position(start_stop).title(nearest_start.get("Pronunciation").getAsString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            mMap.addMarker(new MarkerOptions().position(end_stop).title(nearest_end.get("Pronunciation").getAsString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            getWalkiingDirection(start, start_stop);
            getWalkiingDirection(end, end_stop);

        }

        //Green Line - Green Line
        if (start_index >= sizeOfRed && end_index >= sizeOfRed) {
            start_index -= sizeOfRed;
            end_index -= sizeOfRed;

            boolean flag_start = false;
            boolean flag_end = false;

            Log.d("luas", nearest_start + " " + nearest_end);

            if (start_index > end_index) {
                Log.d("luas", "upper Line");

                for (int i = 0; i < greenUpper.size(); i++) {
                    if (greenUpper.get(i).equals(nearest_start)) {
                        start_index = i;
                        flag_start = true;
                    }
                    if (greenUpper.get(i).equals(nearest_end)) {
                        end_index = i;
                        flag_end = true;
                    }
                }

                if (flag_start == false || flag_end == false) {
                    distance_start = CalculateDistance(start, end);
                    distance_end = distance_start;

                    for (int i = 0; i < greenUpper.size(); i++) {
                        JsonObject stop = (JsonObject) greenUpper.get(i);
                        LatLng cur = new LatLng(stop.get("Latitude").getAsDouble(), stop.get("Longitude").getAsDouble());
                        double distance1 = CalculateDistance(start, cur);
                        double distance2 = CalculateDistance(end, cur);

                        if (distance_start > distance1) {
                            nearest_start = stop;
                            start_index = i;
                            distance_start = distance1;
                        }

                        if (distance_end > distance2) {
                            nearest_end = stop;
                            end_index = i;
                            distance_end = distance2;

                        }
                    }
                }

                if (start_index > end_index) {
                    int temp = start_index;
                    start_index = end_index;
                    end_index = temp;
                }

                for (int i = start_index; i <= end_index; i++) {
                    JsonObject aaa = (JsonObject) greenUpper.get(i);
                    green_route.add(new LatLng(aaa.get("Latitude").getAsDouble(), aaa.get("Longitude").getAsDouble()));
                }
            } else {
                Log.d("luas", "Lower Line");
                flag_start = false;
                flag_end = false;

                for (int i = 0; i < greenLower.size(); i++) {
                    if (greenLower.get(i).equals(nearest_start)) {
                        start_index = i;
                        flag_start = true;
                    }
                    if (greenLower.get(i).equals(nearest_end)) {
                        end_index = i;
                        flag_end = true;
                    }
                }

                Log.d("luas", "flag start " + flag_start + " flag end " + flag_end);

                if (flag_start == false || flag_end == false) {
                    distance_start = CalculateDistance(start, end);
                    distance_end = distance_start;

                    for (int i = 0; i < greenLower.size(); i++) {
                        JsonObject stop = (JsonObject) greenLower.get(i);
                        LatLng cur = new LatLng(stop.get("Latitude").getAsDouble(), stop.get("Longitude").getAsDouble());
                        double distance1 = CalculateDistance(start, cur);
                        double distance2 = CalculateDistance(end, cur);

                        if (distance_start > distance1) {
                            nearest_start = stop;
                            start_index = i;
                            distance_start = distance1;
                        }

                        if (distance_end > distance2) {
                            nearest_end = stop;
                            end_index = i;
                            distance_end = distance2;

                        }
                    }
                }

                if (start_index > end_index) {
                    int temp = start_index;
                    start_index = end_index;
                    end_index = temp;
                }

                for (int i = start_index; i <= end_index; i++) {
                    JsonObject aaa = (JsonObject) greenLower.get(i);
                    green_route.add(new LatLng(aaa.get("Latitude").getAsDouble(), aaa.get("Longitude").getAsDouble()));
                }
            }

            LatLng start_stop = new LatLng(nearest_start.get("Latitude").getAsDouble(), nearest_start.get("Longitude").getAsDouble());
            LatLng end_stop = new LatLng(nearest_end.get("Latitude").getAsDouble(), nearest_end.get("Longitude").getAsDouble());

            mMap.addMarker(new MarkerOptions().position(start_stop).title(nearest_start.get("Pronunciation").getAsString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
            mMap.addMarker(new MarkerOptions().position(end_stop).title(nearest_end.get("Pronunciation").getAsString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
            mMap.addPolyline(new PolylineOptions().width(20).color(Color.GREEN).addAll(green_route));
            getWalkiingDirection(start, start_stop);
            getWalkiingDirection(end, end_stop);
        }

        //RedLine - GreenLine
        if (start_index < sizeOfRed && end_index >= sizeOfRed) {
            boolean flag_tal = false;
            boolean flag_sag = false;

            boolean flag_vert;

            Log.d("luas", nearest_start + " " + nearest_end);

            if (nearest_start.get("Latitude").getAsDouble() > nearest_end.get("Latitude").getAsDouble()) {
                flag_vert = true;
            } else {
                flag_vert = false;
            }
            Log.d("luas", "flag vert : " + flag_vert);


            //Red - Green Lower
            if (flag_vert == true) {
                Log.d("luas", "flag " + flag_vert + " Luas Lower");
                if (start_index > red_inter) {
                    if (start_index > red_Bel) {
                        for (int i = 0; i < redTal.size(); i++) {
                            if (redTal.get(i).equals(nearest_start)) {
                                flag_tal = true;
                                start_index = i;
                            }
                        }

                        for (int i = 0; i < redSag.size(); i++) {
                            if (redSag.get(i).equals(nearest_start)) {
                                flag_sag = true;
                                start_index = i;
                            }
                        }

                        if (flag_tal == true) {
                            for (int i = red_inter; i <= start_index; i++) {
                                JsonObject aaa = (JsonObject) redTal.get(i);
                                red_route.add(new LatLng(aaa.get("Latitude").getAsDouble(), aaa.get("Longitude").getAsDouble()));
                            }
                        }

                        if (flag_sag == true) {
                            for (int i = red_inter; i <= start_index; i++) {
                                JsonObject aaa = (JsonObject) redSag.get(i);
                                red_route.add(new LatLng(aaa.get("Latitude").getAsDouble(), aaa.get("Longitude").getAsDouble()));
                            }
                        }
                    } else {

                        for (int i = 0; i < redTal.size(); i++) {
                            if (redTal.get(i).equals(nearest_start)) {
                                start_index = i;
                            }
                        }

                        for (int i = red_inter; i <= start_index; i++) {
                            JsonObject aaa = (JsonObject) redTal.get(i);
                            red_route.add(new LatLng(aaa.get("Latitude").getAsDouble(), aaa.get("Longitude").getAsDouble()));
                        }

                    }
                } else {
                    for (int i = 0; i < redTal.size(); i++) {
                        if (redTal.get(i).equals(nearest_start)) {
                            start_index = i;
                        }
                    }

                    for (int i = start_index; i <= red_inter; i++) {
                        JsonObject aaa = (JsonObject) redTal.get(i);
                        red_route.add(new LatLng(aaa.get("Latitude").getAsDouble(), aaa.get("Longitude").getAsDouble()));
                    }

                }

                boolean flag_end = false;
                for (int i = 0; i < greenLower.size(); i++) {
                    if (greenLower.get(i).equals(nearest_end)) {
                        end_index = i;
                        flag_end = true;
                    }
                }

                if (flag_end == false) {
                    distance_end = CalculateDistance(start, end);

                    for (int i = 0; i < greenLower.size(); i++) {
                        JsonObject stop = (JsonObject) greenLower.get(i);
                        LatLng cur = new LatLng(stop.get("Latitude").getAsDouble(), stop.get("Longitude").getAsDouble());

                        double distance2 = CalculateDistance(end, cur);

                        if (distance_end > distance2) {
                            nearest_end = stop;
                            end_index = i;
                            distance_end = distance2;
                        }
                    }
                }

                for (int i = greendown; i <= end_index; i++) {
                    JsonObject stop = (JsonObject) greenLower.get(i);
                    green_route.add(new LatLng(stop.get("Latitude").getAsDouble(), stop.get("Longitude").getAsDouble()));

                }

                LatLng start_stop = new LatLng(nearest_start.get("Latitude").getAsDouble(), nearest_start.get("Longitude").getAsDouble());
                LatLng end_stop = new LatLng(nearest_end.get("Latitude").getAsDouble(), nearest_end.get("Longitude").getAsDouble());
                LatLng inter_redStop = new LatLng(inter_red.get("Latitude").getAsDouble(), inter_red.get("Longitude").getAsDouble());
                LatLng inter_green = new LatLng(inter_greendown.get("Latitude").getAsDouble(), inter_greendown.get("Longitude").getAsDouble());

                mMap.addMarker(new MarkerOptions().position(start_stop).title(nearest_start.get("Pronunciation").getAsString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                mMap.addMarker(new MarkerOptions().position(inter_redStop).title(inter_red.get("Pronunciation").getAsString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                mMap.addMarker(new MarkerOptions().position(inter_green).title(inter_greendown.get("Pronunciation").getAsString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                mMap.addMarker(new MarkerOptions().position(end_stop).title(nearest_end.get("Pronunciation").getAsString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));


                getWalkiingDirection(start, start_stop);
                getWalkiingDirection(inter_redStop, inter_green);
                getWalkiingDirection(end, end_stop);

            } else {
                Log.d("luas", "flag " + flag_vert + " Luas Upper");
                flag_tal = false;
                flag_sag = false;
                if (start_index > red_inter) {
                    if (start_index > red_Bel) {
                        for (int i = 0; i < redTal.size(); i++) {
                            if (redTal.get(i).equals(nearest_start)) {
                                flag_tal = true;
                                start_index = i;
                            }
                        }

                        for (int i = 0; i < redSag.size(); i++) {
                            if (redSag.get(i).equals(nearest_start)) {
                                flag_sag = true;
                                start_index = i;
                            }
                        }

                        if (flag_tal == true) {
                            for (int i = red_inter; i <= start_index; i++) {
                                JsonObject aaa = (JsonObject) redTal.get(i);
                                red_route.add(new LatLng(aaa.get("Latitude").getAsDouble(), aaa.get("Longitude").getAsDouble()));
                            }
                        }

                        if (flag_sag == true) {
                            for (int i = red_inter; i <= start_index; i++) {
                                JsonObject aaa = (JsonObject) redSag.get(i);
                                red_route.add(new LatLng(aaa.get("Latitude").getAsDouble(), aaa.get("Longitude").getAsDouble()));
                            }
                        }
                    } else {

                        for (int i = 0; i < redTal.size(); i++) {
                            if (redTal.get(i).equals(nearest_start)) {
                                start_index = i;
                            }
                        }

                        for (int i = red_inter; i <= start_index; i++) {
                            JsonObject aaa = (JsonObject) redTal.get(i);
                            red_route.add(new LatLng(aaa.get("Latitude").getAsDouble(), aaa.get("Longitude").getAsDouble()));
                        }

                    }
                } else {
                    for (int i = 0; i < redTal.size(); i++) {
                        if (redTal.get(i).equals(nearest_start)) {
                            start_index = i;
                        }
                    }

                    for (int i = start_index; i <= red_inter; i++) {
                        JsonObject aaa = (JsonObject) redTal.get(i);
                        red_route.add(new LatLng(aaa.get("Latitude").getAsDouble(), aaa.get("Longitude").getAsDouble()));
                    }
                }

                boolean flag_end = false;

                Log.d("luas", "RED-GREEN UPPER LINE에서 GREEN 파트");
                for (int i = 0; i < greenUpper.size(); i++) {
                    if (greenUpper.get(i).equals(nearest_end)) {
                        end_index = i;
                        flag_end = true;
                    }
                }

                if (flag_end == false) {
                    distance_end = CalculateDistance(start, end);
                    for (int i = 0; i < greenUpper.size(); i++) {
                        JsonObject stop = (JsonObject) greenUpper.get(i);
                        LatLng cur = new LatLng(stop.get("Latitude").getAsDouble(), stop.get("Longitude").getAsDouble());
                        double distance2 = CalculateDistance(end, cur);
                        if (distance_end > distance2) {
                            nearest_end = stop;
                            end_index = i;
                            distance_end = distance2;
                        }
                    }
                }

                for (int i = end_index; i <= greenup; i++) {
                    JsonObject stop = (JsonObject) greenUpper.get(i);
                    Log.d("luas", stop.toString());
                    green_route.add(new LatLng(stop.get("Latitude").getAsDouble(), stop.get("Longitude").getAsDouble()));
                }

                LatLng start_stop = new LatLng(nearest_start.get("Latitude").getAsDouble(), nearest_start.get("Longitude").getAsDouble());
                LatLng end_stop = new LatLng(nearest_end.get("Latitude").getAsDouble(), nearest_end.get("Longitude").getAsDouble());
                LatLng inter_redStop = new LatLng(inter_red.get("Latitude").getAsDouble(), inter_red.get("Longitude").getAsDouble());
                LatLng inter_green = new LatLng(inter_greenup.get("Latitude").getAsDouble(), inter_greenup.get("Longitude").getAsDouble());

                mMap.addMarker(new MarkerOptions().position(start_stop).title(nearest_start.get("Pronunciation").getAsString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                mMap.addMarker(new MarkerOptions().position(inter_redStop).title(inter_red.get("Pronunciation").getAsString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                mMap.addMarker(new MarkerOptions().position(inter_green).title(inter_greenup.get("Pronunciation").getAsString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                mMap.addMarker(new MarkerOptions().position(end_stop).title(nearest_end.get("Pronunciation").getAsString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));

                getWalkiingDirection(start, start_stop);
                getWalkiingDirection(inter_redStop, inter_green);
                getWalkiingDirection(end, end_stop);
            }
            mMap.addPolyline(new PolylineOptions().width(20).color(Color.RED).addAll(red_route));
            mMap.addPolyline(new PolylineOptions().width(20).color(Color.GREEN).addAll(green_route));
        }

        //GreenLine - RedLine
        if (start_index >= sizeOfRed && end_index < sizeOfRed) {

            Log.d("luas", nearest_start + " " + nearest_end);

            boolean flag_vert = (nearest_start.get("Latitude").getAsDouble() > nearest_end.get("Latitude").getAsDouble()) ? true : false;
            //Green-Line

            if (flag_vert == true) {
                //Green Lower
                boolean flag_start = false;
                for (int i = 0; i < greenLower.size(); i++) {
                    if (greenLower.get(i).equals(nearest_start)) {
                        start_index = i;
                        flag_start = true;
                    }
                }

                if (flag_start == false) {
                    distance_start = CalculateDistance(start, end);

                    for (int i = 0; i < greenLower.size(); i++) {
                        JsonObject stop = (JsonObject) greenLower.get(i);
                        LatLng cur = new LatLng(stop.get("Latitude").getAsDouble(), stop.get("Longitude").getAsDouble());

                        double distance2 = CalculateDistance(start, cur);

                        if (distance_start > distance2) {
                            nearest_start = stop;
                            start_index = i;
                            distance_start = distance2;
                        }
                    }
                }

                for (int i = start_index; i <= greendown; i++) {
                    JsonObject stop = (JsonObject) greenLower.get(i);
                    green_route.add(new LatLng(stop.get("Latitude").getAsDouble(), stop.get("Longitude").getAsDouble()));
                }

                boolean flag_end = false;
                if (end_index > red_Bel) {
                    for (int i = 0; i < redTal.size(); i++) {
                        if (redTal.get(i).equals(nearest_end)) {
                            flag_end = true;
                            end_index = i;
                        }
                    }

                    if (flag_end == true) {
                        for (int i = red_inter; i <= end_index; i++) {
                            JsonObject stop = (JsonObject) redTal.get(i);
                            red_route.add(new LatLng(stop.get("Latitude").getAsDouble(), stop.get("Longitude").getAsDouble()));
                        }
                    } else {
                        for (int i = red_inter; i <= end_index; i++) {
                            JsonObject stop = (JsonObject) redSag.get(i);
                            red_route.add(new LatLng(stop.get("Latitude").getAsDouble(), stop.get("Longitude").getAsDouble()));
                        }
                    }
                }

                if (end_index > red_inter && end_index < red_Bel) {

                    for (int i = red_inter; i <= end_index; i++) {
                        JsonObject stop = (JsonObject) redSag.get(i);
                        red_route.add(new LatLng(stop.get("Latitude").getAsDouble(), stop.get("Longitude").getAsDouble()));
                    }
                }

                if (end_index < red_inter) {
                    for (int i = end_index; i <= red_inter; i++) {
                        JsonObject stop = (JsonObject) redTal.get(i);
                        red_route.add(new LatLng(stop.get("Latitude").getAsDouble(), stop.get("Longitude").getAsDouble()));
                    }
                }

                LatLng start_stop = new LatLng(nearest_start.get("Latitude").getAsDouble(), nearest_start.get("Longitude").getAsDouble());
                LatLng end_stop = new LatLng(nearest_end.get("Latitude").getAsDouble(), nearest_end.get("Longitude").getAsDouble());
                LatLng inter_redStop = new LatLng(inter_red.get("Latitude").getAsDouble(), inter_red.get("Longitude").getAsDouble());
                LatLng inter_green = new LatLng(inter_greendown.get("Latitude").getAsDouble(), inter_greendown.get("Longitude").getAsDouble());

                mMap.addMarker(new MarkerOptions().position(start_stop).title(nearest_start.get("Pronunciation").getAsString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                mMap.addMarker(new MarkerOptions().position(inter_redStop).title(inter_red.get("Pronunciation").getAsString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                mMap.addMarker(new MarkerOptions().position(inter_green).title(inter_greendown.get("Pronunciation").getAsString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                mMap.addMarker(new MarkerOptions().position(end_stop).title(nearest_end.get("Pronunciation").getAsString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

                getWalkiingDirection(start, start_stop);
                getWalkiingDirection(end, end_stop);
                getWalkiingDirection(inter_redStop, inter_green);

            } else {
                Log.d("luas", "luas upper");

                boolean flag_start = false;
                for (int i = 0; i < greenUpper.size(); i++) {
                    if (greenUpper.get(i).equals(nearest_start)) {
                        start_index = i;
                        flag_start = true;
                    }
                }

                if (flag_start == false) {
                    distance_start = CalculateDistance(start, end);

                    for (int i = 0; i < greenUpper.size(); i++) {
                        JsonObject stop = (JsonObject) greenUpper.get(i);
                        LatLng cur = new LatLng(stop.get("Latitude").getAsDouble(), stop.get("Longitude").getAsDouble());

                        double distance2 = CalculateDistance(start, cur);

                        if (distance_start > distance2) {
                            nearest_start = stop;
                            start_index = i;
                            distance_start = distance2;
                        }
                    }
                }

                for (int i = greenup; i <= start_index; i++) {
                    JsonObject stop = (JsonObject) greenUpper.get(i);
                    green_route.add(new LatLng(stop.get("Latitude").getAsDouble(), stop.get("Longitude").getAsDouble()));
                }

                //RED
                boolean flag_end = false;
                if (end_index > red_Bel) {
                    for (int i = 0; i < redTal.size(); i++) {
                        if (redTal.get(i).equals(nearest_end)) {
                            flag_end = true;
                            end_index = i;
                        }
                    }

                    if (flag_end == true) {
                        for (int i = red_inter; i <= end_index; i++) {
                            JsonObject stop = (JsonObject) redTal.get(i);
                            red_route.add(new LatLng(stop.get("Latitude").getAsDouble(), stop.get("Longitude").getAsDouble()));
                        }
                    } else {
                        for (int i = 0; i < redSag.size(); i++) {
                            if (redSag.get(i).equals(nearest_end)) {
                                flag_end = true;
                                end_index = i;
                            }
                        }

                        for (int i = red_inter; i <= end_index; i++) {
                            JsonObject stop = (JsonObject) redSag.get(i);
                            red_route.add(new LatLng(stop.get("Latitude").getAsDouble(), stop.get("Longitude").getAsDouble()));
                        }
                    }
                }

                if (end_index > red_inter && end_index < red_Bel) {

                    for (int i = red_inter; i <= end_index; i++) {
                        JsonObject stop = (JsonObject) redTal.get(i);
                        red_route.add(new LatLng(stop.get("Latitude").getAsDouble(), stop.get("Longitude").getAsDouble()));
                    }
                }

                if (end_index < red_inter) {
                    for (int i = end_index; i <= red_inter; i++) {
                        JsonObject stop = (JsonObject) redTal.get(i);
                        red_route.add(new LatLng(stop.get("Latitude").getAsDouble(), stop.get("Longitude").getAsDouble()));
                    }
                }

                LatLng start_stop = new LatLng(nearest_start.get("Latitude").getAsDouble(), nearest_start.get("Longitude").getAsDouble());
                LatLng end_stop = new LatLng(nearest_end.get("Latitude").getAsDouble(), nearest_end.get("Longitude").getAsDouble());
                LatLng inter_redStop = new LatLng(inter_red.get("Latitude").getAsDouble(), inter_red.get("Longitude").getAsDouble());
                LatLng inter_green = new LatLng(inter_greenup.get("Latitude").getAsDouble(), inter_greenup.get("Longitude").getAsDouble());

                mMap.addMarker(new MarkerOptions().position(start_stop).title(nearest_start.get("Pronunciation").getAsString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                mMap.addMarker(new MarkerOptions().position(inter_redStop).title(inter_red.get("Pronunciation").getAsString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                mMap.addMarker(new MarkerOptions().position(inter_green).title(inter_greenup.get("Pronunciation").getAsString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                mMap.addMarker(new MarkerOptions().position(end_stop).title(nearest_end.get("Pronunciation").getAsString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

                getWalkiingDirection(start, start_stop);
                getWalkiingDirection(end, end_stop);
                getWalkiingDirection(inter_redStop, inter_green);
            }

            mMap.addPolyline(new PolylineOptions().width(20).color(Color.RED).addAll(red_route));
            mMap.addPolyline(new PolylineOptions().width(20).color(Color.GREEN).addAll(green_route));
        }
    }

    public void getWalkiingDirection(LatLng start, LatLng end) {

        ArrayList<LatLng> point_list = new ArrayList<>();
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.BLUE);
        polylineOptions.width(10);
        polylineOptions.pattern(PATTERN_POLYGON_ALPHA);

        String tomtom = "https://api.tomtom.com/routing/1/calculateRoute/" + start.latitude + "%2C" + start.longitude + "%3A"
                + end.latitude + "%2C" + end.longitude + "/json?routeRepresentation=polyline&avoid=unpavedRoads&travelMode=pedestrian&key=hsG3k8dTKXUpcbecSrGn3Gx4MWrCGAJG";

        String response = null;
        HTTPGetRequest routeAPI = new HTTPGetRequest();

        JsonParser parser = new JsonParser();

        try {
            response = routeAPI.execute(tomtom).get();
            Log.d("route_api", response);
            if (response != null) {
                Log.d("route_api", tomtom);
                Object object = parser.parse(response);
                JsonObject jsonObject = (JsonObject) object;
                JsonArray jsonArray = (JsonArray) jsonObject.get("routes");
                JsonObject object2 = (JsonObject) jsonArray.get(0);
                JsonArray routeArray = (JsonArray) object2.get("legs");
                JsonObject summary = (JsonObject) ((JsonObject) routeArray.get(0)).get("summary");
                JsonArray routePoint = (JsonArray) ((JsonObject) routeArray.get(0)).get("points");

                int min, hour = 0;
                int sec;
                sec = summary.get("travelTimeInSeconds").getAsInt();

                hour = sec / 3600;
                min = sec % 3600 / 60;

                for (int i = 0; i < routePoint.size(); i++) {
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
    }


    public double CalculateDistance(LatLng start, LatLng end) {

        double distance;

        Location locationA = new Location("point A");
        locationA.setLatitude(start.latitude);
        locationA.setLongitude(start.longitude);

        Location locationB = new Location("point B");
        locationB.setLatitude(end.latitude);
        locationB.setLongitude(end.longitude);

        distance = locationA.distanceTo(locationB);

        return distance;
    }
}