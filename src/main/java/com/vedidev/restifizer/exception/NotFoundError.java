package com.vedidev.restifizer.exception;

import com.vedidev.restifizer.RestifizerRequest;

import okhttp3.Response;

/**
 * Created by vedi on 27/12/15.
 * Vedidev, 2015
 */
public class NotFoundError extends RestifizerError {

    public NotFoundError(int statusCode, Response response, String tag,
                         RestifizerRequest request) {

        super(statusCode, response, tag, request);
    }
}
