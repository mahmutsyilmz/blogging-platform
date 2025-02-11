package com.turkcell.blog.exception;

import com.turkcell.blog.exception.handler.ErrorMessage;

public class InvalidVerificationCodeException extends RuntimeException {
    public InvalidVerificationCodeException(ErrorMessage message) {
        super(message.prepareErrorMessage());
    }
}
