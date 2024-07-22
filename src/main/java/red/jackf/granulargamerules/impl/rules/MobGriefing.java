package red.jackf.granulargamerules.impl.rules;

import net.minecraft.world.level.GameRules;

public class MobGriefing {
    public static final DeferredRule<Boolean> CREEPER_EXPLOSIONS
            = DeferredRule.createBoolean(GameRules.RULE_MOBGRIEFING, "creeperExplosions");
    public static final DeferredRule<Boolean> ENDERMEN_MOVE_BLOCKS
            = DeferredRule.createBoolean(GameRules.RULE_MOBGRIEFING, "endermenMoveBlocks");

    public static void doRegister() {
        // no-op
    }
}
