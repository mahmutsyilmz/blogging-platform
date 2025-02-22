package com.web.blog.exception;

import com.web.blog.exception.handler.ErrorMessage;

public class AlreadyLikedException extends RuntimeException {
    public AlreadyLikedException(ErrorMessage message) {
        super(message.prepareErrorMessage());
    }
}
