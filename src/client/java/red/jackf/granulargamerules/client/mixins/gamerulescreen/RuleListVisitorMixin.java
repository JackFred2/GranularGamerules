package red.jackf.granulargamerules.client.mixins.gamerulescreen;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.screens.worldselection.EditGameRulesScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import red.jackf.granulargamerules.client.impl.mixinutil.GGEntryKeyHolder;

import java.util.List;

/**
 * Used to associate each screen entry with it's key, to be retrieved and post-processed after.
 *
 * @author JackFred
 */
@Mixin(targets = "net/minecraft/client/gui/screens/worldselection/EditGameRulesScreen$RuleList$1")
public abstract class RuleListVisitorMixin {
    @WrapOperation(method = "addEntry(Lnet/minecraft/world/level/GameRules$Key;Lnet/minecraft/client/gui/screens/worldselection/EditGameRulesScreen$EntryFactory;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/worldselection/EditGameRulesScreen$EntryFactory;create(Lnet/minecraft/network/chat/Component;Ljava/util/List;Ljava/lang/String;Lnet/minecraft/world/level/GameRules$Value;)Lnet/minecraft/client/gui/screens/worldselection/EditGameRulesScreen$RuleEntry;"))
    private <T extends GameRules.Value<T>> EditGameRulesScreen.RuleEntry test(EditGameRulesScreen.EntryFactory<T> instance,
                                                                              Component label,
                                                                              List<FormattedCharSequence> tooltip,
                                                                              String narration,
                                                                              T value,
                                                                              Operation<EditGameRulesScreen.RuleEntry> original,
                                                                              GameRules.Key<T> key) {

        // not sure why mcdev is complaining
        //noinspection MixinExtrasOperationParameters
        EditGameRulesScreen.RuleEntry entry = original.call(instance, label, tooltip, narration, value);
        ((GGEntryKeyHolder) entry).gg$setKey(key);
        return entry;
    }
}
