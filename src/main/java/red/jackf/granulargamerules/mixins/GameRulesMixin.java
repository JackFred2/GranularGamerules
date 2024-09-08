package red.jackf.granulargamerules.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicLike;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Used to store deferred game rules and persist them across saves.
 */
@Mixin(GameRules.class)
public abstract class GameRulesMixin implements GGGameRules {
    @Shadow @Final private Map<GameRules.Key<?>, GameRules.Value<?>> rules;
    @Unique
    private static final String TAG_KEY = "_ggDeferrals";

    @Unique
    private final Set<GameRules.Key<?>> deferredGameRuleIds = new HashSet<>();

    @Override
    public void gg$setDeferred(GameRules.Key<?> key, boolean isDeferred) {
        if (isDeferred) {
            if (GranularGamerules.getParentRule(key).isEmpty()) {
                GranularGamerules.LOGGER.warn("Tried to mark non-parented gamerule as deferred: {}", key.getId());
                return;
            }
            deferredGameRuleIds.add(key);
        } else {
            deferredGameRuleIds.remove(key);
        }
    }

    @Override
    public boolean gg$isDeferred(GameRules.Key<?> key) {
        return deferredGameRuleIds.contains(key);
    }

    @ModifyReturnValue(method = "copy", at = @At("RETURN"))
    private GameRules copyDeferred(GameRules copy) {
        GameRulesMixin other = ((GameRulesMixin) (Object) copy);
        //noinspection DataFlowIssue
        other.deferredGameRuleIds.clear();
        other.deferredGameRuleIds.addAll(this.deferredGameRuleIds);
        return copy;
    }

    @Inject(method = "assignFrom", at = @At("TAIL"))
    private void assignDeferredFrom(GameRules source, @Nullable MinecraftServer server, CallbackInfo ci) {
        GameRulesMixin other = ((GameRulesMixin) (Object) source);
        this.deferredGameRuleIds.clear();
        //noinspection DataFlowIssue
        this.deferredGameRuleIds.addAll(other.deferredGameRuleIds);
    }

    @ModifyReturnValue(method = "createTag", at = @At("RETURN"))
    private CompoundTag addDeferredToTag(CompoundTag original) {
        var list = new ListTag();

        for (GameRules.Key<?> key : this.deferredGameRuleIds) {
            list.add(StringTag.valueOf(key.getId()));
        }

        original.put(TAG_KEY, list);

        return original;
    }

    @Inject(method = "loadFromTag", at = @At("RETURN"))
    private void loadDeferredFromTag(DynamicLike<?> dynamic, CallbackInfo ci) {
        Set<String> deferredIds = dynamic.get(TAG_KEY).orElseEmptyList().asStream()
                .map(Dynamic::asString)
                .filter(DataResult::isSuccess)
                .map(DataResult::getOrThrow)
                .collect(Collectors.toSet());

        this.deferredGameRuleIds.clear();

        this.rules.forEach((key, value) -> {
            if (deferredIds.contains(key.getId())) {
                this.deferredGameRuleIds.add(key);
            }
        });
    }
}
