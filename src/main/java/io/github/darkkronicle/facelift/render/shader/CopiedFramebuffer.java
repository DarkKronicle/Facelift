package io.github.darkkronicle.facelift.render.shader;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.darkkronicle.darkkore.util.render.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL30C;

public class CopiedFramebuffer extends Framebuffer {

    private static CopiedFramebuffer instance = null;

    private CopiedFramebuffer(int width, int height) {
        super(true);
        RenderSystem.assertOnRenderThreadOrInit();
        this.resize(width, height, true);
        this.setClearColor(0f, 0f, 0f, 0f);
    }

    public static CopiedFramebuffer obtain() {
        if (instance == null) {
            instance = new CopiedFramebuffer(
                    MinecraftClient.getInstance().getFramebuffer().textureWidth,
                    MinecraftClient.getInstance().getFramebuffer().textureHeight
            );
        }
        return instance;
    }

    public static void copy(Framebuffer other, boolean clear) {
        CopiedFramebuffer buffer = obtain();
        GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, buffer.fbo);
        if (clear) {
            buffer.clear(true);
        }
        if (buffer.textureWidth != other.textureWidth || buffer.textureHeight != other.textureHeight) {
            buffer.resize(other.textureWidth, other.textureHeight, false);
        }
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, other.fbo);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, buffer.fbo);
        GL30.glBlitFramebuffer(0, 0, other.viewportWidth, other.viewportHeight, 0, 0, buffer.textureWidth, buffer.textureHeight, GL30.GL_COLOR_BUFFER_BIT, GL30.GL_NEAREST);
        GL30.glBlitFramebuffer(0, 0, other.viewportWidth, other.viewportHeight, 0, 0, buffer.textureWidth, buffer.textureHeight, GL30.GL_DEPTH_BUFFER_BIT, GL30.GL_NEAREST);
        GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, 0);
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
    }


}
