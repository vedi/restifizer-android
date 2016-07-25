package com.vedidev.restifizer.exception;


import android.util.Log;

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
    public String message;
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
                message = response.body().string();
                jsonObject = new JSONObject(rawResponse);
                parse(jsonObject);
            } catch (Exception e) {
                Log.w("RestifizerError", e);
            }
        }

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
