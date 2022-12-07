package io.github.darkkronicle.facelift.render.shader;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.darkkronicle.facelift.Facelift;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import me.x150.renderer.renderer.util.ShaderEffectDuck;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL30C;

import java.util.Objects;

public class AnimationFramebuffer extends Framebuffer {

    private static AnimationFramebuffer instance = null;

    private AnimationFramebuffer(int width, int height) {
        super(true);
        RenderSystem.assertOnRenderThreadOrInit();
        this.resize(width, height, true);
        this.setClearColor(0f, 0f, 0f, 0f);
    }

    public static AnimationFramebuffer obtain() {
        if (instance == null) {
            instance = new AnimationFramebuffer(
                    MinecraftClient.getInstance().getFramebuffer().textureWidth,
                    MinecraftClient.getInstance().getFramebuffer().textureHeight
            );
        }
        return instance;
    }

    public static void use(ManagedShaderEffect shader, @Nullable Runnable setConfig, float percent, Runnable r) {
        Framebuffer mainBuffer = MinecraftClient.getInstance().getFramebuffer();
        Framebuffer copiedBuffer = CopiedFramebuffer.obtain();
        CopiedFramebuffer.copy(mainBuffer, true);
        RenderSystem.assertOnRenderThreadOrInit();
        AnimationFramebuffer buffer = obtain();
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

        Framebuffer before = BeforeFramebuffer.obtain();

        ((ShaderEffectDuck) Objects.requireNonNull(Shaders.PANEL_ANIMATION_SHADER.getShaderEffect())).addFakeTarget("animationFbo", before);
        shader.setSamplerUniform("AfterSampler", buffer);
        shader.setSamplerUniform("vanilla", copiedBuffer);
        shader.setUniformValue("Percentage", percent);
        if (setConfig != null) {
            setConfig.run();
        }
        shader.render(MinecraftClient.getInstance().getTickDelta());
        GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, buffer.fbo);
        buffer.clear(true);
        GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, before.fbo);
        before.clear(true);
        GlStateManager._glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, mainBuffer.fbo);

        mainBuffer.beginWrite(true);

    }

}
