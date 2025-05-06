package red.jackf.granulargamerules.mixins.miscrules.fluidsourceconversion;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import red.jackf.granulargamerules.impl.rules.MiscRules;

@Mixin(FlowingFluid.class)
public abstract class FlowingFluidMixin {

    @Shadow public abstract Fluid getSource();

    @ModifyExpressionValue(method = "getNewLiquid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FlowingFluid;canConvertToSource(Lnet/minecraft/world/level/Level;)Z"))
    private boolean checkGGRuleAndBiome(boolean original, Level level, BlockPos blockPos) {
        if (!original) return false;

        Fluid source = this.getSource();

        if (source == Fluids.WATER) {
            if (level.getGameRules().getBoolean(MiscRules.WATER_ONLY_IN_WET_BIOMES)) {
                return level.getBiome(blockPos).is(MiscRules.WATER_CONVERTIBLE);
            }
        } else if (source == Fluids.LAVA) {
            if (level.getGameRules().getBoolean(MiscRules.LAVA_ONLY_IN_NETHER_BIOMES)) {
                return level.getBiome(blockPos).is(MiscRules.LAVA_CONVERTIBLE);
            }
        }
        return true;
    }
}
