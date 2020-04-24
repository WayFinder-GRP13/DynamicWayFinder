package com.group13.dynamicwayfinder.Activities.Map;

import com.group13.dynamicwayfinder.Utils.HTTPPOSTRequest;
import com.group13.dynamicwayfinder.Utils.ServerWrapperClass;
import com.group13.dynamicwayfinder.Utils.ServerWrapperDistanceClass;

public class HTTPServerRequest extends HTTPPOSTRequest {

    private ServerFetcher serverFetcher;

    HTTPServerRequest(ServerWrapperClass serverWrapperClass,ServerFetcher serverFetcher) {
        super(serverWrapperClass);
        this.serverFetcher=serverFetcher;
    }

    HTTPServerRequest(ServerWrapperDistanceClass serverDistanceWrapperClass, ServerFetcher serverFetcher) {
        super(serverDistanceWrapperClass);
        this.serverFetcher=serverFetcher;
    }

    @Override
    protected void onPostExecute(String Result) {
        if(Result.contains("\"lng\":")){
            serverFetcher.HTTPServerDistanceResult(Result);
        }else {
            serverFetcher.HTTPServerResult(Result);
        }
    }
}
