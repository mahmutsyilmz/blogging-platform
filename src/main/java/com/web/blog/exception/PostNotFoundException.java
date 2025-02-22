package com.web.blog.exception;

import com.web.blog.exception.handler.ErrorMessage;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(ErrorMessage message) {
        super(message.prepareErrorMessage());
    }
}
