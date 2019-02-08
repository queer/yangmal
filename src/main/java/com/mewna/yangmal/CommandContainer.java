package com.mewna.yangmal;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.mewna.yangmal.context.Context;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;

/**
 * @author amy
 * @since 2/7/19.
 */
@Immutable
@Style(typeImmutable = "Yangmal*")
public abstract class CommandContainer {
    public abstract Object object();
    
    public abstract MethodAccess access();
    
    public abstract int index();
    
    void invoke(final Context ctx) {
        access().invoke(object(), index(), ctx);
    }
}
