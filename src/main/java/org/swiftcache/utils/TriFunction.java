package org.swiftcache.utils;

/**
 * A functional interface representing a function that accepts three arguments
 * and produces a result. This interface can be used to create lambda expressions
 * or method references that take three parameters.
 *
 * @param <A> the type of the first argument to the function
 * @param <B> the type of the second argument to the function
 * @param <C> the type of the third argument to the function
 * @param <R> the type of the result of the function
 */
@FunctionalInterface
public interface TriFunction<A, B, C, R> {

    /**
     * Applies this function to the given arguments.
     *
     * @param a the first argument
     * @param b the second argument
     * @param c the third argument
     * @return the function result
     */
    R apply(A a, B b, C c);
}
