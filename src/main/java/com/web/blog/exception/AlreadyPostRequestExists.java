package com.web.blog.exception;

import com.web.blog.exception.handler.ErrorMessage;

public class AlreadyPostRequestExists extends RuntimeException {
    public AlreadyPostRequestExists(ErrorMessage message) {
        super(message.prepareErrorMessage());
    }
}
