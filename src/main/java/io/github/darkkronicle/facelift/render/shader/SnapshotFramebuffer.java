package io.github.darkkronicle.facelift.render.shader;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.darkkronicle.facelift.Facelift;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import org.lwjgl.opengl.GL30C;

public class SnapshotFramebuffer extends Framebuffer {
    private static SnapshotFramebuffer instance = null;

    private SnapshotFramebuffer(int width, int height) {
        super(true);
        RenderSystem.assertOnRenderThreadOrInit();
        this.resize(width, height, true);
        this.setClearColor(0f, 0f, 0f, 0f);
    }

    public static SnapshotFramebuffer obtain() {
        if (instance == null) {
            instance = new SnapshotFramebuffer(
                    MinecraftClient.getInstance().getFramebuffer().textureWidth,
                    MinecraftClient.getInstance().getFramebuffer().textureHeight
            );
        }
        return instance;
    }

    public static void use(Runnable r) {
        Framebuffer mainBuffer = MinecraftClient.getInstance().getFramebuffer();
        RenderSystem.assertOnRenderThreadOrInit();
        SnapshotFramebuffer buffer = obtain();
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

        mainBuffer.beginWrite(true);
    }

}

