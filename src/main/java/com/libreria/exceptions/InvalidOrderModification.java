package com.libreria.exceptions;

public class InvalidOrderModification extends Exception{
    
    public InvalidOrderModification (){
        super("Invalid modification of an Order");
    }

}
