package com.turkcell.blogging_platform.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiError<E> {
    private Integer status;
    private Exception<E> exception;
}
