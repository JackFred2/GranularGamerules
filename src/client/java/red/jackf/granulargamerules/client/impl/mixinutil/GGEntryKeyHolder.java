package red.jackf.granulargamerules.client.impl.mixinutil;

import net.minecraft.world.level.GameRules;
import org.jetbrains.annotations.Nullable;

public interface GGEntryKeyHolder {
    void gg$setKey(GameRules.Key<?> key);

    @Nullable
    GameRules.Key<?> gg$getKey();
}
