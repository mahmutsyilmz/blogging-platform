package com.turkcell.blog.exception;

import com.turkcell.blog.exception.handler.ErrorMessage;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(ErrorMessage message) {
        super(message.prepareErrorMessage());
    }
}
