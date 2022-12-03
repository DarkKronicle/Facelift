package io.github.darkkronicle.facelift.ui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.darkkronicle.darkkore.util.render.RenderUtil;
import io.wispforest.owo.ui.container.HorizontalFlowLayout;
import io.wispforest.owo.ui.core.AnimatableProperty;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import net.minecraft.client.util.math.MatrixStack;

public class BackgroundHorizontalFlow extends HorizontalFlowLayout {

    private final AnimatableProperty<Color> color;
    private int renderHeight = -1;

    public BackgroundHorizontalFlow(Sizing horizontalSizing, Sizing verticalSizing, Color color) {
        super(horizontalSizing, verticalSizing);
        this.color = AnimatableProperty.of(color);
    }

    @Override
    public void draw(MatrixStack matrices, int mouseX, int mouseY, float partialTicks, float delta) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
        Insets in = margins.get();
        if (renderHeight < 0) {
            RenderUtil.fill(matrices, x - in.left(), y - in.top(), x + width + in.right(), y + height + in.bottom(), color.get().argb());
        } else {
            int centerY = y + height / 2;
            int offset = renderHeight / 2;
            RenderUtil.fill(matrices, x - in.left(), centerY - offset, x + width + in.right(), centerY + offset, color.get().argb());
        }
        super.draw(matrices, mouseX, mouseY, partialTicks, delta);
    }

    public void setRenderHeight(int height) {
        renderHeight = height;
    }
}
