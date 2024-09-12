package red.jackf.granulargamerules.client.impl.mixinutil;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.worldselection.EditGameRulesScreen;
import net.minecraft.network.chat.Component;
import red.jackf.granulargamerules.client.impl.util.ListUtil;
import red.jackf.granulargamerules.client.impl.screen.TwoStateButton;
import red.jackf.granulargamerules.impl.GranularGamerules;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class ChildRulesHolder {
    private static final WidgetSprites BUTTON_SPRITES = new WidgetSprites(
            GranularGamerules.id("group/expanded"),
            GranularGamerules.id("group/collapsed"),
            GranularGamerules.id("group/expanded_highlighted"),
            GranularGamerules.id("group/collapsed_highlighted")
    );

    public static final int BUTTON_LEFT_OFFSET = 24;

    public final List<EditGameRulesScreen.GameRuleEntry> childEntries;
    private final EditGameRulesScreen.GameRuleEntry myEntry;
    private final EditGameRulesScreen.RuleList ruleList;
    public boolean childrenShown = false;
    public final TwoStateButton expandButton = new TwoStateButton(0, 0, 20, 20, BUTTON_SPRITES, Component.empty(), this::cycleState, Supplier::get);

    public ChildRulesHolder(Collection<EditGameRulesScreen.GameRuleEntry> childEntries, EditGameRulesScreen.GameRuleEntry myEntry, EditGameRulesScreen.RuleList ruleList) {
        this.childEntries = ImmutableList.copyOf(childEntries);
        this.myEntry = myEntry;
        this.ruleList = ruleList;
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int left, int top) {
        this.expandButton.setX(left - BUTTON_LEFT_OFFSET);
        this.expandButton.setY(top);
        this.expandButton.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void cycleState(TwoStateButton twoStateButton) {
        this.childrenShown = !this.childrenShown;

        int myIndex = ListUtil.getIndexOf(this.ruleList.children(), entry -> entry == this.myEntry);
        if (myIndex == -1) return;

        if (this.childrenShown) { // add entries
            this.ruleList.children().addAll(myIndex + 1, this.childEntries);
        } else { // remove entries
            this.ruleList.children().removeAll(this.childEntries);
        }

        this.expandButton.setState(this.childrenShown);
    }
}
