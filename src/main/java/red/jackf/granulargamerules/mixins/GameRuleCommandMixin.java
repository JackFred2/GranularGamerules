package red.jackf.granulargamerules.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.commands.GameRuleCommand;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import red.jackf.granulargamerules.impl.GranularGamerules;
import red.jackf.granulargamerules.impl.mixinutil.GGGameRules;
import red.jackf.jackfredlib.api.base.Ephemeral2;

/**
 * Used to emphasise a game rule query has been deferred.
 */
@Mixin(GameRuleCommand.class)
public class GameRuleCommandMixin {
    @Unique
    private static final Ephemeral2<GameRules> gameRules = new Ephemeral2<>();

    @Inject(method = "queryRule", at = @At("HEAD"))
    private static void gg$grabGameRulesObject(CommandSourceStack source, GameRules.Key<?> gameRule, CallbackInfoReturnable<Integer> cir) {
        gameRules.push(source.getServer().getGameRules());
    }

    @WrapOperation(method = "queryRule", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getRule(Lnet/minecraft/world/level/GameRules$Key;)Lnet/minecraft/world/level/GameRules$Value;"))
    private static <T extends GameRules.Value<T>> T getDeferredRule(GameRules instance, GameRules.Key<T> key, Operation<T> original) {
        if (GranularGamerules.hasParent(key) && GGGameRules.isDeferred(instance, key)) {
            var parent = GranularGamerules.getParentRule(key);
            if (parent.isPresent()) {
                key = parent.get();
            }
        }

        return original.call(instance, key);
    }

    @WrapOperation(method = "method_51989", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;"))
    private static <T extends GameRules.Value<T>> MutableComponent emphasiseDeferred(String id, Object[] args, Operation<MutableComponent> original, GameRules.Key<T> key) {
        if (gameRules.hasValue() && args.length >= 2) {
            if (GranularGamerules.hasParent(key) && GGGameRules.isDeferred(gameRules.pop(), key)) {
                Style style = Style.EMPTY.withColor(ChatFormatting.YELLOW)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Component.literal(GranularGamerules.getParentRule(key).map(GameRules.Key::getId).orElse("<unknown parent>"))));

                args[1] = Component.literal(args[1].toString()).withStyle(style);
            }
        }
        return original.call(id, args);
    }

    @Inject(method = "setRule", at = @At("TAIL"))
    private static void disableDeferredOnSet(CommandContext<CommandSourceStack> source, GameRules.Key<?> gameRule, CallbackInfoReturnable<Integer> cir) {
        ((GGGameRules) source.getSource().getServer().getGameRules()).gg$setDeferred(gameRule, false);
    }
}
