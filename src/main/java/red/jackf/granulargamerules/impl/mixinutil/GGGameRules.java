package red.jackf.granulargamerules.impl.mixinutil;

import net.minecraft.world.level.GameRules;

public interface GGGameRules {
    void gg$setDeferred(GameRules.Key<?> key, boolean isDeferred);

    boolean gg$isDeferred(GameRules.Key<?> key);

    static <T extends GameRules.Value<T>> boolean isDeferred(GameRules rules, GameRules.Key<T> key) {
        return ((GGGameRules) rules).gg$isDeferred(key);
    }
}
