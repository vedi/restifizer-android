package com.vedidev.restifizer.exception;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.vedidev.restifizer.RestifizerRequest;

/**
 * Created by vedi on 27/12/15.
 * Vedidev, 2015
 */
public class ExceptionFactory {
    public static RestifizerError createException(VolleyError volleyError, String tag, RestifizerRequest request) {
        NetworkResponse networkResponse = volleyError.networkResponse;

        int statusCode;

        if (networkResponse != null) {
            statusCode = networkResponse.statusCode;
        } else {
            statusCode = -1;
        }

        switch (statusCode) {
            case 400: {
                return new BadRequestError(statusCode, volleyError, tag, request);
            }
            case 401: {
                return new UnauthorizedError(statusCode, volleyError, tag, request);
            }
            case 403: {
                return new ForbiddenError(statusCode, volleyError, tag, request);
            }
            case 404: {
                return new NotFoundError(statusCode, volleyError, tag, request);
            }
            case 500: {
                return new InternalServerError(statusCode, volleyError, tag, request);
            }
            default: {
                Log.d("ExceptionFactory", "error = " + volleyError);
                if (volleyError.toString().contains("authentication challenge")) {
                    statusCode = 401;
                    return new UnauthorizedError(statusCode, volleyError, tag, request);
                }
                return new RestifizerError(statusCode, volleyError, tag, request);
            }
        }


    }
}
