package com.turkcell.blog.exception;

import com.turkcell.blog.exception.handler.ErrorMessage;

public class AlreadyLikedException extends RuntimeException {
    public AlreadyLikedException(ErrorMessage message) {
        super(message.prepareErrorMessage());
    }
}
