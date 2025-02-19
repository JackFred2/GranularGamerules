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
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import red.jackf.granulargamerules.impl.rules.TickRules;

import java.util.function.Supplier;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level {

    protected ServerLevelMixin(WritableLevelData levelData, ResourceKey<Level> dimension, RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeRegistration, Supplier<ProfilerFiller> profiler, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
        super(levelData, dimension, registryAccess, dimensionTypeRegistration, profiler, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
    }

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

        // TODO calc once per server tick instead of once per subchunk
        int maxExtra = TickRules.getMaxExtraTicks(this.getGameRules());
        GameRules rules = this.getGameRules();

        for(int i = 0; i < maxExtra; i++) {
            BlockPos pos = this.getBlockRandomPos(minX, minY, minZ, 15);
            this.getProfiler().push("ggRandomTick");
            BlockState blockState = chunkSection.get().getBlockState(pos.getX() - minX, pos.getY() - minY, pos.getZ() - minZ);
            Block block = blockState.getBlock();

            if (blockState.isRandomlyTicking()) {
                if (i < rules.getInt(TickRules.EXTRA_COPPER_TICKS) && block instanceof WeatheringCopper
                        || i < rules.getInt(TickRules.EXTRA_CROP_TICKS) && blockState.is(TickRules.EXTRA_TICKABLE_CROPS_TAG)
                        || i < rules.getInt(TickRules.EXTRA_LEAF_TICKS) && block instanceof LeavesBlock
                        || i < rules.getInt(TickRules.EXTRA_FARMLAND_TICKS) && block instanceof FarmBlock
                        || i < rules.getInt(TickRules.EXTRA_SAPLING_TICKS) && block instanceof SaplingBlock
                        || i < rules.getInt(TickRules.EXTRA_SPREADING_TERRAIN_TICKS) && block instanceof SpreadingSnowyDirtBlock) {
                    blockState.randomTick((ServerLevel) (Object) this, pos, this.random);
                }
            }

            this.getProfiler().pop();
        }
    }
}
