package com.web.blog.exception;

import com.web.blog.exception.handler.ErrorMessage;

public class NotLikedException extends RuntimeException {
    public NotLikedException(ErrorMessage message) {
        super(message.prepareErrorMessage());
    }
}
