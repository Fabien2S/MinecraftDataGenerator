package dev.fabien2s.mdg.mapping.exceptions;

public class MappingException extends Exception{

    public MappingException(String message) {
        super(message);
    }

    public MappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
