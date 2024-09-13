package red.jackf.granulargamerules.impl.dedicated;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import red.jackf.granulargamerules.impl.GGCommand;
import red.jackf.granulargamerules.impl.config.GGConfig;
import red.jackf.granulargamerules.mixins.dedicated.DedicatedServerAccessor;
import red.jackf.granulargamerules.mixins.dedicated.SettingsAccessor;

import java.util.Properties;
import java.util.function.Consumer;

public class GranularGamerulesDedicated implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        if (GGConfig.INSTANCE.instance().enableFakeServerRules) {
            CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
                if (environment.includeDedicated) {
                    dispatcher.register(GGCommand.makeFakeGameruleRoot()
                            .then(Commands.literal("pvp")
                                    .then(Commands.literal("true")
                                            .executes(ctx -> GranularGamerulesDedicated.setPvp(ctx, true)))
                                    .then(Commands.literal("false")
                                            .executes(ctx -> GranularGamerulesDedicated.setPvp(ctx, false)))
                                    .executes(GranularGamerulesDedicated::queryPvp)
                            ).then(Commands.literal("allowFlight")
                                    .then(Commands.literal("true")
                                            .executes(ctx -> GranularGamerulesDedicated.setAllowFlight(ctx, true)))
                                    .then(Commands.literal("false")
                                            .executes(ctx -> GranularGamerulesDedicated.setAllowFlight(ctx, false)))
                                    .executes(GranularGamerulesDedicated::queryAllowFlight)
                            ).then(Commands.literal("enableCommandBlock")
                                    .then(Commands.literal("true")
                                            .executes(ctx -> GranularGamerulesDedicated.setEnableCommandBlock(ctx, true)))
                                    .then(Commands.literal("false")
                                            .executes(ctx -> GranularGamerulesDedicated.setEnableCommandBlock(ctx, false)))
                                    .executes(GranularGamerulesDedicated::queryEnableCommandBlock)
                            )
                    );
                }
            });
        }
    }

    private static boolean doWork(MinecraftServer server, Consumer<Properties> actor) {
        if (server instanceof DedicatedServerAccessor accessed) {
            accessed.getSettings().update(prop -> {
                //noinspection unchecked
                SettingsAccessor<DedicatedServerProperties> cast = ((SettingsAccessor<DedicatedServerProperties>) prop);
                Properties copy = cast.invokeCloneProperties();
                actor.accept(copy);
                return cast.invokeReload(server.registryAccess(), copy);
            });

            return true;
        } else {
            return false;
        }
    }

    private static int queryPvp(CommandContext<CommandSourceStack> ctx) {
        boolean pvpAllowed = ctx.getSource().getServer().isPvpAllowed();
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.gamerule.query", "pvp", Boolean.toString(pvpAllowed)), false);
        return pvpAllowed ? 1 : 0;
    }

    private static int setPvp(CommandContext<CommandSourceStack> ctx, boolean newPvpAllowed) {
        MinecraftServer server = ctx.getSource().getServer();

        doWork(server, prop -> {
            server.setPvpAllowed(newPvpAllowed);
            prop.setProperty("pvp", Boolean.toString(newPvpAllowed));
        });

        ctx.getSource().sendSuccess(() -> Component.translatable("commands.gamerule.set", "pvp", Boolean.toString(newPvpAllowed)), true);

        return server.isPvpAllowed() ? 1 : 0;
    }

    private static int queryAllowFlight(CommandContext<CommandSourceStack> ctx) {
        boolean flightAllowed = ctx.getSource().getServer().isFlightAllowed();
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.gamerule.query", "allowFlight", Boolean.toString(flightAllowed)), false);
        return flightAllowed ? 1 : 0;
    }

    private static int setAllowFlight(CommandContext<CommandSourceStack> ctx, boolean newFlightAllowed) {
        MinecraftServer server = ctx.getSource().getServer();

        doWork(server, prop -> {
            server.setFlightAllowed(newFlightAllowed);
            prop.setProperty("allow-flight", Boolean.toString(newFlightAllowed));
        });

        ctx.getSource().sendSuccess(() -> Component.translatable("commands.gamerule.set", "allowFlight", Boolean.toString(newFlightAllowed)), true);

        return server.isFlightAllowed() ? 1 : 0;
    }

    private static int queryEnableCommandBlock(CommandContext<CommandSourceStack> ctx) {
        boolean commandBlocksAllowed = ctx.getSource().getServer().isCommandBlockEnabled();
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.gamerule.query", "enableCommandBlock", Boolean.toString(commandBlocksAllowed)), false);
        return commandBlocksAllowed ? 1 : 0;
    }

    private static int setEnableCommandBlock(CommandContext<CommandSourceStack> ctx, boolean newEnableCommandBlock) {
        MinecraftServer server = ctx.getSource().getServer();

        doWork(server, prop -> prop.setProperty("enable-command-block", Boolean.toString(newEnableCommandBlock)));

        ctx.getSource().sendSuccess(() -> Component.translatable("commands.gamerule.set", "enableCommandBlock", Boolean.toString(newEnableCommandBlock)), true);

        return server.isCommandBlockEnabled() ? 1 : 0;
    }
}
