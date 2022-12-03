package io.github.darkkronicle.facelift.title;

import io.github.darkkronicle.facelift.mixin.SplashOverlayAccessor;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.container.FlowLayout;
import me.x150.renderer.renderer.MSAAFramebuffer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

import java.awt.desktop.QuitHandler;

public abstract class AbstractTitleMenu extends BaseOwoScreen<FlowLayout> implements TitleMenu {

    protected AbstractTitleMenu() {
        super();
    }


    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrices, mouseX, mouseY, partialTicks);
        MSAAFramebuffer.use(8, () ->
            super.render(matrices, mouseX, mouseY, partialTicks)
        );
    }

    @Override
    protected void init() {
        Overlay overlay = MinecraftClient.getInstance().getOverlay();
        if (overlay != null && MinecraftClient.getInstance().getOverlay() instanceof SplashOverlay) {
            if (((SplashOverlayAccessor) overlay).getReloadCompleteTime() < 0) {
                return;
            }
        }
        super.init();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.uiAdapter == null) {
            return false;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.uiAdapter == null) {
            return false;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void setClientScreen() {
        MinecraftClient.getInstance().setScreen(this);
    }
}
