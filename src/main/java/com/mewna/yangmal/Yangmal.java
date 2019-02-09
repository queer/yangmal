package com.mewna.yangmal;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.mewna.catnip.entity.message.Message;
import com.mewna.catnip.extension.AbstractExtension;
import com.mewna.catnip.shard.DiscordEvent;
import com.mewna.yangmal.context.Arg;
import com.mewna.yangmal.context.Context;
import com.mewna.yangmal.context.EditableContext;
import com.mewna.yangmal.context.YangmalContext;
import com.mewna.yangmal.function.*;
import com.mewna.yangmal.util.Result;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author amy
 * @since 2/7/19.
 */
@SuppressWarnings("unused")
public final class Yangmal extends AbstractExtension {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private final Map<String, CommandContainer> commands = new HashMap<>();
    private final Map<Class<?>, AsyncBiFunction<Context, Arg, ? extends Result<?, Throwable>>> typeConverters = new ConcurrentHashMap<>();
    private final Map<Class<?>, Optional<?>> contextServices = new ConcurrentHashMap<>();
    
    private final Collection<AsyncBiConsumer<EditableContext, Message>> contextHooks = new ArrayList<>();
    private final Collection<AsyncBiPredicate<Context, Message>> commandChecks = new ArrayList<>();
    
    private Consumer<Throwable> errorHandler = e -> logger.warn("Encountered error during command processing:", e);
    private AsyncSupplier<Message, List<String>> prefixSupplier = __ -> CompletableFuture.completedFuture(Collections.singletonList("!"));
    private AsyncBiConsumer<String, Context> invalidCommandHandler = (__, ___) -> CompletableFuture.completedFuture(null);
    private AsyncConsumer<Message> notCommandHandler = __ -> CompletableFuture.completedFuture(null);
    private AsyncTriConsumer<Message, String, Context> checksFailedHandler = (__, ___, ____) -> CompletableFuture.completedFuture(null);
    
    public Yangmal() {
        super("yangmal");
    }
    
    @Nonnull
    public Yangmal setup() {
        try(final ScanResult res = new ClassGraph().enableAllInfo().scan()) {
            res.getClassesWithMethodAnnotation(Command.class.getName())
                    .stream().map(ClassInfo::loadClass).forEach(this::loadCommandsFromClass);
        }
        catnip().on(DiscordEvent.MESSAGE_CREATE, this::runCommand);
        return this;
    }
    
    @Nonnull
    public Yangmal addContextHook(@Nonnull final AsyncBiConsumer<EditableContext, Message> hook) {
        contextHooks.add(hook);
        return this;
    }
    
    @Nonnull
    public Yangmal addCommandCheck(@Nonnull final AsyncBiPredicate<Context, Message> check) {
        commandChecks.add(check);
        return this;
    }
    
    @Nonnull
    public <T> Yangmal registerTypeConverter(@Nonnull final Class<T> type,
                                             @Nonnull final AsyncBiFunction<Context, Arg,
                                                     ? extends Result<?, Throwable>> converter) {
        typeConverters.put(type, converter);
        return this;
    }
    
    @Nonnull
    public <T> Yangmal registerContextService(@Nonnull final Class<T> type, @Nonnull final T service) {
        contextServices.put(type, Optional.of(service));
        return this;
    }
    
    @Nonnull
    public Yangmal errorHandler(@Nonnull final Consumer<Throwable> handler) {
        errorHandler = handler;
        return this;
    }
    
    @Nonnull
    public Yangmal prefixSupplier(@Nonnull final AsyncSupplier<Message, List<String>> supplier) {
        prefixSupplier = supplier;
        return this;
    }
    
    @Nonnull
    public Yangmal constantPrefix(@Nonnull final String prefix) {
        prefixSupplier = AsyncSupplier.constant(Collections.singletonList(prefix));
        return this;
    }
    
    @Nonnull
    public Yangmal invalidCommandHandler(@Nonnull final AsyncBiConsumer<String, Context> handler) {
        invalidCommandHandler = handler;
        return this;
    }
    
    @Nonnull
    public Yangmal notCommandHandler(@Nonnull final AsyncConsumer<Message> handler) {
        notCommandHandler = handler;
        return this;
    }
    
    @Nonnull
    public Yangmal checksFailedHandler(@Nonnull final AsyncTriConsumer<Message, String, Context> handler) {
        checksFailedHandler = handler;
        return this;
    }
    
    @Nonnull
    public Map<Class<?>, AsyncBiFunction<Context, Arg, ? extends Result<?, Throwable>>> typeConverters() {
        return typeConverters;
    }
    
    public void runCommand(@Nonnull final Message source) {
        prefixSupplier.supply(source).thenAccept(prefixes -> {
            // Test for prefixes
            String prefix = null;
            for(final String s : prefixes) {
                if(source.content().startsWith(s)) {
                    prefix = s;
                    break;
                }
            }
            if(prefix != null) {
                // Have a valid command, process hooks
                final YangmalContext ctx = new YangmalContext();
                ctx.startAcceptingParams();
                // Have to do a copy to make it effectively final :K
                final String argstr = source.content().substring(prefix.length());
                if(!argstr.isEmpty()) {
                    // If the argstr isn't empty after removing the prefix, then we must have at least a name
                    final String finalPrefix = prefix;
                    CompletableFuture.allOf(contextHooks.stream().map(e -> e.consume(ctx, source)).toArray(CompletableFuture[]::new))
                            .thenAccept(__ -> {
                                ctx.stopAcceptingParams();
                                ctx.startPopulating();
                                ctx.services(contextServices);
                                
                                // Populate prefix, args, ...
                                ctx.prefix(finalPrefix);
                                
                                final var nameArgSplit = argstr.split("\\s+", 2);
                                final var name = nameArgSplit[0];
                                if(nameArgSplit.length > 1) {
                                    ctx.argstr(nameArgSplit[1]);
                                    ctx.args(Arrays.stream(nameArgSplit[1].split("\\s+", 2))
                                            .map(e -> Arg.create(this, e))
                                            .collect(Collectors.toList()));
                                } else {
                                    ctx.argstr(null);
                                    ctx.args(Collections.emptyList());
                                }
                                
                                ctx.stopPopulating();
                                
                                // Check if we can run the command
                                final List<CompletableFuture<Boolean>> checkFutures = commandChecks.stream()
                                        .map(e -> e.test(ctx, source)).collect(Collectors.toList());
                                CompletableFuture.allOf(checkFutures.toArray(new CompletableFuture[0])).thenAccept(___ -> {
                                    if(checkFutures.stream().allMatch(e -> e.getNow(false))) {
                                        // All checks passed, execute!
                                        Optional.ofNullable(commands.get(ctx.name()))
                                                .ifPresentOrElse(cmd -> cmd.invoke(ctx),
                                                        () -> invalidCommandHandler.consume(ctx.name(), ctx));
                                    } else {
                                        // Warn that checks failed
                                        checksFailedHandler.consume(source, ctx.name(), ctx);
                                    }
                                });
                            })
                            .exceptionally(e -> {
                                errorHandler.accept(e);
                                return null;
                            });
                } else {
                    // Not a command (nothing after prefix)
                    notCommandHandler.consume(source);
                }
            } else {
                // Not a command (no prefix)
                notCommandHandler.consume(source);
            }
        });
    }
    
    private void loadCommandsFromClass(@Nonnull final Class<?> cls) {
        try {
            final Object instance = cls.getDeclaredConstructor().newInstance();
            Arrays.stream(cls.getDeclaredMethods())
                    .filter(e -> e.isAnnotationPresent(Command.class))
                    .filter(e -> Modifier.isPublic(e.getModifiers()))
                    .forEach(method -> {
                        if(method.getParameterCount() != 1 || !method.getParameters()[0].getType().equals(Context.class)) {
                            logger.error("Method {}#{} is invalid: doesn't take exactly 1 Context as its parameters.",
                                    cls.getName(), method.getName());
                        } else {
                            final MethodAccess access = MethodAccess.get(cls);
                            final int index = access.getIndex(method.getName());
                            final Command cmd = method.getDeclaredAnnotation(Command.class);
                            for(final String name : cmd.names()) {
                                // We ignore this because generated sources and IntelliJ are apparently
                                // a giant meme :K
                                //noinspection UnnecessaryFullyQualifiedName
                                commands.put(name, com.mewna.yangmal.YangmalCommandContainer.builder()
                                        .object(instance)
                                        .access(access)
                                        .index(index)
                                        .build());
                            }
                        }
                    });
        } catch(final InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            logger.warn("yangmal couldn't load commands from class {}: Instantiation failed", cls.getName(), e);
        }
    }
}
