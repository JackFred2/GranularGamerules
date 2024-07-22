package red.jackf.granulargamerules.impl.rules;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.gamerule.v1.rule.EnumRule;
import net.minecraft.world.level.GameRules;
import red.jackf.granulargamerules.impl.GranularGamerules;
import red.jackf.granulargamerules.impl.rules.types.DeferBoolean;

public abstract class DeferredRule<T> {
    private DeferredRule() {}

    public abstract T get(GameRules rules);

    public static DeferredRule<Boolean> createBoolean(GameRules.Key<GameRules.BooleanValue> parent, String id) {
        String fullId = parent.getId() + "/" + id;
        GameRules.Key<EnumRule<DeferBoolean>> registered = GameRuleRegistry.register(fullId, parent.getCategory(), GameRuleFactory.createEnumRule(DeferBoolean.DEFER));
        GranularGamerules.getChildGamerules().put(parent, registered);
        return new DeferredBooleanRule(parent, registered);
    }

    public static class DeferredBooleanRule extends DeferredRule<Boolean> {
        private final GameRules.Key<GameRules.BooleanValue> parent;
        private final GameRules.Key<EnumRule<DeferBoolean>> key;

        public DeferredBooleanRule(GameRules.Key<GameRules.BooleanValue> parent, GameRules.Key<EnumRule<DeferBoolean>> key) {
            this.parent = parent;
            this.key = key;
        }

        public Boolean get(GameRules rules) {
            return switch (rules.getRule(key).get()) {
                case TRUE -> true;
                case FALSE -> false;
                case DEFER -> rules.getBoolean(parent);
            };
        }
    }
}
