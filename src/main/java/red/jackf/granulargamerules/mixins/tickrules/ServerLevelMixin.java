package red.jackf.granulargamerules.mixins.tickrules;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import red.jackf.granulargamerules.impl.rules.TickRules;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {
    protected ServerLevelMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, RegistryAccess registryAccess, Holder<DimensionType> holder, boolean bl, boolean bl2, long l, int i) {
        super(writableLevelData, resourceKey, registryAccess, holder, bl, bl2, l, i);
    }

    @Shadow public abstract GameRules getGameRules();

    @WrapOperation(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;isRandomlyTicking()Z"))
    private boolean tickrules$captureLevelSection(LevelChunkSection instance, Operation<Boolean> original, @Share("chunkSection") LocalRef<LevelChunkSection> chunkSection) {
        chunkSection.set(instance);
        return original.call(instance);
    }

    @ModifyExpressionValue(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/SectionPos;sectionToBlockCoord(I)I"))
    private int tickrules$captureSubchunkY(int original, @Share("minChunkY") LocalIntRef minChunkY) {
        minChunkY.set(original);
        return original;
    }

    @Inject(method = "tickChunk", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/core/SectionPos;sectionToBlockCoord(I)I", shift = At.Shift.AFTER))
    private void tickrules$runExtraTicks(LevelChunk chunk,
                                         int randomTickSpeed,
                                         CallbackInfo ci,
                                         @Share("minChunkY") LocalIntRef minChunkY,
                                         @Share("chunkSection") LocalRef<LevelChunkSection> chunkSection) {
        int minX = chunk.getPos().getMinBlockX();
        int minY = minChunkY.get();
        int minZ = chunk.getPos().getMinBlockZ();

        for(int i = 0; i < TickRules.getMaxExtraTicks(this.getGameRules()); i++) {
            BlockPos pos = this.getBlockRandomPos(minX, minY, minZ, 15);
            Profiler.get().push("ggRandomTick");
            BlockState blockState = chunkSection.get().getBlockState(pos.getX() - minX, pos.getY() - minY, pos.getZ() - minZ);

            if (blockState.isRandomlyTicking()) {
                if (i < this.getGameRules().getInt(TickRules.EXTRA_LEAF_TICKS) && blockState.getBlock() instanceof LeavesBlock
                        || i < this.getGameRules().getInt(TickRules.EXTRA_FARMLAND_TICKS) && blockState.getBlock() instanceof FarmBlock) {
                    blockState.randomTick((ServerLevel) (Object) this, pos, this.random);
                }
            }
            
            Profiler.get().pop();
        }
    }
}
