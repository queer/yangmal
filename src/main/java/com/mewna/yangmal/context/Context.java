package com.mewna.yangmal.context;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author amy
 * @since 2/7/19.
 */
public interface Context extends Iterable<Arg>, Iterator<Arg> {
    <T> T param(final String key);
    
    String prefix();
    
    String name();
    
    Collection<Arg> args();
    
    Arg peek();
    
    String argstr();
}
