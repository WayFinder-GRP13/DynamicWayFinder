package com.group13.dynamicwayfinder.Utils;

import java.util.List;

public abstract class HTTPRequestBuilder {
    public abstract String URLStringBuilder(List input, boolean RequestType);

    public abstract String URLStringBuilderDistance(List serverRequestList);
}
