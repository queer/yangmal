package com.mewna.yangmal.context;

import java.util.List;

/**
 * @author amy
 * @since 2/8/19.
 */
public interface PopulatableContext extends EditableContext {
    void prefix(String prefix);
    
    void name(String name);
    
    void args(List<Arg> args);
    
    void argstr(String argstr);
}
