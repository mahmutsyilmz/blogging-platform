package com.turkcell.blogging_platform.exception;

import com.turkcell.blogging_platform.exception.handler.ErrorMessage;

public class UsernameAlreadyExistsException extends RuntimeException {


  public UsernameAlreadyExistsException(ErrorMessage errorMessage) {
    super(errorMessage.prepareErrorMessage());
  }

}
