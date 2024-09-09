package red.jackf.granulargamerules.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicLike;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import red.jackf.granulargamerules.impl.GranularGamerules;
import red.jackf.granulargamerules.impl.mixinutil.GGGameRules;

import java.util.*;

/**
 * Used to store deferred game rules and persist them across saves.
 */
@Mixin(GameRules.class)
public abstract class GameRulesMixin implements GGGameRules {
    @Shadow @Final private Map<GameRules.Key<?>, GameRules.Value<?>> rules;
    @Unique
    private static final String TAG_KEY = "_ggDeferrals";

    @Unique
    private final Map<GameRules.Key<?>, Boolean> deferralMap = new HashMap<>();

    @Override
    public void gg$setDeferred(GameRules.Key<?> key, boolean isDeferred) {
        if (!GranularGamerules.hasParent(key)) {
            GranularGamerules.LOGGER.warn("Tried to mark non-parented gamerule: {}", key.getId());
            return;
        }

        deferralMap.put(key, isDeferred);
    }

    @Override
    public boolean gg$isDeferred(GameRules.Key<?> key) {
        return deferralMap.getOrDefault(key, false);
    }

    @ModifyReturnValue(method = "copy", at = @At("RETURN"))
    private GameRules copyDeferred(GameRules copy) {
        GameRulesMixin other = ((GameRulesMixin) (Object) copy);
        //noinspection DataFlowIssue
        other.deferralMap.clear();
        other.deferralMap.putAll(this.deferralMap);
        return copy;
    }

    @Inject(method = "assignFrom", at = @At("TAIL"))
    private void assignDeferredFrom(GameRules source, @Nullable MinecraftServer server, CallbackInfo ci) {
        GameRulesMixin other = ((GameRulesMixin) (Object) source);
        this.deferralMap.clear();
        //noinspection DataFlowIssue
        this.deferralMap.putAll(other.deferralMap);
    }

    @ModifyReturnValue(method = "createTag", at = @At("RETURN"))
    private CompoundTag addDeferredToTag(CompoundTag original) {
        var map = new CompoundTag();

        this.rules.forEach((key, value) -> {
            if (GranularGamerules.hasParent(key)) {
                map.putBoolean(key.getId(), this.deferralMap.getOrDefault(key, true));
            }
        });

        original.put(TAG_KEY, map);

        return original;
    }

    @Inject(method = "loadFromTag", at = @At("RETURN"))
    private void loadDeferredFromTag(DynamicLike<?> dynamic, CallbackInfo ci) {
        Dynamic<?> deferredIds = dynamic.get(TAG_KEY).orElseEmptyMap();

        this.deferralMap.clear();

        this.rules.forEach((key, value) -> {
            if (GranularGamerules.hasParent(key)) {
                this.deferralMap.put(key, deferredIds.get(key.getId()).asBoolean(true));
            }
        });
    }
}
