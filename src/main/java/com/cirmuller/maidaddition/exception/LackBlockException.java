package com.cirmuller.maidaddition.exception;

public class LackBlockException extends RuntimeException{
    public LackBlockException(String string){
        super(string);
    }
    public LackBlockException(){
        super();
    }
}
