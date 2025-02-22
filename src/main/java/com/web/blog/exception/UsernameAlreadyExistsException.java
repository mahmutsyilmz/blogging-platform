package com.web.blog.exception;

import com.web.blog.exception.handler.ErrorMessage;

public class UsernameAlreadyExistsException extends RuntimeException {


  public UsernameAlreadyExistsException(ErrorMessage errorMessage) {
    super(errorMessage.prepareErrorMessage());
  }

}
