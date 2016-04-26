package com.vedidev.restifizer;

import com.vedidev.restifizer.exception.RestifizerError;

/**
 * Created by vedi on 20/12/15.
 * Vedidev, 2015
 */
public interface IErrorHandler {
    /**
     * return true if the error has been handled, and should not be propagated to Response
     */
    boolean onRestifizerError(RestifizerError restifizerError);
}
