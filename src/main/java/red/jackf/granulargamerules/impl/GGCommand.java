package red.jackf.granulargamerules.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import red.jackf.granulargamerules.impl.mixinutil.GGGameRules;
import red.jackf.jackfredlib.api.base.ServerTracker;

public class GGCommand {
    public static void setup() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            MinecraftServer server = ServerTracker.INSTANCE.getServer();
            if (server == null) return;

            server.getGameRules().visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
                @Override
                public <T extends GameRules.Value<T>> void visit(GameRules.Key<T> key, GameRules.Type<T> type) {
                    GGCommand.possiblyMakeDeferOption(dispatcher, key);
                }
            });
        });
    }

    public static LiteralArgumentBuilder<CommandSourceStack> makeFakeGameruleRoot() {
        return Commands.literal("gamerule")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(2));
    }

    private static <T extends GameRules.Value<T>> void possiblyMakeDeferOption(CommandDispatcher<CommandSourceStack> dispatcher, GameRules.Key<T> key) {
        var parent = GranularGamerules.getDeferredParent(key);

        if (parent.isPresent())
            dispatcher.register(makeFakeGameruleRoot()
                    .then(Commands.literal(key.getId())
                    .then(Commands.literal("defer")
                        .executes(ctx -> GGCommand.setDeferred(ctx, key)))));
    }

    private static <T extends GameRules.Value<T>> int setDeferred(CommandContext<CommandSourceStack> ctx, GameRules.Key<T> key) {
        ((GGGameRules) ctx.getSource().getServer().getGameRules()).gg$setDeferred(key, true);

        ctx.getSource().sendSuccess(() -> Component.translatable("commands.gamerule.set", key.getId(), Component.literal("defer")
                        .withStyle(ChatFormatting.YELLOW)), true);
        return 0;
    }
}
