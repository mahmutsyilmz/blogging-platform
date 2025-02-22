package com.web.blog.exception;

import com.web.blog.exception.handler.ErrorMessage;

public class UsernameNotFoundException extends RuntimeException {

    public UsernameNotFoundException(ErrorMessage errorMessage) {
        super(errorMessage.prepareErrorMessage());
    }
}
