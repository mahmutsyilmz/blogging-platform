package com.web.blog.exception;

import com.web.blog.exception.handler.ErrorMessage;

public class RequestNotFoundException extends RuntimeException {
    public RequestNotFoundException(ErrorMessage message) {
        super(message.prepareErrorMessage());
    }
}
