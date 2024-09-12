package red.jackf.granulargamerules.client.impl.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import red.jackf.granulargamerules.impl.GranularGamerules;

public class GroupRenderer {
    private static final int RIGHT_MARGIN = 1;
    private static final int LEFT_MARGIN = 26;
    private static final int BOTTOM_MARGIN = -2;
    private static final int TOP_MARGIN = 2;

    private static final ResourceLocation GROUP_BACKGROUND = GranularGamerules.id("group/background");

    public static void render(GuiGraphics graphics, int left, int top, int width, int height) {
        graphics.blitSprite(GROUP_BACKGROUND, left - LEFT_MARGIN, top - TOP_MARGIN, width + LEFT_MARGIN + RIGHT_MARGIN, height + BOTTOM_MARGIN + TOP_MARGIN);
    }
}
