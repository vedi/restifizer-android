package com.vedidev.restifizer.exception;

import android.util.Log;

import com.android.volley.VolleyError;
import com.vedidev.restifizer.RestifizerRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by vedi on 20/12/15.
 * Vedidev, 2015
 */
public class RestifizerError extends Exception {
    public VolleyError errorRaw;
    public String tag;
    public final RestifizerRequest request;
    public int statusCode = -1;
    private String rawResponse = null;

    public RestifizerError(int statusCode, VolleyError error, String tag, RestifizerRequest request) {
        this.statusCode = statusCode;
        this.errorRaw = error;
        this.tag = tag;
        this.request = request;
        JSONObject jsonObject = null;

        if (error.networkResponse != null) {
            try {
                rawResponse = new String(error.networkResponse.data, "UTF-8");
                jsonObject = new JSONObject(rawResponse);
            } catch (JSONException | UnsupportedEncodingException e) {
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
