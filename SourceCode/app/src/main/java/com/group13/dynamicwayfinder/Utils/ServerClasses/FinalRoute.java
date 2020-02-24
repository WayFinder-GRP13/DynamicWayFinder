package com.group13.dynamicwayfinder.Utils.ServerClasses;


public class FinalRoute{
    private Node Origin;
    private Node Destination;
    private String OverviewPolyline;

    public FinalRoute(Node origin, Node destination, String overviewPolyline) {
        Origin = origin;
        Destination = destination;
        OverviewPolyline = overviewPolyline;
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
}