package de.gleyder.admiral.core.parser;

/**
 * Thrown if an errors occurs in the parser
 *
 * @author Gleyder
 * @version 1.0
 * @since 1.0
 *
 * @see RuntimeException
 */
public class InputParserException extends RuntimeException {

    public InputParserException(String message) {
        super(message);
    }
}
