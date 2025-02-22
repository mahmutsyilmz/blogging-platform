package com.web.blog.exception;

import com.web.blog.exception.handler.ErrorMessage;

public class InvalidVerificationCodeException extends RuntimeException {
    public InvalidVerificationCodeException(ErrorMessage message) {
        super(message.prepareErrorMessage());
    }
}
