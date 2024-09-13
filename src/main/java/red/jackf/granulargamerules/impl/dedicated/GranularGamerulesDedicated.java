package red.jackf.granulargamerules.impl.dedicated;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import red.jackf.granulargamerules.impl.GGCommand;
import red.jackf.granulargamerules.impl.config.GGConfig;
import red.jackf.granulargamerules.mixins.dedicated.DedicatedServerAccessor;
import red.jackf.granulargamerules.mixins.dedicated.PlayerListAccessor;
import red.jackf.granulargamerules.mixins.dedicated.SettingsAccessor;

import java.util.Properties;
import java.util.function.Consumer;

public class GranularGamerulesDedicated implements DedicatedServerModInitializer {
    private static final Style RULE_NAME_STYLE = Style.EMPTY.withColor(ChatFormatting.YELLOW);

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
                            ).then(Commands.literal("entityBroadcastRangePercentage")
                                    // capped during load 10 - 1000
                                    .then(Commands.argument("percentage", IntegerArgumentType.integer(10, 1000))
                                            .executes(GranularGamerulesDedicated::setEntityBroadcastRangePercentage))
                                    .executes(GranularGamerulesDedicated::queryEntityBroadcastRangePercentage)
                            ).then(Commands.literal("maxPlayers")
                                    // not actually capped
                                    .then(Commands.argument("max", IntegerArgumentType.integer(0, 128))
                                            .executes(GranularGamerulesDedicated::setMaxPlayers))
                                    .executes(GranularGamerulesDedicated::queryMaxPlayers)
                            ).then(Commands.literal("simulationDistance")
                                    // not actually capped but we dont want to lag the server
                                    .then(Commands.argument("radiusChunks", IntegerArgumentType.integer(2, 32))
                                            .executes(GranularGamerulesDedicated::setSimulationDistance))
                                    .executes(GranularGamerulesDedicated::querySimulationDistance)
                            ).then(Commands.literal("spawnProtection")
                                    .then(Commands.argument("radius", IntegerArgumentType.integer(0))
                                            .executes(GranularGamerulesDedicated::setSpawnProtection))
                                    .executes(GranularGamerulesDedicated::querySpawnProtection)
                            ).then(Commands.literal("viewDistance")
                                    // clamped at net.minecraft.server.level.ChunkMap#setServerViewDistance
                                    .then(Commands.argument("radiusChunks", IntegerArgumentType.integer(2, 32))
                                            .executes(GranularGamerulesDedicated::setViewDistance))
                                    .executes(GranularGamerulesDedicated::queryViewDistance)
                            )
                    );
                }
            });
        }
    }

    private static void doWork(MinecraftServer server, Consumer<Properties> actor) {
        if (server instanceof DedicatedServerAccessor accessed) {
            accessed.getSettings().update(prop -> {
                //noinspection unchecked
                SettingsAccessor<DedicatedServerProperties> cast = ((SettingsAccessor<DedicatedServerProperties>) prop);
                Properties copy = cast.invokeCloneProperties();
                actor.accept(copy);
                return cast.invokeReload(server.registryAccess(), copy);
            });
        }
    }

    private static void sendQuery(CommandContext<CommandSourceStack> ctx, String ruleName, String value) {
        Component formattedName = Component.literal(ruleName).withStyle(RULE_NAME_STYLE);
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.gamerule.query", formattedName, value), false);
    }

    private static void sendSet(CommandContext<CommandSourceStack> ctx, String ruleName, String value) {
        Component formattedName = Component.literal(ruleName).withStyle(RULE_NAME_STYLE);
        ctx.getSource().sendSuccess(() -> Component.translatable("commands.gamerule.set", formattedName, value), true);
    }

    private static int queryPvp(CommandContext<CommandSourceStack> ctx) {
        boolean pvpAllowed = ctx.getSource().getServer().isPvpAllowed();
        sendQuery(ctx, "pvp", Boolean.toString(pvpAllowed));
        return pvpAllowed ? 1 : 0;
    }

    private static int setPvp(CommandContext<CommandSourceStack> ctx, boolean newPvpAllowed) {
        MinecraftServer server = ctx.getSource().getServer();
        doWork(server, prop -> {
            server.setPvpAllowed(newPvpAllowed);
            prop.setProperty("pvp", Boolean.toString(newPvpAllowed));
        });
        sendSet(ctx, "pvp", Boolean.toString(newPvpAllowed));
        return server.isPvpAllowed() ? 1 : 0;
    }

    private static int queryAllowFlight(CommandContext<CommandSourceStack> ctx) {
        boolean flightAllowed = ctx.getSource().getServer().isFlightAllowed();
        sendQuery(ctx, "allowFlight", Boolean.toString(flightAllowed));
        return flightAllowed ? 1 : 0;
    }

    private static int setAllowFlight(CommandContext<CommandSourceStack> ctx, boolean newFlightAllowed) {
        MinecraftServer server = ctx.getSource().getServer();
        doWork(server, prop -> {
            server.setFlightAllowed(newFlightAllowed);
            prop.setProperty("allow-flight", Boolean.toString(newFlightAllowed));
        });
        sendSet(ctx, "allowFlight", Boolean.toString(newFlightAllowed));
        return server.isFlightAllowed() ? 1 : 0;
    }

    private static int queryEnableCommandBlock(CommandContext<CommandSourceStack> ctx) {
        boolean commandBlocksAllowed = ctx.getSource().getServer().isCommandBlockEnabled();
        sendQuery(ctx, "enableCommandBlock", Boolean.toString(commandBlocksAllowed));
        return commandBlocksAllowed ? 1 : 0;
    }

    private static int setEnableCommandBlock(CommandContext<CommandSourceStack> ctx, boolean newEnableCommandBlock) {
        MinecraftServer server = ctx.getSource().getServer();
        doWork(server, prop -> prop.setProperty("enable-command-block", Boolean.toString(newEnableCommandBlock)));
        sendSet(ctx, "enableCommandBlock", Boolean.toString(newEnableCommandBlock));
        return newEnableCommandBlock ? 1 : 0;
    }

    private static int querySpawnProtection(CommandContext<CommandSourceStack> ctx) {
        int spawnProtectionRadius = ctx.getSource().getServer().getSpawnProtectionRadius();
        sendQuery(ctx, "spawnProtection", Integer.toString(spawnProtectionRadius));
        return spawnProtectionRadius;
    }

    private static int setSpawnProtection(CommandContext<CommandSourceStack> ctx) {
        int radius = ctx.getArgument("radius", Integer.class);
        MinecraftServer server = ctx.getSource().getServer();
        doWork(server, prop -> prop.setProperty("spawn-protection", Integer.toString(radius)));
        sendSet(ctx, "spawnProtection", Integer.toString(radius));
        return radius;
    }

    private static int queryViewDistance(CommandContext<CommandSourceStack> ctx) {
        int viewDistanceChunks = ctx.getSource().getServer().getPlayerList().getViewDistance();
        sendQuery(ctx, "viewDistance", Integer.toString(viewDistanceChunks));
        return viewDistanceChunks;
    }

    private static int setViewDistance(CommandContext<CommandSourceStack> ctx) {
        int viewDistanceChunks = ctx.getArgument("radiusChunks", Integer.class);
        MinecraftServer server = ctx.getSource().getServer();
        doWork(server, prop -> {
            server.getPlayerList().setViewDistance(viewDistanceChunks);
            prop.setProperty("view-distance", Integer.toString(viewDistanceChunks));
        });
        sendSet(ctx, "viewDistance", Integer.toString(viewDistanceChunks));
        return viewDistanceChunks;
    }

    private static int querySimulationDistance(CommandContext<CommandSourceStack> ctx) {
        int simulationDistanceChunks = ctx.getSource().getServer().getPlayerList().getSimulationDistance();
        sendQuery(ctx, "simulationDistance", Integer.toString(simulationDistanceChunks));
        return simulationDistanceChunks;
    }

    private static int setSimulationDistance(CommandContext<CommandSourceStack> ctx) {
        int simulationDistanceChunks = ctx.getArgument("radiusChunks", Integer.class);
        MinecraftServer server = ctx.getSource().getServer();
        doWork(server, prop -> {
            server.getPlayerList().setSimulationDistance(simulationDistanceChunks);
            prop.setProperty("simulation-distance", Integer.toString(simulationDistanceChunks));
        });
        sendSet(ctx, "simulationDistance", Integer.toString(simulationDistanceChunks));
        return simulationDistanceChunks;
    }

    private static int queryMaxPlayers(CommandContext<CommandSourceStack> ctx) {
        int maxPlayers = ctx.getSource().getServer().getMaxPlayers();
        sendQuery(ctx, "maxPlayers", Integer.toString(maxPlayers));
        return maxPlayers;
    }

    private static int setMaxPlayers(CommandContext<CommandSourceStack> ctx) {
        int maxPlayers = ctx.getArgument("max", Integer.class);
        MinecraftServer server = ctx.getSource().getServer();
        doWork(server, prop -> {
            ((PlayerListAccessor) server.getPlayerList()).setMaxPlayers(maxPlayers);
            prop.setProperty("max-players", Integer.toString(maxPlayers));
        });
        sendSet(ctx, "maxPlayers", Integer.toString(maxPlayers));
        return maxPlayers;
    }

    private static int queryEntityBroadcastRangePercentage(CommandContext<CommandSourceStack> ctx) {
        int percentage = ((DedicatedServer) ctx.getSource().getServer()).getProperties().entityBroadcastRangePercentage;
        sendQuery(ctx, "entityBroadcastRangePercentage", Integer.toString(percentage));
        return percentage;
    }

    private static int setEntityBroadcastRangePercentage(CommandContext<CommandSourceStack> ctx) {
        int percentage = ctx.getArgument("percentage", Integer.class);
        MinecraftServer server = ctx.getSource().getServer();
        doWork(server, prop -> prop.setProperty("entity-broadcast-range-percentage", Integer.toString(percentage)));
        sendSet(ctx, "entityBroadcastRangePercentage", Integer.toString(percentage));
        return percentage;
    }
}
