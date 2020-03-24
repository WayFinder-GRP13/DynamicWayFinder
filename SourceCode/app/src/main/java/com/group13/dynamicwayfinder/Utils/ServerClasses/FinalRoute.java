package com.group13.dynamicwayfinder.Utils.ServerClasses;

public class FinalRoute{
    private Node Origin;
    private Node Destination;
    private String OverviewPolyline;
    private int RouteType;
    private int lengthMinutes;
    private String RouteNumber;
    private String departureTime;

    public FinalRoute(Node origin, Node destination, String overviewPolyline, int RouteType, int lengthMinutes, String routeNumber,String departureTime) {
        Origin = origin;
        Destination = destination;
        OverviewPolyline = overviewPolyline;
        this.RouteType=RouteType;
        this.lengthMinutes=lengthMinutes;
        RouteNumber = routeNumber;
        this.departureTime = departureTime;
    }

    public String getOverviewPolyline() {
        return OverviewPolyline;
    }

    public void setOverviewPolyline(String overviewPolyline) {
        OverviewPolyline = overviewPolyline;
    }

    public Node getOrigin() {
        return Origin;
    }

    public void setOrigin(Node origin) {
        Origin = origin;
    }

    public Node getDestination() {
        return Destination;
    }

    public void setDestination(Node destination) {
        Destination = destination;
    }

    public int getRouteType() {
        return RouteType;
    }

    public void setRouteType(int routeType) {
        RouteType = routeType;
    }

    public int getLengthMinutes() {
        return lengthMinutes;
    }

    public void setLengthMinutes(int lengthMinutes) {
        this.lengthMinutes = lengthMinutes;
    }

    public String getRouteNumber() {
        return RouteNumber;
    }

    public void setRouteNumber(String routeNumber) {
        RouteNumber = routeNumber;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }
}