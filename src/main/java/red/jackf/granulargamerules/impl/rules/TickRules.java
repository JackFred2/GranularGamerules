package red.jackf.granulargamerules.impl.rules;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.minecraft.world.level.GameRules;
import org.joml.Math;

public class TickRules {
    public static final GameRules.Key<GameRules.IntegerValue> EXTRA_FARMLAND_TICKS = create("extraFarmlandTicks");
    public static final GameRules.Key<GameRules.IntegerValue> EXTRA_LEAF_TICKS = create("extraLeafTicks");

    private static GameRules.Key<GameRules.IntegerValue> create(String name) {
        return Utils.createChild(GameRules.RULE_RANDOMTICKING, name, GameRuleFactory.createIntRule(0));
    }

    public static void setup() {
        // no-op
    }

    public static int getMaxExtraTicks(GameRules gameRules) {
        return max(
                gameRules.getInt(EXTRA_FARMLAND_TICKS),
                gameRules.getInt(EXTRA_LEAF_TICKS)
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
