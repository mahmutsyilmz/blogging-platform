package com.turkcell.blogging_platform.exception.handler;

import com.turkcell.blogging_platform.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    private List<String> addMapValue(List<String> list, String newValue){
        list.add(newValue);
        return list;
    }
    //spring validationdan atılan hataların yönetimi
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, List<String>> errorsMap = new HashMap<>();

        for(ObjectError objError : ex.getBindingResult().getAllErrors()){
           String fieldName =  ((FieldError)objError).getField();
           if (errorsMap.containsKey(fieldName)){
                errorsMap.put(fieldName, addMapValue(errorsMap.get(fieldName), objError.getDefaultMessage()));
           }else {
                errorsMap.put(fieldName, addMapValue(new ArrayList<>(), objError.getDefaultMessage()));
           }
        }
        return ResponseEntity.badRequest().body(createApiError(errorsMap,request,HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(AlreadyLikedException.class)
    public ResponseEntity<ApiError> handleAlreadyLikedException(AlreadyLikedException ex, WebRequest request) {
        return ResponseEntity.badRequest().body(createApiError(ex.getMessage(),request,HttpStatus.CONFLICT));
    }

    @ExceptionHandler(NotLikedException.class)
    public ResponseEntity<ApiError> handleNotLikedException(NotLikedException ex, WebRequest request) {
        return ResponseEntity.badRequest().body(createApiError(ex.getMessage(),request,HttpStatus.NOT_FOUND));
    }

    //unique username hatasının yönetimi
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUserAlreadyExistsException(UsernameAlreadyExistsException ex, WebRequest request) {
        return ResponseEntity.badRequest().body(createApiError(ex.getMessage(),request,HttpStatus.CONFLICT));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {
        return ResponseEntity.badRequest().body(createApiError(ex.getMessage(),request,HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ApiError> handleInvalidPasswordException(InvalidPasswordException ex, WebRequest request) {
        return ResponseEntity.badRequest().body(createApiError(ex.getMessage(),request,HttpStatus.UNAUTHORIZED));
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ApiError> handlePostNotFoundException(PostNotFoundException ex, WebRequest request) {
        return ResponseEntity.badRequest().body(createApiError(ex.getMessage(),request,HttpStatus.NOT_FOUND));
    }

    public <E> ApiError<E> createApiError(E message,WebRequest request,HttpStatus status) {
        ApiError apiError = new ApiError();
        apiError.setStatus(status.value());

        Exception exception = new Exception();
        exception.setCreatedDate(new Date());
        exception.setHostName(getHostName());
        exception.setPath(request.getDescription(false));
        exception.setMessage(message);

        apiError.setException(exception);
        return apiError;
    }

    private String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
