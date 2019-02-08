package com.mewna.yangmal.function;

import java.util.concurrent.CompletableFuture;

/**
 * @author amy
 * @since 2/7/19.
 */
@FunctionalInterface
public interface AsyncBiConsumer<T, U> {
    CompletableFuture<Void> consume(T val, U val2);
}
