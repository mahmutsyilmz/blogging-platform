package com.web.blog.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage {

    private MessageType messageType;


    public String prepareErrorMessage() {
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append(messageType.getMessage());

        return errorMessage.toString();

    }
}
