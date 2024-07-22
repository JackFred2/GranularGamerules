package red.jackf.granulargamerules.client.mixins.gamerulescreen;

import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Used to grab the height of the game rule entries; used to get a correct height for the background of game rule groups
 *
 * @author JackFred
 */
@Mixin(AbstractSelectionList.class)
public interface AbstractSelectionListAccessor {

    @Accessor
    int getItemHeight();
}
