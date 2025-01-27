package com.turkcell.blogging_platform.exception;

import com.turkcell.blogging_platform.exception.handler.ErrorMessage;

public class UsernameNotFoundException extends RuntimeException {

    public UsernameNotFoundException(ErrorMessage errorMessage) {
        super(errorMessage.prepareErrorMessage());
    }
}
