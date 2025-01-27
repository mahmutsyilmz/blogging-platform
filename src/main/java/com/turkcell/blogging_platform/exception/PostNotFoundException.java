package com.turkcell.blogging_platform.exception;

import com.turkcell.blogging_platform.exception.handler.ErrorMessage;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(ErrorMessage message) {
        super(message.prepareErrorMessage());
    }
}
