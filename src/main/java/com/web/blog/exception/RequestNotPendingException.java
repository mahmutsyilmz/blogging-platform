package com.web.blog.exception;

import com.web.blog.exception.handler.ErrorMessage;

public class RequestNotPendingException extends RuntimeException {
    public RequestNotPendingException(ErrorMessage message) {
        super(message.prepareErrorMessage());
    }
}
