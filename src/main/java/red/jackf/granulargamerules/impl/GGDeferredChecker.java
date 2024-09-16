package red.jackf.granulargamerules.impl;

import net.fabricmc.fabric.api.gamerule.v1.rule.DoubleRule;
import net.fabricmc.fabric.api.gamerule.v1.rule.EnumRule;
import net.minecraft.world.level.GameRules;
import red.jackf.granulargamerules.impl.mixinutil.GGGameRules;

import java.util.Optional;
import java.util.function.Function;

/**
 * Empty optional -> deferred
 * Otherwise, value of rule
 */
public class GGDeferredChecker {
    public static Optional<Boolean> getBoolean(GameRules rules, GameRules.Key<GameRules.BooleanValue> key) {
        return get(rules, key, rules::getBoolean);
    }

    public static Optional<Integer> getInt(GameRules rules, GameRules.Key<GameRules.IntegerValue> key) {
        return get(rules, key, rules::getInt);
    }

    public static Optional<Double> getDouble(GameRules rules, GameRules.Key<DoubleRule> key) {
        return get(rules, key, key2 -> rules.getRule(key2).get());
    }

    public static <E extends Enum<E>> Optional<E> getEnum(GameRules rules, GameRules.Key<EnumRule<E>> key) {
        return get(rules, key, key2 -> rules.getRule(key2).get());
    }

    private static <T extends GameRules.Value<T>, A> Optional<A> get(GameRules rules, GameRules.Key<T> key, Function<GameRules.Key<T>, A> getter) {
        boolean isDeferred = ((GGGameRules) rules).gg$isDeferred(key);
        if (isDeferred) {
            return Optional.empty();
        } else {
            return Optional.of(getter.apply(key));
        }
    }
}