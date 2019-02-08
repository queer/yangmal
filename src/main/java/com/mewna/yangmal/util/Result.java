package com.mewna.yangmal.util;

import org.derive4j.ArgOption;
import org.derive4j.Data;

import java.util.function.Function;

/**
 * Based off of https://nbsoftsolutions.com/blog/a-java-result-algebraic-data-type-worth-using
 *
 * @author amy
 * @since 2/8/19.
 */
@Data(arguments = ArgOption.checkedNotNull)
public abstract class Result<V, E> {
    public abstract <T> T either(Function<V, T> value, Function<E, T> error);
}