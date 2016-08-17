package com.vedidev.restifizer.exception;


import com.vedidev.restifizer.RestifizerRequest;

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
    public String message;
    public String rawResponse = null;

    public RestifizerError(int statusCode, Response response, String tag, RestifizerRequest request) {
        this.statusCode = statusCode;
        this.response = response;
        this.tag = tag;
        this.request = request;
        JSONObject jsonObject;

        if (response != null) {
            try {
                this.rawResponse = response.message();
                jsonObject = new JSONObject(response.body().string());
                parse(jsonObject);
            } catch (Exception e) {
                //nothing to do here
            }
        }

    }

    protected void parse(@SuppressWarnings("UnusedParameters") JSONObject jsonObject) {
        if (jsonObject != null) {
            this.message = jsonObject.optString("message", null);
        }
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
