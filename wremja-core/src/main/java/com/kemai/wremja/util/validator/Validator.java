package com.kemai.wremja.util.validator;

/**
 * Defines a generic validator to validate String input.
 */
public interface Validator {
   /**
    * Validates the input.
    * 
    * @return true if the input was valid, false otherwise
    */
    public boolean validate( String value );
}
