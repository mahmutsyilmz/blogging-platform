package com.turkcell.blog.exception;

import com.turkcell.blog.exception.handler.ErrorMessage;

public class InvalidPasswordException extends RuntimeException {

    public InvalidPasswordException(ErrorMessage message) {
        super(message.prepareErrorMessage());
    }
}
