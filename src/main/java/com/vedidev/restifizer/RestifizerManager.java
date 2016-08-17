package com.vedidev.restifizer;


import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.internal.Util;

/**
 * Created by vedi on 20/12/15.
 * Vedidev, 2015
 */
public class RestifizerManager implements RestifizerParams {

    private static RestifizerManager INSTANCE = null;
    private static Executor executor;

    private String baseUrl;
    private OkHttpClient client = null;
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
                this, errorHandler, client);
        restifizerRequest.fetchList = true;
        restifizerRequest.path += baseUrl + "/" + resourceName;
        return restifizerRequest;
    }

    protected Executor getExecutor() {
        if (executor == null) {
            executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(),
                    Util.threadFactory("OkHttp Dispatcher", false));
        }
        return executor;
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

    public RestifizerManager setClient(OkHttpClient client) {
        this.client = client;
        return this;
    }

    public RestifizerManager setErrorHandler(IErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }
}
