package com.vedidev.restifizer.exception;

import android.util.Log;

import com.vedidev.restifizer.RestifizerRequest;

import okhttp3.Response;

/**
 * Created by vedi on 27/12/15.
 * Vedidev, 2015
 */
public class ExceptionFactory {
    public static RestifizerError createException(Response response, String tag, RestifizerRequest request) {

        int statusCode;

        if (response != null) {
            statusCode = response.code();
        } else {
            statusCode = -1;
        }

        switch (statusCode) {
            case 400: {
                return new BadRequestError(statusCode, response, tag, request);
            }
            case 401: {
                return new UnauthorizedError(statusCode, response, tag, request);
            }
            case 403: {
                return new ForbiddenError(statusCode, response, tag, request);
            }
            case 404: {
                return new NotFoundError(statusCode, response, tag, request);
            }
            case 500: {
                return new InternalServerError(statusCode, response, tag, request);
            }
            default: {
                if (response != null) {
                    Log.d("ExceptionFactory", "error = " + response.message());
                    if (response.message().contains("authentication challenge")) {
                        statusCode = 401;
                        return new UnauthorizedError(statusCode, response, tag, request);
                    }
                }
                return new RestifizerError(statusCode, null, tag, request);
            }
        }
    }
}

