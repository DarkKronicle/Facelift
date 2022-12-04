package io.github.darkkronicle.facelift.render.title;

import io.github.darkkronicle.facelift.mixin.SplashOverlayAccessor;
import io.github.darkkronicle.facelift.render.animation.AnimatableOwoScreen;
import io.wispforest.owo.ui.container.FlowLayout;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import me.x150.renderer.renderer.MSAAFramebuffer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractTitleMenu extends AnimatableOwoScreen<FlowLayout> implements TitleMenu {

    public AbstractTitleMenu(
            Screen parent, ManagedShaderEffect animation,
            @Nullable Runnable setShaderConfig
    ) {
        super(parent, animation, setShaderConfig);
    }

    @Override
    protected void rawRender(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrices, mouseX, mouseY, partialTicks);
        MSAAFramebuffer.use(8, () ->
            super.rawRender(matrices, mouseX, mouseY, partialTicks)
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
