package com.cirmuller.maidaddition.exception;

public class TimeoutException extends ExecuteActionException{
    public TimeoutException(String string){
        super(string);
    }
}
