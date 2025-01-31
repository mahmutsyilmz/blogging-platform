package com.turkcell.blog.exception;

import com.turkcell.blog.exception.handler.ErrorMessage;

public class UsernameAlreadyExistsException extends RuntimeException {


  public UsernameAlreadyExistsException(ErrorMessage errorMessage) {
    super(errorMessage.prepareErrorMessage());
  }

}
