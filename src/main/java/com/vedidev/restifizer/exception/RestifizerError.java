package com.vedidev.restifizer.exception;


import com.vedidev.restifizer.RestifizerRequest;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;

/**
 * Created by vedi on 20/12/15.
 * Vedidev, 2015
 */
public class RestifizerError extends Exception {
    public Response response;
    public String tag;
    public final RestifizerRequest request;
    public int statusCode = -1;
    private String rawResponse = null;

    public RestifizerError(int statusCode, Response response, String tag, RestifizerRequest request) {
        this.statusCode = statusCode;
        this.response = response;
        this.tag = tag;
        this.request = request;
        JSONObject jsonObject = null;

        if (response != null) {
            try {
                rawResponse = response.message();
                jsonObject = new JSONObject(rawResponse);
            } catch (JSONException e) {
                // It's not JSON or not UTF, and it's ok
            }
        }

        parse(jsonObject);
    }

    protected void parse(@SuppressWarnings("UnusedParameters") JSONObject jsonObject) {

    }

    @Override
    public String toString() {
        String result = "statusCode: " + statusCode + ", tag: " + tag + ", raw: ";
        if (rawResponse != null) {
            result += rawResponse;
        } else {
            result += "<EMPTY>";
        }

        return result;
    }

}
