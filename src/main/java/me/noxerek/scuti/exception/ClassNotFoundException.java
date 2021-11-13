package me.noxerek.scuti.exception;

/**
 * @author netindev
 */
public class ClassNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ClassNotFoundException(final String message) {
        super(message);
    }

}
