package com.turkcell.blogging_platform.exception.handler;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Exception<E> {
    private String hostName;
    private String path;
    private Date createdDate;
    private E message;
}
