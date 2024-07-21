package red.jackf.granulargamerules.impl.rules;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.rule.EnumRule;
import net.minecraft.world.level.GameRules;
import red.jackf.granulargamerules.impl.GranularGamerules;
import red.jackf.granulargamerules.impl.rules.types.DeferBoolean;

public class MobGriefing {
    public static final GameRules.Key<EnumRule<DeferBoolean>> CREEPER_EXPLOSIONS
            = GranularGamerules.register(GameRules.RULE_MOBGRIEFING, "creeperExplosions", GameRuleFactory.createEnumRule(DeferBoolean.DEFER));
    public static final GameRules.Key<EnumRule<DeferBoolean>> ENDERMEN_MOVE_BLOCKS
            = GranularGamerules.register(GameRules.RULE_MOBGRIEFING, "endermenMoveBlocks", GameRuleFactory.createEnumRule(DeferBoolean.DEFER));

    public static void doRegister() {
        // no-op
    }
}
