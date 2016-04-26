package com.vedidev.restifizer.exception;

import com.android.volley.VolleyError;
import com.vedidev.restifizer.RestifizerRequest;

/**
 * Created by vedi on 27/12/15.
 * Vedidev, 2015
 */
public class UnauthorizedError extends RestifizerError {
    public UnauthorizedError(int statusCode, VolleyError volleyError, String tag,
                             RestifizerRequest request) {

        super(statusCode, volleyError, tag, request);
    }
}
