package com.turkcell.blogging_platform.exception;

import com.turkcell.blogging_platform.exception.handler.ErrorMessage;

public class NotLikedException extends RuntimeException {
    public NotLikedException(ErrorMessage message) {
        super(message.prepareErrorMessage());
    }
}
