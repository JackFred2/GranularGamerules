package red.jackf.granulargamerules.impl.rules;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import org.joml.Math;
import red.jackf.granulargamerules.impl.GranularGamerules;

public class TickRules {
    public static final GameRules.Key<GameRules.IntegerValue> EXTRA_CROP_TICKS = create("extraCropTicks");
    public static final GameRules.Key<GameRules.IntegerValue> EXTRA_COPPER_TICKS = create("extraCopperTicks");
    public static final GameRules.Key<GameRules.IntegerValue> EXTRA_FARMLAND_TICKS = create("extraFarmlandTicks");
    public static final GameRules.Key<GameRules.IntegerValue> EXTRA_LEAF_TICKS = create("extraLeafTicks");
    public static final GameRules.Key<GameRules.IntegerValue> EXTRA_SAPLING_TICKS = create("extraSaplingTicks");
    public static final GameRules.Key<GameRules.IntegerValue> EXTRA_SPREADING_TERRAIN_TICKS = create("extraSpreadingTerrainTicks");

    public static final TagKey<Block> EXTRA_TICKABLE_CROPS_TAG = TagKey.create(Registries.BLOCK, GranularGamerules.id("extra_tickable_crops"));

    private static GameRules.Key<GameRules.IntegerValue> create(String name) {
        return Utils.createChild(GameRules.RULE_RANDOMTICKING, name, GameRuleFactory.createIntRule(0));
    }

    public static void setup() {
        // no-op
    }

    public static int getMaxExtraTicks(GameRules gameRules) {
        return max(
                gameRules.getInt(EXTRA_CROP_TICKS),
                gameRules.getInt(EXTRA_COPPER_TICKS),
                gameRules.getInt(EXTRA_FARMLAND_TICKS),
                gameRules.getInt(EXTRA_LEAF_TICKS),
                gameRules.getInt(EXTRA_SAPLING_TICKS),
                gameRules.getInt(EXTRA_SPREADING_TERRAIN_TICKS)
        );
    }

    private static int max(int... values) {
        int result = 0;

        for (int v : values) {
            result = Math.max(result, v);
        }

        return result;
    }
}
