package red.jackf.granulargamerules.impl.rules;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import red.jackf.granulargamerules.impl.mixinutil.GGServerPlayerSurfaceTracker;

public class MiscRules {
    public static final int BLOCKS_BELOW_SEA_LEVEL = 10;

    public static final GameRules.Key<GameRules.BooleanValue> SLEEP_COUNTS_BELOW_SURFACE
            = GameRuleRegistry.register("playersSleepingCountsBelowSurface", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(true, MiscRules::onSleepBelowSurfaceUpdate));

    private static void onSleepBelowSurfaceUpdate(MinecraftServer server, GameRules.BooleanValue value) {
        for (ServerLevel level : server.getAllLevels()) {
            level.updateSleepingPlayerList();
        }
    }

    public static boolean isBelowSurface(ServerPlayer player) {
        return player.position().y() < (player.serverLevel().getSeaLevel() - BLOCKS_BELOW_SEA_LEVEL);
    }

    public static void setup() {
        ServerTickEvents.START_WORLD_TICK.register(level -> {
            boolean shouldUpdateSleepStatus = false;

            for (ServerPlayer player : level.players()) {
                shouldUpdateSleepStatus |= ((GGServerPlayerSurfaceTracker) player).gg$updateIsUnderSurface();
            }

            if (shouldUpdateSleepStatus) {
                level.updateSleepingPlayerList();
            }
        });
    }
}