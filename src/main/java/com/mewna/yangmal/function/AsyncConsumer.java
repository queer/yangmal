package com.mewna.yangmal.function;

import java.util.concurrent.CompletableFuture;

/**
 * @author amy
 * @since 2/8/19.
 */
@FunctionalInterface
public interface AsyncConsumer<T> {
    CompletableFuture<Void> consume(T data);
}
