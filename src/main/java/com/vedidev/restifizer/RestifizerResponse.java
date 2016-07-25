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
    public final int statusCode;
    public final String response;
    public final String tag;
    public final RestifizerError error;
    public final Request request;

    public RestifizerResponse(Request request, boolean fetchList,
                              String response, String tag) {

        this.isList = fetchList;
        this.response = response;
        this.statusCode = 200;
        this.hasError = false;
        this.error = null;
        this.request = request;
        this.tag = tag;
    }

    public RestifizerResponse(Request request, RestifizerError error, String tag) {
        this.isList = false;
        this.response = error != null ? error.message : null;
        this.hasError = true;
        this.error = error;
        this.statusCode = error != null ? error.statusCode : -1;
        this.request = request;
        this.tag = tag;
    }
}
