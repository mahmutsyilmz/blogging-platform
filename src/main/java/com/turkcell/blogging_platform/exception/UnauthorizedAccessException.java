package com.turkcell.blogging_platform.exception;

import com.turkcell.blogging_platform.exception.handler.ErrorMessage;

public class UnauthorizedAccessException extends RuntimeException

{
    public UnauthorizedAccessException(ErrorMessage errorMessage) {
        super(errorMessage.prepareErrorMessage());
    }
}
