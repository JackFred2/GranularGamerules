package red.jackf.granulargamerules.impl.rules;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.level.GameRules;
import red.jackf.granulargamerules.impl.GranularGamerules;

public class MobGriefingRules {
    public static final GameRules.Key<GameRules.BooleanValue> ENDERMEN_MOVE_BLOCKS = create("endermenMoveBlocks", true);

    public static void setup() {
        // no-op
    }

    private static GameRules.Key<GameRules.BooleanValue> create(String name, boolean defaultValue) {
        var parent = GameRules.RULE_MOBGRIEFING;

        String ruleId = parent.getId() + "/" + name;
        GameRules.Key<GameRules.BooleanValue> rule = GameRuleRegistry.register(ruleId, parent.getCategory(), GameRuleFactory.createBooleanRule(defaultValue));
        GranularGamerules.add(rule, parent);

        return rule;
    }
}
