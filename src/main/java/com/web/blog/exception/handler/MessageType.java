package com.web.blog.exception.handler;

import lombok.Getter;

@Getter
public enum MessageType {
    USERNAME_ALREADY_EXISTS("0001","Username already exists."),
    UNAUTHORIZED_ACCESS("0002","Unauthorized access."),
    USER_NOT_FOUND("0003","User not found."),
    INVALID_PASSWORD("0004","Invalid password."),
    POST_NOT_FOUND("0005","Post not found."),
    ALREADY_LIKED("0006","You have already liked this post."),
    NOT_LIKED("0007","You have not liked this post."),
    REQUEST_NOT_FOUND("0008","Request not found."),
    REQUEST_NOT_PENDING("0009","Request is not pending."),
    EMAIL_ALREADY_EXISTS("0010","Email already exists."),
    POST_REQUEST_EXISTS("0011","There is already a request for this post."),
    INVALID_VERIFICATION_CODE("0012","Invalid verification code."),
    GENERAL_EXCEPTION("9999","An error occurred.");

    private String code;
    private String message;


    MessageType(String code, String message) {
        this.code = code;
        this.message = message;
    }

    MessageType(String message) {
        this.message = message;
    }




}
