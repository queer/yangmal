package com.mewna.yangmal.function;

/**
 * @author amy
 * @since 11/10/19.
 */
@FunctionalInterface
public interface TriFunction<T, U, V, W> {
    W consume(T t, U u, V v);
}
