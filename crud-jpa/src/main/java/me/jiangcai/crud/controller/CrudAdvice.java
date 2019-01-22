package me.jiangcai.crud.controller;

import me.jiangcai.crud.exception.CrudNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author CJ
 */
@ControllerAdvice
public class CrudAdvice {

    @ExceptionHandler(CrudNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void sawCrudNotFoundException() {

    }

}
