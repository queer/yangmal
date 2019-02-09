package com.mewna.yangmal.context;

import com.mewna.yangmal.Yangmal;
import com.mewna.yangmal.function.AsyncBiFunction;
import com.mewna.yangmal.util.Result;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;

import java.util.concurrent.CompletableFuture;

/**
 * @author amy
 * @since 2/8/19.
 */
@Immutable
@Style(typeImmutable = "Yangmal*")
public interface Arg {
    Yangmal yangmal();
    
    Context ctx();
    
    String val();
    
    default <T> Result<T, Throwable> as(final Class<T> as) {
        return cast(as);
    }
    
    default <T> Result<T, Throwable> cast(final Class<T> as) {
        return castAsync(as).join();
    }
    
    @SuppressWarnings("unchecked")
    default <T> CompletableFuture<Result<T, Throwable>> castAsync(final Class<T> as) {
        return ((AsyncBiFunction<Context, Arg, Result<T, Throwable>>) yangmal().typeConverters().get(as))
                .apply(ctx(), this);
    }
    
    static Arg create(final Yangmal yangmal, final Context ctx, final String string) {
        return YangmalArg.builder()
                .yangmal(yangmal)
                .ctx(ctx)
                .val(string)
                .build();
    }
}
