package red.jackf.granulargamerules.api;

import net.fabricmc.fabric.api.gamerule.v1.rule.DoubleRule;
import net.fabricmc.fabric.api.gamerule.v1.rule.EnumRule;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import red.jackf.granulargamerules.impl.GranularGamerules;
import red.jackf.granulargamerules.impl.mixinutil.GGGameRules;

import java.util.function.Function;

/**
 * Used to grab GameRules in a way that respects parent rules.
 */
public class GGAPI {

    public static boolean getBoolean(MinecraftServer server, GameRules.Key<GameRules.BooleanValue> key) {
        return getBoolean(server.getGameRules(), key);
    }

    public static boolean getBoolean(GameRules rules, GameRules.Key<GameRules.BooleanValue> key) {
        return get(rules, key, rules::getBoolean);
    }

    public static int getInt(MinecraftServer server, GameRules.Key<GameRules.IntegerValue> key) {
        return getInt(server.getGameRules(), key);
    }

    public static int getInt(GameRules rules, GameRules.Key<GameRules.IntegerValue> key) {
        return get(rules, key, rules::getInt);
    }

    public static double getDouble(MinecraftServer server, GameRules.Key<DoubleRule> key) {
        return getDouble(server.getGameRules(), key);
    }

    public static double getDouble(GameRules rules, GameRules.Key<DoubleRule> key) {
        return get(rules, key, key2 -> rules.getRule(key2).get());
    }

    public static <E extends Enum<E>> E getEnum(MinecraftServer server, GameRules.Key<EnumRule<E>> key) {
        return getEnum(server.getGameRules(), key);
    }

    public static <E extends Enum<E>> E getEnum(GameRules rules, GameRules.Key<EnumRule<E>> key) {
        return get(rules, key, key2 -> rules.getRule(key2).get());
    }

    private static <T extends GameRules.Value<T>, A> A get(GameRules rules, GameRules.Key<T> key, Function<GameRules.Key<T>, A> getter) {
        boolean isDeferred = ((GGGameRules) rules).gg$isDeferred(key);
        return getter.apply(isDeferred ? GranularGamerules.getParentRule(key).orElse(key) : key);
    }
}