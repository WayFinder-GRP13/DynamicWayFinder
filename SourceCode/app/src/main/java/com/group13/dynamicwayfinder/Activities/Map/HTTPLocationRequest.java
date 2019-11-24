package com.group13.dynamicwayfinder.Activities.Map;

import com.group13.dynamicwayfinder.Utils.HTTPGetRequest;

public class HTTPLocationRequest extends HTTPGetRequest {

    private AddressFetcher addressFetcher;

    HTTPLocationRequest(AddressFetcher addressFetcher){
        this.addressFetcher=addressFetcher;
    }
    @Override
    protected void onPostExecute(String Result) {
        addressFetcher.HTTPLocationResult(Result);
    }
}
