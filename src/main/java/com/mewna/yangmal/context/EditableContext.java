package com.mewna.yangmal.context;

import javax.annotation.Nonnull;

/**
 * @author amy
 * @since 2/8/19.
 */
public interface EditableContext extends Context {
    @Nonnull
    <T> EditableContext param(final String key, final T value);
}
