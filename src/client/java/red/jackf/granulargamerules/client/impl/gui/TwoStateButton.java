package red.jackf.granulargamerules.client.impl.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;

public class TwoStateButton extends Button {
    private final WidgetSprites sprites;
    private boolean state = false;

    public TwoStateButton(int x, int y, int width, int height, WidgetSprites sprites, Component message, OnPress onPress, CreateNarration narration) {
        super(x, y, width, height, message, b -> onPress.onPress((TwoStateButton) b), narration);
        this.sprites = sprites;
    }

    public void setState(boolean newState) {
        this.state = newState;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.blitSprite(sprites.get(this.state, this.isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    public interface OnPress {
        void onPress(TwoStateButton button);
    }
}
