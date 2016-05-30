package com.vedidev.restifizer;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.vedidev.restifizer.exception.ExceptionFactory;
import com.vedidev.restifizer.exception.RestifizerError;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by vedi on 20/12/15.
 * Vedidev, 2015
 */
public class RestifizerRequest {

    private Request.Builder builder;
    private static final int GET = 201;
    private static final int POST = 202;
    private static final int PUT = 203;
    private static final int PATCH = 204;
    private static final int DELETE = 205;

    private static final MediaType APPJSON = MediaType.parse("application/json; charset=utf-8");

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
    private final OkHttpClient client;

    public RestifizerRequest(RestifizerParams restifizerParams, IErrorHandler errorHandler,
                             OkHttpClient client) {
        this.restifizerParams = restifizerParams;
        this.errorHandler = errorHandler;
        this.client = client;

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
        performRequest(GET, null, callback);
    }

    public void post(String jsonStr, RestifizerCallback callback) {
        performRequest(POST, jsonStr, callback);
    }

    public void put(String jsonStr, RestifizerCallback callback) {
        performRequest(PUT, jsonStr, callback);
    }

    public void patch(String jsonStr, RestifizerCallback callback) {
        this.patch = true;
        performRequest(PATCH, jsonStr, callback);
    }

    public void delete(String jsonStr, RestifizerCallback callback) {
        performRequest(DELETE, jsonStr, callback);
    }

    public RestifizerRequest copy() {

        RestifizerRequest restifizerRequest = new RestifizerRequest(
                restifizerParams, errorHandler, client);
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

        builder = new Request.Builder();
        builder.url(url);
        initMethod();
        // Handle authentication
        if (this.authType == AuthType.Client) {
            try {
                String concatinated = this.restifizerParams.getClientId() + ":" +
                        this.restifizerParams.getClientSecret();

                String encoded = Base64.encodeToString(
                        concatinated.getBytes("UTF-8"),
                        Base64.NO_WRAP);
                builder.header("Authorization", "Basic " +
                        encoded);
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException(e);
            }
        } else if (this.authType == AuthType.Bearer) {
            builder.header("Authorization", "Bearer " +
                    restifizerParams.getAccessToken());
        }

        final Request request = builder.build();

        new AsyncTask<Void, Void, Response>() {
            @Override
            protected Response doInBackground(Void... params) {
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            }

            @Override
            protected void onPostExecute(Response response) {
                RestifizerCallback callback = RestifizerRequest.this.callback;
                if (callback == null) {
                    return;
                }
                if (response.isSuccessful()) {
                    try {
                        callback.onCallback(new RestifizerResponse(request,
                                RestifizerRequest.this.fetchList, response.body().string(), tag));
                    } catch (IOException e) {
                        error(request);
                    }
                } else {
                    error(request);
                }
            }
        }.executeOnExecutor(RestifizerManager.getInstance().getExecutor());
    }

    private void initMethod() {
        switch (method) {
            case GET:
                builder.get();
                break;
            case POST:
                builder.post(RequestBody.create(APPJSON, jsonStr));
                break;
            case PUT:
                builder.put(RequestBody.create(APPJSON, jsonStr));
                break;
            case PATCH:
                builder.patch(RequestBody.create(APPJSON, jsonStr));
                break;
            case DELETE:
                builder.delete();
                break;
        }
    }

    private void error(Request request) {
        RestifizerError error = ExceptionFactory.createException(null, tag,
                RestifizerRequest.this);
        if (errorHandler == null || !errorHandler.onRestifizerError(error)) {
            callback.onCallback(new RestifizerResponse(request, error, tag));
        }
    }
}
