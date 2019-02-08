package com.mewna.yangmal.function;

import java.util.concurrent.CompletableFuture;

/**
 * @author amy
 * @since 2/8/19.
 */
@FunctionalInterface
public interface AsyncSupplier<T, U> {
    CompletableFuture<U> supply(T data);
    
    static <T, U> AsyncSupplier<T, U> constant(final U val) {
        return __ -> CompletableFuture.completedFuture(val);
    }
}
