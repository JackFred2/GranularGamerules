package red.jackf.granulargamerules.mixins.miscrules.sleepcountsbelowsurface;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.SleepStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;
import red.jackf.granulargamerules.impl.rules.MiscRules;

@Mixin(SleepStatus.class)
public class SleepStatusMixin {

    @WrapWithCondition(method = "update",
            at = @At(value = "FIELD", target = "Lnet/minecraft/server/players/SleepStatus;activePlayers:I", ordinal = 1),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isSpectator()Z"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isSleeping()Z")
            ))
    private boolean dontCountPlayersBelowSurface(SleepStatus instance, int activePlayers, @Local ServerPlayer player) {
        return player.level().getGameRules().getBoolean(MiscRules.COUNT_UNDERGROUND) || !MiscRules.isBelowSurface(player);
    }
}
