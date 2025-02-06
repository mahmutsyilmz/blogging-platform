package com.turkcell.blog.exception;

import com.turkcell.blog.exception.handler.ErrorMessage;

public class AlreadyPostRequestExists extends RuntimeException {
    public AlreadyPostRequestExists(ErrorMessage message) {
        super(message.prepareErrorMessage());
    }
}
