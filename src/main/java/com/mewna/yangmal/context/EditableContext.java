package com.mewna.yangmal.context;

/**
 * @author amy
 * @since 2/8/19.
 */
public interface EditableContext extends Context {
    <T> Context param(final String key, final T value);
}
