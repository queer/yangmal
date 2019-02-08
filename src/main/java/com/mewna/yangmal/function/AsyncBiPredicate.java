package com.mewna.yangmal.function;

import java.util.concurrent.CompletableFuture;

/**
 * @author amy
 * @since 2/7/19.
 */
@FunctionalInterface
public interface AsyncBiPredicate<T, U> {
    CompletableFuture<Boolean> test(T val, U val2);
}
