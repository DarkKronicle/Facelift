package io.github.darkkronicle.facelift.render.components;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.darkkronicle.facelift.render.image.CustomImage;
import io.wispforest.owo.ui.core.AnimatableProperty;
import io.wispforest.owo.ui.core.Color;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

public class TextureComponent extends io.wispforest.owo.ui.component.TextureComponent {

    private final AnimatableProperty<Color> color = AnimatableProperty.of(new Color(1, 1, 1, 1));
    private final CustomImage image;

    public TextureComponent(
            CustomImage image, int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight
    ) {
        super(null, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
        this.image = image;
    }

    public TextureComponent(
            Identifier texture, int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight
    ) {
        super(texture, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
        blend = true;
        this.image = null;
    }

    public void color(Color color) {
        this.color.set(color);
    }

    @Override
    public void draw(MatrixStack matrices, int mouseX, int mouseY, float partialTicks, float delta) {
        if (image != null) {
            image.update(delta);
            image.setShaderTexture();
        } else if (texture != null) {
            RenderSystem.setShaderTexture(0, this.texture);
        }
        RenderSystem.enableDepthTest();
        Color current = color.get();
        RenderSystem.setShaderColor(current.red(), current.green(), current.blue(), current.alpha());

        if (this.blend) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
        }

        matrices.push();
        matrices.translate(x, y, 0);

        matrices.scale((float) regionWidth / textureWidth, (float) regionHeight / textureHeight, 0);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix, 0, textureHeight, 0).texture(0, 1).next();
        bufferBuilder.vertex(matrix, textureWidth, textureHeight, 0).texture(1, 1).next();
        bufferBuilder.vertex(matrix, textureWidth, 0, 0).texture(1, 0).next();
        bufferBuilder.vertex(matrix, 0, 0, 0).texture(0, 0).next();
        BufferRenderer.drawWithShader(bufferBuilder.end());

        if (this.blend) {
            RenderSystem.disableBlend();
        }

        matrices.pop();
    }
}
