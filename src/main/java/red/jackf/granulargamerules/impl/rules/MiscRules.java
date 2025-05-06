package red.jackf.granulargamerules.impl.rules;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.biome.Biome;
import red.jackf.granulargamerules.impl.GranularGamerules;
import red.jackf.granulargamerules.impl.mixinutil.GGServerPlayerSurfaceTracker;
import red.jackf.granulargamerules.mixins.miscrules.enablethunder.ServerLevelAccessor;

public class MiscRules {
    public static final int BLOCKS_BELOW_SEA_LEVEL = 10;
    public static final TagKey<Biome> WATER_CONVERTIBLE = TagKey.create(Registries.BIOME, GranularGamerules.id("water_convertible"));
    public static final TagKey<Biome> LAVA_CONVERTIBLE = TagKey.create(Registries.BIOME, GranularGamerules.id("lava_convertible"));

    public static final GameRules.Key<GameRules.BooleanValue> COUNT_UNDERGROUND
            = Utils.createChild(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE, "countUnderground", GameRuleFactory.createBooleanRule(true, MiscRules::onSleepBelowSurfaceUpdate));

    public static final GameRules.Key<GameRules.BooleanValue> ENABLE_THUNDER
            = Utils.createChild(GameRules.RULE_WEATHER_CYCLE, "enableThunder", GameRuleFactory.createBooleanRule(true, (server, newValue) -> {
                // disable thunder if turned off, prevents rain being stuck as thunder
                if (!newValue.get()) {
                    for (ServerLevel level : server.getAllLevels()) {
                        if (level.dimensionType().hasSkyLight()) {
                            ((ServerLevelAccessor) level).getServerLevelData().setThunderTime(ServerLevelAccessor.getThunderDelay().sample(level.random));
                            ((ServerLevelAccessor) level).getServerLevelData().setThundering(false);
                        }
                    }
                }
    }));

    public static final GameRules.Key<GameRules.BooleanValue> WATER_ONLY_IN_WET_BIOMES
            = Utils.createChild(GameRules.RULE_WATER_SOURCE_CONVERSION, "onlyInWetBiomes", GameRuleFactory.createBooleanRule(false));

    public static final GameRules.Key<GameRules.BooleanValue> LAVA_ONLY_IN_NETHER_BIOMES
            = Utils.createChild(GameRules.RULE_LAVA_SOURCE_CONVERSION, "onlyInNetherBiomes", GameRuleFactory.createBooleanRule(false));

    public static final GameRules.Key<GameRules.BooleanValue> PLAYERS_TRAMPLE_FARMLAND
            = GameRuleRegistry.register("playersTrampleFarmland", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(true));

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
