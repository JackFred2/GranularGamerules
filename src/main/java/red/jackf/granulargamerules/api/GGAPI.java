package red.jackf.granulargamerules.api;

import net.fabricmc.fabric.api.gamerule.v1.rule.DoubleRule;
import net.fabricmc.fabric.api.gamerule.v1.rule.EnumRule;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import red.jackf.granulargamerules.impl.GranularGamerules;
import red.jackf.granulargamerules.impl.mixinutil.GGGameRules;

import java.util.function.BiFunction;

/**
 * Used to grab GameRules in a way that respects parent rules.
 */
public class GGAPI {

    public static boolean getBoolean(MinecraftServer server, GameRules.Key<GameRules.BooleanValue> key) {
        return get(server, key, GameRules::getBoolean);
    }

    public static int getInt(MinecraftServer server, GameRules.Key<GameRules.IntegerValue> key) {
        return get(server, key, GameRules::getInt);
    }

    public static double getDouble(MinecraftServer server, GameRules.Key<DoubleRule> key) {
        return get(server, key, (rules, key2) -> rules.getRule(key2).get());
    }

    public static <E extends Enum<E>> E getEnum(MinecraftServer server, GameRules.Key<EnumRule<E>> key) {
        return get(server, key, (rules, key2) -> rules.getRule(key2).get());
    }

    private static <T extends GameRules.Value<T>, A> A get(MinecraftServer server, GameRules.Key<T> key, BiFunction<GameRules, GameRules.Key<T>, A> getter) {
        GameRules rules = server.getGameRules();
        boolean isDeferred = ((GGGameRules) rules).gg$isDeferred(key);
        return getter.apply(rules, isDeferred ? GranularGamerules.getParentRule(key).orElse(key) : key);
    }
}