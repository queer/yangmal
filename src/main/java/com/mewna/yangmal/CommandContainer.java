package com.mewna.yangmal;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.mewna.yangmal.context.Context;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;

import java.lang.reflect.Method;

/**
 * @author amy
 * @since 2/7/19.
 */
@Immutable
@Style(typeImmutable = "Yangmal*")
public abstract class CommandContainer {
    public abstract Yangmal yangmal();
    
    public abstract Object object();
    
    public abstract Method method();
    
    public abstract MethodAccess access();
    
    public abstract int index();
    
    public abstract String[] names();
    
    public abstract String description();
    
    public abstract String[] usage();
    
    public abstract String[] examples();
    
    void invoke(final Context ctx) {
        try {
            access().invoke(object(), index(), ctx);
        } catch(final Exception e) {
            e.printStackTrace();
        }
    }
}
