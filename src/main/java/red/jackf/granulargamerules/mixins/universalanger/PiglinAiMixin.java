package red.jackf.granulargamerules.mixins.universalanger;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import red.jackf.granulargamerules.impl.GGDeferredChecker;
import red.jackf.granulargamerules.impl.rules.UniversalAngerRules;

@Mixin(PiglinAi.class)
public class PiglinAiMixin {

    @WrapOperation(method = {"maybeRetaliate", "setAngerTarget", "method_24734"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"))
    private static boolean checkGGRule(GameRules instance, GameRules.Key<GameRules.BooleanValue> parent, Operation<Boolean> original) {
        return GGDeferredChecker.getBoolean(instance, UniversalAngerRules.PIGLINS_UNIVERSAL_ANGER).orElse(original.call(instance, parent));
    }
}
