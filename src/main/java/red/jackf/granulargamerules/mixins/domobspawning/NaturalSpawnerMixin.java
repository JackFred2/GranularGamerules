package red.jackf.granulargamerules.mixins.domobspawning;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import red.jackf.granulargamerules.impl.rules.MobSpawningRules;

@Mixin(NaturalSpawner.class)
public abstract class NaturalSpawnerMixin {

    @WrapWithCondition(method = "spawnForChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/NaturalSpawner;spawnCategoryForChunk(Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/LevelChunk;Lnet/minecraft/world/level/NaturalSpawner$SpawnPredicate;Lnet/minecraft/world/level/NaturalSpawner$AfterSpawnCallback;)V"))
    private static boolean possiblyCancelSpawningForCategory(MobCategory category, ServerLevel level, LevelChunk chunk, NaturalSpawner.SpawnPredicate filter, NaturalSpawner.AfterSpawnCallback callback) {
        return MobSpawningRules.isSpawningAllowed(level, category);
    }
}
