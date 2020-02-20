package com.group13.dynamicwayfinder.Utils;

public class ServerWrapperClass {
    private RestAPIRequestInformation restAPIRequestInformation;
    private String httpRequest;

    public ServerWrapperClass(String httpRequest, RestAPIRequestInformation restAPIRequestInformation) {
        this.httpRequest = httpRequest;
        this.restAPIRequestInformation = restAPIRequestInformation;
    }

    public String getHttpRequest() {
        return httpRequest;
    }

    public RestAPIRequestInformation getRestAPIRequestInformation() {
        return restAPIRequestInformation;
    }

}
