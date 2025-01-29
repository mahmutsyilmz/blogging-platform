package com.turkcell.blogging_platform.exception;

import com.turkcell.blogging_platform.exception.handler.ErrorMessage;

public class AlreadyLikedException extends RuntimeException {
    public AlreadyLikedException(ErrorMessage message) {
        super(message.prepareErrorMessage());
    }
}
