package com.group13.dynamicwayfinder.Utils;

public class ServerWrapperDistanceClass {
    private  LocationPoint locationPoint;
    private String httpRequest;

    public ServerWrapperDistanceClass(String httpRequest, LocationPoint locationPoint) {
        this.httpRequest = httpRequest;
        this.locationPoint = locationPoint;
    }

    public String getHttpRequest() {
        return httpRequest;
    }

    public LocationPoint getlocationPoint() {
        return locationPoint;
    }

}
