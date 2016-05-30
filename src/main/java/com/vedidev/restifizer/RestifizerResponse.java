package com.vedidev.restifizer;

import com.vedidev.restifizer.exception.RestifizerError;

import okhttp3.Request;

/**
 * Created by vedi on 20/12/15.
 * Vedidev, 2015
 */
public class RestifizerResponse {

    public final boolean isList;
    public final boolean hasError;
    public final String response;
    public final RestifizerError error;
    public final String tag;
    public final Request request;

    public RestifizerResponse(Request request, boolean fetchList,
                              String response, String tag) {

        this.isList = fetchList;
        this.response = response;
        this.hasError = false;
        this.error = null;
        this.request = request;
        this.tag = tag;
    }

    public RestifizerResponse(Request request, RestifizerError error, String tag) {
        this.isList = false;
        this.response = null;
        this.hasError = true;
        this.error = error;
        this.request = request;
        this.tag = tag;
    }
}
