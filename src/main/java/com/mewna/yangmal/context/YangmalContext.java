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
    
    public <T> Context param(final String key, final T value) {
        if(!acceptingParams) {
            throw new IllegalStateException("Attempted to add params to YangmalContext when not accepting params!");
        }
        params.put(key, value);
        return this;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T param(final String key) {
        return (T) params.get(key);
    }
    
    @Override
    public String prefix() {
        return prefix;
    }
    
    @Override
    public void prefix(final String prefix) {
        if(!populating) {
            throw new IllegalStateException("Not populating!");
        }
        this.prefix = prefix;
    }
    
    @Override
    public void name(final String name) {
        this.name = name;
    }
    
    @Override
    public String name() {
        return name;
    }
    
    @Override
    public Collection<Arg> args() {
        return args;
    }
    
    @Override
    public void args(final List<Arg> args) {
        if(!populating) {
            throw new IllegalStateException("Not populating!");
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
            throw new IllegalStateException("Not populating!");
        }
        this.argstr = argstr;
    }
    
    @Nonnull
    @Override
    public Iterator<Arg> iterator() {
        // TODO
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean hasNext() {
        // TODO
        return false;
    }
    
    @Override
    public Arg next() {
        // TODO
        return null;
    }
    
    @Override
    public Arg peek() {
        // TODO
        return null;
    }
}
