package com.turkcell.blogging_platform.exception;

import com.turkcell.blogging_platform.exception.handler.ErrorMessage;

public class BaseException extends RuntimeException {

    public BaseException() {

    }
    public BaseException(ErrorMessage errorMessage) {
        super(errorMessage.prepareErrorMessage());
    }
}
