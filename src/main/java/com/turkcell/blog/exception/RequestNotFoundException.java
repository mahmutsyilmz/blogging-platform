package com.turkcell.blog.exception;

import com.turkcell.blog.exception.handler.ErrorMessage;

public class RequestNotFoundException extends RuntimeException {
    public RequestNotFoundException(ErrorMessage message) {
        super(message.prepareErrorMessage());
    }
}
