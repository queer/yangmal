package com.mewna.yangmal.context;

import com.mewna.yangmal.Yangmal;
import com.mewna.yangmal.util.Result;
import io.reactivex.rxjava3.core.Single;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;

/**
 * @author amy
 * @since 2/8/19.
 */
@Immutable
@Style(typeImmutable = "Yangmal*")
public interface Arg {
    static Arg create(final Yangmal yangmal, final Context ctx, final String string) {
        return YangmalArg.builder()
                .yangmal(yangmal)
                .ctx(ctx)
                .val(string)
                .build();
    }
    
    Yangmal yangmal();
    
    Context ctx();
    
    String val();
    
    default <T> Result<T, Throwable> as(final Class<T> as) {
        return cast(as);
    }
    
    default <T> Result<T, Throwable> cast(final Class<T> as) {
        return castAsync(as).blockingGet();
    }
    
    @SuppressWarnings("unchecked")
    default <T> Single<? extends Result<T, Throwable>> castAsync(final Class<T> as) {
        final var converter = yangmal().typeConverters().get(as);
        final Single<? extends Result<?, Throwable>> converted = converter.apply(ctx(), this);
        return (Single<? extends Result<T, Throwable>>) converted;
    }
}
