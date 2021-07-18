package dev.fabien2s.mdg.mapping.exceptions;

public class MappingNotFoundException extends MappingException {

    public MappingNotFoundException(String message) {
        super(message);
    }

    public MappingNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
