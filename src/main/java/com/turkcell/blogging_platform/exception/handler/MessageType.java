package com.turkcell.blogging_platform.exception.handler;

import lombok.Getter;

@Getter
public enum MessageType {
    USERNAME_ALREADY_EXISTS("0001","kullanıcı adı zaten mevcut"),
    UNAUTHORIZED_ACCESS("0002","yetkisiz erişim"),
    USER_NOT_FOUND("0003","kullanıcı bulunamadı"),
    INVALID_PASSWORD("0004","geçersiz şifre"),
    POST_NOT_FOUND("0005","post bulunamadı"),
    GENERAL_EXCEPTION("9999","genel hata");

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
