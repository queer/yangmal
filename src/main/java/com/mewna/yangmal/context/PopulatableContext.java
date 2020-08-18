package com.mewna.yangmal.context;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author amy
 * @since 2/8/19.
 */
public interface PopulatableContext extends EditableContext {
    void prefix(@Nonnull String prefix);
    
    void name(@Nonnull String name);
    
    void args(@Nonnull List<Arg> args);
    
    void argstr(@Nullable String argstr);
    
    void services(@Nonnull Map<Class<?>, Object> services);
}
