package com.mewna.yangmal.function;

import java.util.concurrent.CompletableFuture;

/**
 * @author amy
 * @since 2/8/19.
 */
@FunctionalInterface
public interface AsyncTriConsumer<T, U, V> {
    CompletableFuture<Void> consume(T t, U u, V v);
}
