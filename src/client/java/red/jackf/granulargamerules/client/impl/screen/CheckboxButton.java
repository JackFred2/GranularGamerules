package red.jackf.granulargamerules.client.impl.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import red.jackf.granulargamerules.impl.GranularGamerules;

public class CheckboxButton extends Button {
    private static final int SIZE = 20;
    private static final int SPRITE_SIZE = 15;

    private static final ResourceLocation CHECKED = GranularGamerules.id("checkbox/checked");
    private static final ResourceLocation UNCHECKED = GranularGamerules.id("checkbox/unchecked");
    private final OnChange onChange;
    private boolean isChecked = true;

    public CheckboxButton(Component message,
                             OnChange onChange,
                             @Nullable Button.CreateNarration createNarration) {
        super(0, 0, SIZE, SIZE, message, b -> {}, createNarration);
        this.onChange = onChange;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    @Override
    public void onPress() {
        super.onPress();
        this.setChecked(!this.isChecked());
        this.onChange.onPress(this, this.isChecked());
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
        int spriteLeft = this.getX() + this.getWidth() / 2 - SPRITE_SIZE / 2;
        int spriteTop = this.getY() + this.getHeight() / 2 - SPRITE_SIZE / 2;
        graphics.blitSprite(RenderType::guiTextured, isChecked ? CHECKED : UNCHECKED, spriteLeft, spriteTop, SPRITE_SIZE, SPRITE_SIZE);
    }

    public interface OnChange {
        void onPress(CheckboxButton button, boolean isNowChecked);
    }
}
