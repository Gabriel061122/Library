package com.libreria.exceptions;

public class InvalidOrderStateModificationException extends InvalidOrderModification{
    public InvalidOrderStateModificationException(){
        super();
    }

    @Override
    public String getMessage(){
        return super.getMessage() + " Can't modify an Order with OrderState distinct of PENDING";
    }
}
