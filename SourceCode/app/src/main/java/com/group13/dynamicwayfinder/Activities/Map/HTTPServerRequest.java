package com.group13.dynamicwayfinder.Activities.Map;

import com.group13.dynamicwayfinder.Utils.HTTPPOSTRequest;
import com.group13.dynamicwayfinder.Utils.ServerWrapperClass;

public class HTTPServerRequest extends HTTPPOSTRequest {

    private ServerFetcher serverFetcher;

    HTTPServerRequest(ServerWrapperClass serverWrapperClass,ServerFetcher serverFetcher) {
        super(serverWrapperClass);
        this.serverFetcher=serverFetcher;
    }

    @Override
    protected void onPostExecute(String Result) {
        serverFetcher.HTTPServerResult(Result);
    }
}
