package io.github.darkkronicle.facelift.render.components;

import io.github.darkkronicle.darkkore.util.render.RenderUtil;
import io.wispforest.owo.ui.core.AnimatableProperty;
import io.wispforest.owo.ui.core.Color;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class CircleTextureComponent extends TextureComponent {

    private final AnimatableProperty<Color> innerColor = AnimatableProperty.of(new Color(0, 0, 0, 1));
    private final AnimatableProperty<Color> outerColor = AnimatableProperty.of(new Color(1, 1, 1, 1));

    private int circleRad;
    private int outlineRad;


    public CircleTextureComponent(
            Identifier texture, int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight,
            int circleRad, int outlineRad
    ) {
        super(texture, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
        this.circleRad = circleRad;
        this.outlineRad = outlineRad;
    }

    @Override
    public void draw(MatrixStack matrices, int mouseX, int mouseY, float partialTicks, float delta) {
        float centerX = x + width / 2f;
        float centerY = y + height / 2f;

        if (circleRad != outlineRad && outlineRad > 0) {
            Color outlineColor = outerColor.get();
            RenderUtil.drawRingInnerPercent(
                    matrices,
                    centerX,
                    centerY,
                    (float) outlineRad,
                    (float) Math.pow(((float) circleRad - 1) / outlineRad, 2),
                    outlineColor.argb()
            );
        }
        Color circleColor = innerColor.get();
        RenderUtil.drawCircle(
                matrices,
                centerX,
                centerY,
                circleRad,
                circleColor.argb()
        );
        super.draw(matrices, mouseX, mouseY, partialTicks, delta);
    }

    @Override
    public boolean isInBoundingBox(double x, double y) {
        double offsetX = x - (this.x + width / 2f);
        double offsetY = y - (this.y + height / 2f);
        return Math.sqrt(Math.pow(offsetX, 2) + Math.pow(offsetY, 2)) <= outlineRad;
    }

    public CircleTextureComponent innerColor(Color color) {
        innerColor.set(color);
        return this;
    }

    public CircleTextureComponent outerColor(Color color) {
        outerColor.set(color);
        return this;
    }
}
