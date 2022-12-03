package io.github.darkkronicle.facelift.ui.background;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.darkkronicle.facelift.image.AsyncCustomImage;
import io.github.darkkronicle.facelift.image.CustomImage;
import io.github.darkkronicle.facelift.image.ImageHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class RotatingCubeBackgroundRenderer implements BackgroundRenderer {

    private final CustomImage[] images;
    private float time = 0;
    private final File facesDirectory;


    public RotatingCubeBackgroundRenderer(File facesDirectory) {
        images = new CustomImage[6];
        this.facesDirectory = facesDirectory;
    }

    @Override
    public void load() {
        File[] files = facesDirectory.listFiles(file -> file.getName().endsWith(".jpg") || file.getName().endsWith(".png"));
        for (int i = 0; i < 6; i++) {
            File file = null;
            for (File f : files) {
                String name = FilenameUtils.getBaseName(f.getName());
                if (name.endsWith(String.valueOf(i))) {
                    file = f;
                    break;
                }
            }
            if (file == null) {
                continue;
            }
            CustomImage image = ImageHandler.getInstance().loadImage(file);
            image.loadImages();
            images[i] = image;
        }
    }

    @Override
    public CompletableFuture<Void> loadAsync(Executor executor) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        File[] files = facesDirectory.listFiles(file -> file.getName().endsWith(".jpg") || file.getName().endsWith(".png"));
        for (int i = 0; i < 6; i++) {
            int finalI = i;
            File file = null;
            for (File f : files) {
                String name = FilenameUtils.getBaseName(f.getName());
                if (name.endsWith(String.valueOf(i))) {
                    file = f;
                    break;
                }
            }
            if (file == null) {
                continue;
            }
            AsyncCustomImage image = ImageHandler.getInstance().loadImageAsync(file, executor);
            if (image == null || image.getFuture() == null) {
                images[finalI] = ImageHandler.getInstance().loadImage(file);
                continue;
            }
            futures.add(image.getFuture().thenAccept(asyncImage -> images[finalI] = asyncImage));
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        time += delta;
        float yaw = MathHelper.sin(this.time * 0.001F) * 5.0F + 25.0F;
        float pitch = -this.time * 0.1f;

        float alpha = 1;
        MinecraftClient client = MinecraftClient.getInstance();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        Matrix4f matrix4f = Matrix4f.viewboxMatrix(
                85.0,
                (float) client.getWindow().getFramebufferWidth() / (float) client.getWindow().getFramebufferHeight(),
                0.05f,
                10.0f
        );
        RenderSystem.backupProjectionMatrix();
        RenderSystem.setProjectionMatrix(matrix4f);
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.loadIdentity();
        matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180.0f));
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        for (int j = 0; j < 4; j++) {
            matrixStack.push();
            float f = ((float) (j % 2) / 2.0f - 0.5f) / 256.0f;
            float g = ((float) (j / 2) / 2.0f - 0.5f) / 256.0f;
            matrixStack.translate(f, g, 0.0);
            matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(yaw));
            matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(pitch));
            RenderSystem.applyModelViewMatrix();
            for (int face = 0; face < 6; face++) {
                if (images[face] == null || !images[face].isLoaded()) {
                    continue;
                }
                images[face].setShaderTexture();
                bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
                int calcAlpha = Math.round(255.0f * alpha) / (j + 1);
                if (face == 0) {
                    bufferBuilder.vertex(-1.0, -1.0, 1.0).texture(0.0f, 0.0f).color(255, 255, 255, calcAlpha).next();
                    bufferBuilder.vertex(-1.0, 1.0, 1.0).texture(0.0f, 1.0f).color(255, 255, 255, calcAlpha).next();
                    bufferBuilder.vertex(1.0, 1.0, 1.0).texture(1.0f, 1.0f).color(255, 255, 255, calcAlpha).next();
                    bufferBuilder.vertex(1.0, -1.0, 1.0).texture(1.0f, 0.0f).color(255, 255, 255, calcAlpha).next();
                }
                if (face == 1) {
                    bufferBuilder.vertex(1.0, -1.0, 1.0).texture(0.0f, 0.0f).color(255, 255, 255, calcAlpha).next();
                    bufferBuilder.vertex(1.0, 1.0, 1.0).texture(0.0f, 1.0f).color(255, 255, 255, calcAlpha).next();
                    bufferBuilder.vertex(1.0, 1.0, -1.0).texture(1.0f, 1.0f).color(255, 255, 255, calcAlpha).next();
                    bufferBuilder.vertex(1.0, -1.0, -1.0).texture(1.0f, 0.0f).color(255, 255, 255, calcAlpha).next();
                }
                if (face == 2) {
                    bufferBuilder.vertex(1.0, -1.0, -1.0).texture(0.0f, 0.0f).color(255, 255, 255, calcAlpha).next();
                    bufferBuilder.vertex(1.0, 1.0, -1.0).texture(0.0f, 1.0f).color(255, 255, 255, calcAlpha).next();
                    bufferBuilder.vertex(-1.0, 1.0, -1.0).texture(1.0f, 1.0f).color(255, 255, 255, calcAlpha).next();
                    bufferBuilder.vertex(-1.0, -1.0, -1.0).texture(1.0f, 0.0f).color(255, 255, 255, calcAlpha).next();
                }
                if (face == 3) {
                    bufferBuilder.vertex(-1.0, -1.0, -1.0).texture(0.0f, 0.0f).color(255, 255, 255, calcAlpha).next();
                    bufferBuilder.vertex(-1.0, 1.0, -1.0).texture(0.0f, 1.0f).color(255, 255, 255, calcAlpha).next();
                    bufferBuilder.vertex(-1.0, 1.0, 1.0).texture(1.0f, 1.0f).color(255, 255, 255, calcAlpha).next();
                    bufferBuilder.vertex(-1.0, -1.0, 1.0).texture(1.0f, 0.0f).color(255, 255, 255, calcAlpha).next();
                }
                if (face == 4) {
                    bufferBuilder.vertex(-1.0, -1.0, -1.0).texture(0.0f, 0.0f).color(255, 255, 255, calcAlpha).next();
                    bufferBuilder.vertex(-1.0, -1.0, 1.0).texture(0.0f, 1.0f).color(255, 255, 255, calcAlpha).next();
                    bufferBuilder.vertex(1.0, -1.0, 1.0).texture(1.0f, 1.0f).color(255, 255, 255, calcAlpha).next();
                    bufferBuilder.vertex(1.0, -1.0, -1.0).texture(1.0f, 0.0f).color(255, 255, 255, calcAlpha).next();
                }
                if (face == 5) {
                    bufferBuilder.vertex(-1.0, 1.0, 1.0).texture(0.0f, 0.0f).color(255, 255, 255, calcAlpha).next();
                    bufferBuilder.vertex(-1.0, 1.0, -1.0).texture(0.0f, 1.0f).color(255, 255, 255, calcAlpha).next();
                    bufferBuilder.vertex(1.0, 1.0, -1.0).texture(1.0f, 1.0f).color(255, 255, 255, calcAlpha).next();
                    bufferBuilder.vertex(1.0, 1.0, 1.0).texture(1.0f, 0.0f).color(255, 255, 255, calcAlpha).next();
                }
                tessellator.draw();
            }
            matrixStack.pop();
            RenderSystem.applyModelViewMatrix();
            RenderSystem.colorMask(true, true, true, false);
        }
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.restoreProjectionMatrix();
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
    }

}
