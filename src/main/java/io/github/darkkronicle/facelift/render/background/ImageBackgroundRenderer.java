package io.github.darkkronicle.facelift.render.background;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.darkkronicle.darkkore.util.render.RenderUtil;
import io.github.darkkronicle.facelift.render.image.AsyncCustomImage;
import io.github.darkkronicle.facelift.render.image.CustomImage;
import io.github.darkkronicle.facelift.render.image.ImageHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ImageBackgroundRenderer implements BackgroundRenderer {

    private CustomImage image;
    private final File imageFile;
    private final static int MOUSE_STRENGTH = 10;

    public ImageBackgroundRenderer(File file) {
        this.imageFile = file;
    }

    @Override
    public void load() {
        this.image = ImageHandler.getInstance().loadImage(imageFile);
        this.image.loadImages();
    }

    @Override
    public CompletableFuture<Void> loadAsync(Executor executor) {
        AsyncCustomImage image = ImageHandler.getInstance().loadImageAsync(imageFile, executor);
        if (image == null) {
            // Exists within the cache
            this.image = ImageHandler.getInstance().loadImage(imageFile);
            return null;
        }
        this.image = image;
        return image.getFuture().thenApply((f) -> null);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (image == null) {
            return;
        }
        int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int height = MinecraftClient.getInstance().getWindow().getScaledHeight();
        if (!image.partialLoaded()) {
            RenderUtil.drawRectangle(matrices, 0, 0, width, height, 0xFF000000);
            return;
        }
        image.update(delta);
        int tWidth = image.getWidth();
        int tHeight = image.getHeight();
        if (tWidth < 0 || tHeight < 0) {
            return;
        }
        while (tWidth < width) {
            tWidth *= 2;
            tHeight *= 2;
        }

        matrices.push();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        image.setShaderTexture();
        float textureRatio = ((float) tHeight) / tWidth;
        float screenRatio = ((float) height) / width;
        int renderWidth, renderHeight;
        if (screenRatio > textureRatio) {
            renderHeight = height;
            renderWidth = (int) (height * (1f / textureRatio));
        } else {
            renderWidth = width;
            renderHeight = (int) (width * textureRatio);
        }

        float mouseXPer = ((float) mouseX) / width;
        float mouseYPer = ((float) mouseY) / height;

        matrices.translate(mouseXPer * -MOUSE_STRENGTH, mouseYPer * -MOUSE_STRENGTH, 0);
        DrawableHelper.drawTexture(matrices, -10, -10, 0, 0, tWidth, tHeight, renderWidth + MOUSE_STRENGTH * 2, renderHeight + MOUSE_STRENGTH * 2);
        matrices.pop();
    }

}
