package com.vedidev.restifizer;

import android.util.Base64;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vedidev.restifizer.exception.ExceptionFactory;
import com.vedidev.restifizer.exception.RestifizerError;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vedi on 20/12/15.
 * Vedidev, 2015
 */
public class RestifizerRequest {

    private RestifizerVolleyRequest restifizerVolleyRequest;

    enum AuthType {
        None,
        Client,
        Bearer
    }

    public String path;
    public int method;
    private String jsonStr;
    private RestifizerCallback callback;
    public boolean fetchList = true;
    public String tag;
    public int pageNumber = -1;
    public int pageSize = -1;
    public boolean patch;


    private Map<String, Object> filterParams;

    private Map<String, Object> extraQuery;

    private AuthType authType = AuthType.None;

    private RestifizerParams restifizerParams;
    private IErrorHandler errorHandler;
    private final RequestQueue requestQueue;

    public RestifizerRequest(RestifizerParams restifizerParams, IErrorHandler errorHandler,
                             RequestQueue requestQueue) {
        this.restifizerParams = restifizerParams;
        this.errorHandler = errorHandler;
        this.requestQueue = requestQueue;

        this.path = "";
    }

    public RestifizerRequest withClientAuth() {
        this.authType = AuthType.Client;

        return this;
    }

    public RestifizerRequest withBearerAuth() {
        this.authType = AuthType.Bearer;

        return this;
    }

    public RestifizerRequest withTag(String tag) {
        this.tag = tag;

        return this;
    }

    public RestifizerRequest filter(String key, Object value) {
        return filter(key, value, value instanceof String);
    }

    public RestifizerRequest filter(String key, Object value, boolean quoted) {
        if (filterParams == null) {
            filterParams = new HashMap<>();
        }

        if (quoted) {
            filterParams.put(key, "\"" + value + "\"");
        } else {
            filterParams.put(key, value);
        }

        return this;
    }

    public RestifizerRequest query(String key, Object value) {
        if (extraQuery == null) {
            extraQuery = new HashMap<>();
        }
        extraQuery.put(key, value);

        return this;
    }

    public RestifizerRequest page(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;

        return this;
    }

    public RestifizerRequest page(int pageNumber) {
        this.pageNumber = pageNumber;

        return this;
    }

    public RestifizerRequest setPageSize(int pageSize) {
        this.pageSize = pageSize;

        return this;
    }

    public RestifizerRequest one(String id) {
        this.path += "/" + id;
        this.fetchList = false;

        return this;
    }

    public RestifizerRequest list(String path) {
        this.path += "/" + path;
        this.fetchList = true;

        return this;
    }


    public void get(RestifizerCallback callback) {
        performRequest(Request.Method.GET, null, callback);
    }

    public void post(String jsonStr, RestifizerCallback callback) {
        performRequest(Request.Method.POST, jsonStr, callback);
    }

    public void put(String jsonStr, RestifizerCallback callback) {
        performRequest(Request.Method.PUT, jsonStr, callback);
    }

    public void patch(String jsonStr, RestifizerCallback callback) {
        this.patch = true;
        performRequest(Request.Method.POST, jsonStr, callback);
    }

    public void delete(String jsonStr, RestifizerCallback callback) {
        performRequest(Request.Method.DELETE, jsonStr, callback);
    }

    public RestifizerRequest copy() {

        RestifizerRequest restifizerRequest = new RestifizerRequest(
                restifizerParams, errorHandler, requestQueue);
        restifizerRequest.path = path;
        restifizerRequest.method = method;
        restifizerRequest.tag = tag;
        restifizerRequest.pageNumber = pageNumber;
        restifizerRequest.pageSize = pageSize;
        restifizerRequest.fetchList = fetchList;
        restifizerRequest.callback = callback;
        restifizerRequest.patch = patch;

        if (filterParams != null) {
            restifizerRequest.filterParams = new HashMap<>(filterParams);
        }
        if (extraQuery != null) {
            restifizerRequest.extraQuery = new HashMap<>(extraQuery);
        }
        restifizerRequest.authType = authType;
        return restifizerRequest;
    }

    private void performRequest(int method, String jsonStr,
                                final RestifizerCallback callback) {
        this.method = method;
        this.jsonStr = jsonStr;
        this.callback = callback;

        exec();

    }

    public void exec() {
        String url = path;
        StringBuilder queryStrBuilder = new StringBuilder();


        // paging
        if (pageNumber != -1) {
            if (queryStrBuilder.length() > 0) {
                queryStrBuilder.append('&');
            }
            queryStrBuilder.append("page=").append(pageNumber);
        }
        if (pageSize != -1) {
            if (queryStrBuilder.length() > 0) {
                queryStrBuilder.append('&');
            }
            queryStrBuilder.append("per_page=").append(pageSize);
        }

        // filtering
        if (filterParams != null && filterParams.size() > 0) {
            if (queryStrBuilder.length() > 0) {
                queryStrBuilder.append('&');
            }
            StringBuilder filterValueBuilder = new StringBuilder();
            filterValueBuilder.append('{');
            for (Map.Entry<String, Object> entry : filterParams.entrySet()) {
                filterValueBuilder.append("\"").append(entry.getKey()).append("\": ");
                filterValueBuilder.append(entry.getValue());
                filterValueBuilder.append(",");
            }
            int lastCommaIndex = filterValueBuilder.lastIndexOf(",");
            filterValueBuilder.delete(lastCommaIndex, lastCommaIndex + 1);
            filterValueBuilder.append('}');
            try {
                queryStrBuilder.append("filter=").append(
                        URLEncoder.encode(filterValueBuilder.toString(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException(e);
            }
        }

        // extra params
        if (extraQuery != null && extraQuery.size() > 0) {
            for (Map.Entry<String, Object> entry : extraQuery.entrySet()) {
                if (queryStrBuilder.length() > 0) {
                    queryStrBuilder.append('&');
                }
                queryStrBuilder.append(entry.getKey()).append('=').append(entry.getValue());
            }
        }

        if (queryStrBuilder.length() > 0) {
            url += "?" + queryStrBuilder.toString();
            Log.d("query url", "url = " + url);
        }

        restifizerVolleyRequest = new RestifizerVolleyRequest(method, url,
                jsonStr, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                RestifizerCallback callback = RestifizerRequest.this.callback;
                if (callback != null) {
                    callback.onCallback(new RestifizerResponse(restifizerVolleyRequest,
                            RestifizerRequest.this.fetchList, response, tag));
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        RestifizerError error = ExceptionFactory.createException(volleyError, tag,
                                RestifizerRequest.this);

                        if (errorHandler == null || !errorHandler.onRestifizerError(error)) {
                            RestifizerCallback callback = RestifizerRequest.this.callback;
                            if (callback != null) {
                                callback.onCallback(new RestifizerResponse(
                                        restifizerVolleyRequest,
                                        error,
                                        tag));
                            }
                        }
                    }
                });

//patch for old android
        if (patch) {
            restifizerVolleyRequest.setHeader("X-HTTP-Method-Override", "PATCH");
        }
        // Handle authentication
        if (this.authType == AuthType.Client) {
            try {
                String concatinated = this.restifizerParams.getClientId() + ":" +
                        this.restifizerParams.getClientSecret();

                String encoded = Base64.encodeToString(
                        concatinated.getBytes("UTF-8"),
                        Base64.NO_WRAP);
                restifizerVolleyRequest.setHeader("Authorization", "Basic " +
                        encoded);
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException(e);
            }
        } else if (this.authType == AuthType.Bearer) {
            restifizerVolleyRequest.setHeader("Authorization", "Bearer " +
                    restifizerParams.getAccessToken());
        }

        requestQueue.add(restifizerVolleyRequest);
    }
}
