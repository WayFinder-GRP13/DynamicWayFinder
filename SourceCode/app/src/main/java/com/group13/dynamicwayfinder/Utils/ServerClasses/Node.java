package com.group13.dynamicwayfinder.Utils.ServerClasses;



import java.util.ArrayList;

public class Node {

    private String Name;
    private String StopId;
    private int TransportType;
    private double latitude;
    private double longitudue;
    private double score;
    private double distanceToStartLocation;
    private double distanceToEndLocation;
    private ArrayList<String> TransportRoute;

    public Node(String Name,int StopId,int TransportType,double latitude, double longitudue, double score) {
        this.Name=Name;
        this.StopId=Integer.toString(StopId);
        this.TransportType=TransportType;
        this.latitude = latitude;
        this.longitudue = longitudue;
        this.score = score;
    }
    public Node(Node node)
    {
        this.Name=node.getName();
        this.StopId=node.getStopId();
        this.latitude = node.getLatitude();
        this.longitudue = node.getLongitudue();
        this.TransportType=node.getTransportType();
        this.score = node.getScore();
        this.distanceToStartLocation=node.getDistanceToStartLocation();
        this.distanceToEndLocation=node.getDistanceToEndLocation();
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitudue() {
        return longitudue;
    }

    public void setLongitudue(double longitudue) {
        this.longitudue = longitudue;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
    public int getTransportType() {
        return TransportType;
    }

    public void setTransportType(int transportType) {
        TransportType = transportType;
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getStopId() {
        return StopId;
    }

    public void setStopId(String stopId) {
        StopId = stopId;
    }

    public void printNode(){
        System.out.println("Node is: "+TransportType+" stop name is: "+Name+"stop ID is: "+StopId+" Lat: "+latitude+" Lng: "+longitudue);
    }

    public double getDistanceToStartLocation() {
        return distanceToStartLocation;
    }

    public void setDistanceToStartLocation(double distanceToStartLocation) {
        this.distanceToStartLocation = distanceToStartLocation;
    }

    public double getDistanceToEndLocation() {
        return distanceToEndLocation;
    }

    public void setDistanceToEndLocation(double distanceToEndLocation) {
        this.distanceToEndLocation = distanceToEndLocation;
    }

    public ArrayList<String> getTransportRoute() {
        return TransportRoute;
    }

    public void setTransportRoute(ArrayList<String> transportRoute) {
        TransportRoute = transportRoute;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((StopId == null) ? 0 : StopId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Node other = (Node) obj;
        if (StopId == null) {
            if (other.StopId != null)
                return false;
        } else if (!StopId.equals(other.StopId))
            return false;
        return true;
    }
}



