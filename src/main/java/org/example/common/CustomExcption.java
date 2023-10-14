package org.example.common;


/**
 * 自定义业务异常
 */
public class CustomExcption extends RuntimeException{
    public CustomExcption(String message){
        super(message);
    }
}
