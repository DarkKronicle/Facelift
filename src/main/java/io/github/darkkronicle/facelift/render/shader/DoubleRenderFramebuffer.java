package io.github.darkkronicle.facelift.render.shader;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.darkkronicle.facelift.Facelift;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import me.x150.renderer.renderer.util.ShaderEffectDuck;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL30C;

import java.util.Objects;

public class DoubleRenderFramebuffer extends Framebuffer {

    private static DoubleRenderFramebuffer instance = null;

    private DoubleRenderFramebuffer(int width, int height) {
        super(true);
        RenderSystem.assertOnRenderThreadOrInit();
        this.resize(width, height, true);
        this.setClearColor(0f, 0f, 0f, 0f);
    }

    public static DoubleRenderFramebuffer obtain() {
        if (instance == null) {
            instance = new DoubleRenderFramebuffer(
                    MinecraftClient.getInstance().getFramebuffer().textureWidth,
                    MinecraftClient.getInstance().getFramebuffer().textureHeight
            );
        }
        return instance;
    }

    public static void use(ManagedShaderEffect shader, @Nullable Runnable setConfig, float percent, Runnable r) {
        Framebuffer mainBuffer = MinecraftClient.getInstance().getFramebuffer();
        RenderSystem.assertOnRenderThreadOrInit();
        DoubleRenderFramebuffer buffer = obtain();
        if (buffer.textureWidth != mainBuffer.textureWidth || buffer.textureHeight != mainBuffer.textureHeight) {
            buffer.resize(mainBuffer.textureWidth, mainBuffer.textureHeight, false);
        }
        GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, buffer.fbo);
        Facelift.renderToBuffer = buffer;

        buffer.beginWrite(true);
        r.run();
        buffer.endWrite();
        Facelift.renderToBuffer = null;

        GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, mainBuffer.fbo);
        mainBuffer.beginWrite(false);

        Framebuffer before = SnapshotFramebuffer.obtain();

        ((ShaderEffectDuck) shader.getShaderEffect()).addFakeTarget("animationFbo", before);
        shader.setSamplerUniform("AfterSampler", buffer);
        shader.setUniformValue("Percentage", percent);
        if (setConfig != null) {
            setConfig.run();
        }
        shader.render(MinecraftClient.getInstance().getTickDelta());

        GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, mainBuffer.fbo);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        RenderSystem.setShaderTexture(0, before.getColorAttachment());
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

        MatrixStack matrices = new MatrixStack();
        matrices.scale(0.5f, 0.5f, 1);
        Matrix4f base = matrices.peek().getPositionMatrix();

        bufferBuilder.vertex(base, 0, before.textureHeight, 0.0f).texture(0, 0).color(-1).next();
        bufferBuilder.vertex(base, before.textureWidth, before.textureHeight, 0.0f).texture(1, 0).color(-1).next();
        bufferBuilder.vertex(base, before.textureWidth, 0, 0.0f).texture(1, 1).color(-1).next();
        bufferBuilder.vertex(base, 0, 0, 0.0f).texture(0, 1).color(-1).next();

        tessellator.draw();
        RenderSystem.disableBlend();

        GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, buffer.fbo);
        buffer.clear(true);
        GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, before.fbo);
        before.clear(true);
        GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, mainBuffer.fbo);

        mainBuffer.beginWrite(true);

    }

    public static void copy(Framebuffer original, Framebuffer other, boolean clear) {
        GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, original.fbo);
        if (clear) {
            original.clear(true);
        }
        if (original.textureWidth != other.textureWidth || original.textureHeight != other.textureHeight) {
            original.resize(other.textureWidth, other.textureHeight, false);
        }
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, other.fbo);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, original.fbo);
        GL30.glBlitFramebuffer(0, 0, other.viewportWidth, other.viewportHeight, 0, 0, original.textureWidth, original.textureHeight, GL30.GL_COLOR_BUFFER_BIT, GL30.GL_NEAREST);
        GL30.glBlitFramebuffer(0, 0, other.viewportWidth, other.viewportHeight, 0, 0, original.textureWidth, original.textureHeight, GL30.GL_DEPTH_BUFFER_BIT, GL30.GL_NEAREST);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, 0);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
    }

}
