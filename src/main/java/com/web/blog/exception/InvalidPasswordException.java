package com.web.blog.exception;

import com.web.blog.exception.handler.ErrorMessage;

public class InvalidPasswordException extends RuntimeException {

    public InvalidPasswordException(ErrorMessage message) {
        super(message.prepareErrorMessage());
    }
}
