package red.jackf.granulargamerules.mixins.miscrules.enablethunder;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.ServerLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import red.jackf.granulargamerules.impl.rules.MiscRules;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

    @WrapWithCondition(
            method = "advanceWeatherCycle",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/ServerLevelData;setThunderTime(I)V")
    )
    private boolean checkIfThunderDisabled(ServerLevelData instance, int ignore) {
        return instance.getGameRules().getBoolean(MiscRules.ENABLE_THUNDER);
    }

    @WrapWithCondition(
            method = "advanceWeatherCycle",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/ServerLevelData;setThundering(Z)V")
    )
    private boolean checkIfThunderDisabled(ServerLevelData instance, boolean ignore) {
        return instance.getGameRules().getBoolean(MiscRules.ENABLE_THUNDER);
    }
}
