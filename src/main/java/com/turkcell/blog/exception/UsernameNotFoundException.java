package com.turkcell.blog.exception;

import com.turkcell.blog.exception.handler.ErrorMessage;

public class UsernameNotFoundException extends RuntimeException {

    public UsernameNotFoundException(ErrorMessage errorMessage) {
        super(errorMessage.prepareErrorMessage());
    }
}
