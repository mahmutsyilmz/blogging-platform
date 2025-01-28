package com.turkcell.blogging_platform.exception.handler;

import lombok.Getter;

@Getter
public enum MessageType {
    USERNAME_ALREADY_EXISTS("0001","Kullanıcı adı zaten mevcut."),
    UNAUTHORIZED_ACCESS("0002","Bu kaynağa erişim yetkiniz yok."),
    USER_NOT_FOUND("0003","Kullanıcı bulunamadı."),
    INVALID_PASSWORD("0004","Geçersiz şifre."),
    POST_NOT_FOUND("0005","Post bulunamadı"),
    GENERAL_EXCEPTION("9999","Genel bir hata oluştu.");

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
