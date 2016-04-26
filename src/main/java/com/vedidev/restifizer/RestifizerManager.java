package com.vedidev.restifizer;

import com.android.volley.RequestQueue;

/**
 * Created by vedi on 20/12/15.
 * Vedidev, 2015
 */
public class RestifizerManager implements RestifizerParams {

    private static RestifizerManager INSTANCE = null;

    private String baseUrl;
    private RequestQueue requestQueue = null;
    private IErrorHandler errorHandler;

    private String clientId;
    private String clientSecret;
    private String accessToken;

    public static RestifizerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RestifizerManager();
        }

        return INSTANCE;
    }

    public RestifizerManager configClientAuth(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        return this;
    }

    public RestifizerManager configBearerAuth(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public RestifizerRequest resourceAt(String resourceName) {
        RestifizerRequest restifizerRequest = new RestifizerRequest(
                this, errorHandler, requestQueue);
        restifizerRequest.fetchList = true;
        restifizerRequest.path += baseUrl + "/" + resourceName;
        return restifizerRequest;
    }

    /* RestifizerParams */
    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public RestifizerManager setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public RestifizerManager setRequestQueue(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
        return this;
    }

    public RestifizerManager setErrorHandler(IErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }
}
