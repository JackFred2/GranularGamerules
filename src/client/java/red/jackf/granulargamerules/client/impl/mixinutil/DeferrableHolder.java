package red.jackf.granulargamerules.client.impl.mixinutil;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.worldselection.EditGameRulesScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameRules;
import red.jackf.granulargamerules.client.impl.screen.CheckboxButton;
import red.jackf.granulargamerules.impl.mixinutil.GGGameRules;

import java.util.function.Supplier;

public class DeferrableHolder {
    private final GameRules.Key<?> myKey;
    private final GameRules rules;
    public final CheckboxButton checkboxButton = new CheckboxButton(Component.empty(), this::cycle, Supplier::get);

    public DeferrableHolder(EditGameRulesScreen.GameRuleEntry myEntry, GameRules rules) {
        this.myKey = ((GGGameRuleEntry) myEntry).gg$getGameruleKey();
        this.rules = rules;

        this.checkboxButton.setChecked(((GGGameRules) rules).gg$isDeferred(this.myKey));
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int left, int top) {
        this.checkboxButton.setX(left - 24);
        this.checkboxButton.setY(top);
        this.checkboxButton.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void cycle(CheckboxButton checkboxButton, boolean isChecked) {
        ((GGGameRules) rules).gg$setDeferred(this.myKey, isChecked);
    }
}
