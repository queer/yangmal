package com.mewna.yangmal.context;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author amy
 * @since 2/7/19.
 */
public class YangmalContext implements PopulatableContext {
    private final Map<String, Object> params = new ConcurrentHashMap<>();
    private final Collection<Arg> args = new ArrayList<>();
    private final List<Arg> consumableArgs = new ArrayList<>();
    private final Map<Class<?>, Optional<?>> services = new ConcurrentHashMap<>();
    private String prefix;
    private String name;
    private String argstr;
    
    private boolean acceptingParams;
    private boolean populating;
    
    public void startAcceptingParams() {
        acceptingParams = true;
    }
    
    public void stopAcceptingParams() {
        acceptingParams = false;
    }
    
    public void startPopulating() {
        populating = true;
    }
    
    public void stopPopulating() {
        populating = false;
    }
    
    @Nonnull
    public <T> EditableContext param(final String key, final T value) {
        if(!acceptingParams) {
            throw new IllegalStateException("Attempted to add params to YangmalContext when not accepting params!");
        }
        params.put(key, value);
        return this;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T param(@Nonnull final String key) {
        return (T) params.get(key);
    }
    
    @Nonnull
    @Override
    public String prefix() {
        return prefix;
    }
    
    @Override
    public void prefix(@Nonnull final String prefix) {
        if(!populating) {
            throw new IllegalStateException("Attempted to populate YangmalContext when not populating!");
        }
        this.prefix = prefix;
    }
    
    @Nonnull
    @Override
    public String name() {
        return name;
    }
    
    @Override
    public void name(@Nonnull final String name) {
        if(!populating) {
            throw new IllegalStateException("Attempted to populate YangmalContext when not populating!");
        }
        this.name = name;
    }
    
    @Nonnull
    @Override
    public Collection<Arg> args() {
        return List.copyOf(args);
    }
    
    @Override
    public void args(@Nonnull final List<Arg> args) {
        if(!populating) {
            throw new IllegalStateException("Attempted to populate YangmalContext when not populating!");
        }
        this.args.addAll(args);
    }
    
    @Override
    public String argstr() {
        return argstr;
    }
    
    @Override
    public void argstr(final String argstr) {
        if(!populating) {
            throw new IllegalStateException("Attempted to populate YangmalContext when not populating!");
        }
        this.argstr = argstr;
    }
    
    @Override
    public void services(@Nonnull final Map<Class<?>, Optional<?>> services) {
        if(!populating) {
            throw new IllegalStateException("Attempted to populate YangmalContext when not populating!");
        }
        this.services.clear();
        this.services.putAll(services);
    }
    
    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> service(@Nonnull final Class<T> cls) {
        return (Optional<T>) services.get(cls);
    }
    
    @Nonnull
    @Override
    public Iterator<Arg> iterator() {
        return new ContextIterator();
    }
    
    @Override
    public boolean hasNext() {
        return !consumableArgs.isEmpty();
    }
    
    @Override
    public Arg next() {
        if(consumableArgs.isEmpty()) {
            return null;
        }
        return consumableArgs.remove(0);
    }
    
    @Override
    public Arg peek() {
        if(consumableArgs.isEmpty()) {
            return null;
        }
        return consumableArgs.get(0);
    }
    
    @Override
    public void reset() {
        consumableArgs.clear();
        consumableArgs.addAll(args);
    }
    
    public final class ContextIterator implements Iterator<Arg> {
        @Override
        public boolean hasNext() {
            return !consumableArgs.isEmpty();
        }
        
        @Override
        public Arg next() {
            return consumableArgs.remove(0);
        }
    }
}
