package red.jackf.granulargamerules.mixins.miscrules.enablethunder;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.storage.ServerLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerLevel.class)
public interface ServerLevelAccessor {
    @Accessor("THUNDER_DELAY")
    @Final
    IntProvider getThunderDelay();

    @Accessor
    ServerLevelData getServerLevelData();
}
