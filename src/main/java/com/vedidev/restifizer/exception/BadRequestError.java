package com.vedidev.restifizer.exception;

import com.vedidev.restifizer.RestifizerRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Response;

/**
 * Created by vedi on 27/12/15.
 * Vedidev, 2015
 */
public class BadRequestError extends RestifizerError {

    private static final String BAD_REQUEST = "Bad Request";

    public enum Oauth2Error {
        INVALID_REQUEST, INVALID_CLIENT, INVALID_GRANT, UNAUTHORIZED_CLIENT, UNSUPPORTED_GRANT_TYPE
    }

    public class ValidationItem {
        private final String key;
        private final String message;

        public ValidationItem(String key, String message) {
            this.key = key;
            this.message = message;
        }
    }

    private String message = null;

    private Oauth2Error oauth2Error = null;

    private boolean badRequest = false;

    private Map<String, ValidationItem> details = null;

    public BadRequestError(int statusCode, Response response, String tag,
                           RestifizerRequest request) {
        super(statusCode, response, tag, request);
    }

    @Override
    protected void parse(JSONObject jsonObject) {
        super.parse(jsonObject);

        if (!tryParseOauth2(jsonObject) && !tryParseBadRequest(jsonObject)) {
            this.message = jsonObject.optString("message", null);
        }
    }

    private boolean tryParseOauth2(JSONObject jsonObject) {
        String error = jsonObject.optString("error", null);
        if (error != null) {
            try {
                this.oauth2Error = Oauth2Error.valueOf(error.toUpperCase());
                this.message = jsonObject.optString("error_description", null);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean tryParseBadRequest(JSONObject jsonObject) {
        String error = jsonObject.optString("error", null);
        if (error == null || !BAD_REQUEST.equals(error)) {
            return false;
        }

        badRequest = true;

        JSONObject detailsJson = jsonObject.optJSONObject("details");
        if (detailsJson != null) {

            details = new HashMap<>();

            Iterator<String> keys = detailsJson.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject fieldJson = detailsJson.optJSONObject(key);
                String message = fieldJson.optString("message", null);

                ValidationItem validationItem = new ValidationItem(key, message);

                details.put(key, validationItem);
            }
        }

        this.message = jsonObject.optString("message", null);

        return true;
    }

    public boolean isOauth2() {
        return oauth2Error != null;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Oauth2Error getOauth2Error() {
        return oauth2Error;
    }

    public boolean isBadRequest() {
        return badRequest;
    }

    public Map<String, ValidationItem> getDetails() {
        return details;
    }

}
