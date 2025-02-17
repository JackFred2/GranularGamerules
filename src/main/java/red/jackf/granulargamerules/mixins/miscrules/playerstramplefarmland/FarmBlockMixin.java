package red.jackf.granulargamerules.mixins.miscrules.playerstramplefarmland;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import red.jackf.granulargamerules.impl.rules.MiscRules;

@Mixin(FarmBlock.class)
public class FarmBlockMixin {
    @WrapWithCondition(method = "fallOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/FarmBlock;turnToDirt(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V"))
    private boolean checkGGRule(Entity entity, BlockState state, Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel) || !(entity instanceof Player)) return true;
        return serverLevel.getGameRules().getBoolean(MiscRules.PLAYERS_TRAMPLE_FARMLAND);
    }
}
