# 양말

"양말" means "socks" in Korean. Love you @natanbc <33

양말 is a command library for catnip.

## Building

Just run `mvn clean package`.

## Usage

Default prefix is `!`.

```Java
final class README {
    private README() {}
    public static void main(final String[] args) {
        final Catnip catnip = Catnip.catnip("token");
        catnip.loadExtension(
            new Yangmal()
                // If you just want to always use the same prefix everywhere
                .constantPrefix("!!")
                // Alternatively, you can use the message to help determine the prefix.
                // Prefix suppliers are called asynchronously.
                .prefixSupplier(msg -> {
                    return database.getPrefixAsync(msg.guildId());
                })
                // Hooks populate the context with whatever data you think you'll need:
                // user, guild, member, channel, stuff from your database, ...
                // Hooks are called asynchronously.
                .addContextHook((ctx, msg) -> {
                    return database.getSomething(msg.author())
                            .thenAccept(thing -> ctx.param("thing", thing));
                })
                // Command checks are whether or not the command can be run. They get
                // whatever data you pass in the context, and determine whether the
                // command should be run or not
                // Checks are called asynchronously
                .addCommandCheck(ctx -> {
                    return someAsynchronousThing(ctx.param("thing"));
                })
                // Type converters
                .registerTypeConverter()
                // Handle errors during command parsing, ex. if you want errors to go
                // to your Sentry.io instance
                // You guessed it, asynchronous
                .errorHandler(e -> {
                    Sentry.capture(e);
                })
                // Handle commands that aren't found. Does nothing by default.
                // Guess what? Async!
                .invalidCommandHandler((name, ctx) -> {
                    logger.warn("Couldn't find command: {}", name);
                })
                // Handle messages that aren't commands
                .notCommandHandler(msg -> {
                    chatLevels.process(msg);
                })
                // Handled command checks that don't pass
                .checksFailedHandler((msg, name, ctx) -> {
                    logger.warn("Command checks failed for command {}", name);
                })
                // Finally, load all the commands and register catnip listeners
                .setup()
        );
    }
}
```
