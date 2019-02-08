package com.mewna.yangmal.context;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author amy
 * @since 2/7/19.
 */
public interface Context extends Iterable<Arg>, Iterator<Arg> {
    /**
     * Get the context param for the given key.
     *
     * @param key The key to fetch for.
     * @param <T> The type of the key stored at the param.
     *
     * @return The value, or {@code null} if no value is present.
     */
    @Nullable
    <T> T param(@Nonnull final String key);
    
    /**
     * @return The prefix used to invoke this command.
     */
    @Nonnull
    String prefix();
    
    /**
     * @return The name of the command that was invoked.
     */
    @Nonnull
    String name();
    
    /**
     * @return The arguments passed to this command. May be empty.
     */
    @Nonnull
    Collection<Arg> args();
    
    /**
     * Peek at the next argument without consuming it. Use {@link #next()} if
     * you want to consume it.
     * @return The next argument, or {@code null} if none exists.
     */
    @Nullable
    Arg peek();
    
    /**
     * Reset the context's iterable arguments so that they can be iterated again.
     */
    void reset();
    
    /**
     * @return The argument string that was passed to this command.
     */
    @Nullable
    String argstr();
}
