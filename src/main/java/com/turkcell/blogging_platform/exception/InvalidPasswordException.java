package com.turkcell.blogging_platform.exception;

import com.turkcell.blogging_platform.exception.handler.ErrorMessage;

public class InvalidPasswordException extends RuntimeException {

    public InvalidPasswordException(ErrorMessage message) {
        super(message.prepareErrorMessage());
    }
}
