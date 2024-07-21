package red.jackf.granulargamerules.client.mixins.gamerulescreen;

import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractSelectionList.class)
public interface AbstractSelectionListAccessor {

    @Accessor
    int getItemHeight();
}
