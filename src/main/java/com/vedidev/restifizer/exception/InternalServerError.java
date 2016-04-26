package com.vedidev.restifizer.exception;

import com.android.volley.VolleyError;
import com.vedidev.restifizer.RestifizerRequest;

/**
 * Created by vedi on 27/12/15.
 * Vedidev, 2015
 */
public class InternalServerError extends RestifizerError {
    public InternalServerError(int statusCode, VolleyError volleyError, String tag,
                               RestifizerRequest request) {

        super(statusCode, volleyError, tag, request);
    }
}