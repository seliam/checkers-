package com.checkers.core.exceptions;


public class InvalidArrangement extends Exception{
    public InvalidArrangement(){
        super("Invalid arrangement.");
    }

    public InvalidArrangement(String msg){
        super(msg);
    }
}
