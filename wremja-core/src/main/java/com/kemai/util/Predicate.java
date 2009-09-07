package com.kemai.util;

/**
 * Defines a functor interface implemented by classes that perform a predicate
 * test on an object.
 */
public interface Predicate<T> {

    /**
     * Use the specified parameter to perform a test that returns true or false.
     *
     * @param object  the object to evaluate, should not be changed
     * @return true or false
     * @throws IllegalArgumentException (runtime) if the input is invalid
     */
    public boolean evaluate(T object);

}