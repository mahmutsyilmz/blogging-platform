package com.turkcell.blog.exception;

import com.turkcell.blog.exception.handler.ErrorMessage;

public class NotLikedException extends RuntimeException {
    public NotLikedException(ErrorMessage message) {
        super(message.prepareErrorMessage());
    }
}
