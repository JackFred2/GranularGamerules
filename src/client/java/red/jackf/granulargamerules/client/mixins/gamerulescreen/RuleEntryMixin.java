package red.jackf.granulargamerules.client.mixins.gamerulescreen;

import net.minecraft.client.gui.screens.worldselection.EditGameRulesScreen;
import net.minecraft.world.level.GameRules;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import red.jackf.granulargamerules.client.impl.mixinutil.GGEntryKeyHolder;

/**
 * Used to store the game rule key in each entry.
 *
 * @author JackFred
 */
@Mixin(EditGameRulesScreen.RuleEntry.class)
public abstract class RuleEntryMixin implements GGEntryKeyHolder {
    @Unique
    private GameRules.Key<?> key;

    @Override
    public void gg$setKey(GameRules.Key<?> key) {
        this.key = key;
    }

    @Override
    @Nullable
    public GameRules.Key<?> gg$getKey() {
        return key;
    }
}
