package com.turkcell.blog.exception;

import com.turkcell.blog.exception.handler.ErrorMessage;

public class RequestNotPendingException extends RuntimeException {
    public RequestNotPendingException(ErrorMessage message) {
        super(message.prepareErrorMessage());
    }
}
