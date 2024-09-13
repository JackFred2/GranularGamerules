package red.jackf.granulargamerules.impl.rules;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.level.GameRules;
import red.jackf.granulargamerules.impl.GranularGamerules;

public class MobGriefingRules {
    public static final GameRules.Key<GameRules.BooleanValue> ENDERMEN_MOVE_BLOCKS = create("endermenMoveBlocks");
    public static final GameRules.Key<GameRules.BooleanValue> MOBS_TRAMPLE_FARMLAND = create("mobsTrampleFarmland");
    public static final GameRules.Key<GameRules.BooleanValue> RAVAGERS_DESTROY_PLANTS = create("ravagersDestroyPlants");
    public static final GameRules.Key<GameRules.BooleanValue> SILVERFISH_ENTER_STONE = create("silverfishEnterStone");
    public static final GameRules.Key<GameRules.BooleanValue> SILVERFISH_WAKE_FRIENDS = create("silverfishWakeFriends");
    public static final GameRules.Key<GameRules.BooleanValue> ZOMBIES_BREAK_DOORS = create("zombiesBreakDoors");

    public static void setup() {
        // no-op
    }

    private static GameRules.Key<GameRules.BooleanValue> create(String name) {
        var parent = GameRules.RULE_MOBGRIEFING;
        var defaultValue = true;

        String ruleId = parent.getId() + "/" + name;
        GameRules.Key<GameRules.BooleanValue> rule = GameRuleRegistry.register(ruleId, parent.getCategory(), GameRuleFactory.createBooleanRule(defaultValue));
        GranularGamerules.add(rule, parent);

        return rule;
    }
}
