package io.github.darkkronicle.facelift.ui;

import io.github.darkkronicle.facelift.Facelift;
import io.github.darkkronicle.facelift.shader.AnimationShader;
import io.github.darkkronicle.facelift.shader.Renderable;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.core.Easing;
import io.wispforest.owo.ui.core.ParentComponent;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;

public abstract class AnimatableOwoScreen<T extends ParentComponent> extends BaseOwoScreen<T> {

    protected Screen previousScreen;
    protected AnimationShader animation;
    private final ManagedShaderEffect animationShader;
    @Nullable private final Runnable setShaderConfig;

    public AnimatableOwoScreen(Screen previousScreen, ManagedShaderEffect animation, @Nullable Runnable setShaderConfig) {
        super();
        this.previousScreen = previousScreen;
        this.animationShader = animation;
        this.setShaderConfig = setShaderConfig;
    }

    protected void animate() {
        Renderable renderable;
        if (previousScreen == null) {
            renderable = (matrices, delta, mouseX, mouseY) -> {};
        } else {
            renderable = (matrices, delta, mouseX, mouseY) -> previousScreen.render(matrices, mouseX, mouseY, delta);
        }
        this.animation = new AnimationShader(
                animationShader, true,
                 renderable,
                (matrices, delta, mouseX, mouseY) -> this.rawRender(matrices, mouseX, mouseY, delta),
                setShaderConfig
        );
        this.animation.animate(500, Easing.SINE);
    }

    @Override
    protected void init() {
        if (Facelift.lastScreen != null && Facelift.lastScreen != this && MinecraftClient.getInstance().currentScreen == this) {
            previousScreen = Facelift.lastScreen;
            Facelift.lastScreen = null;
            animate();
        }
        super.init();
    }

    @Override
    protected void build(T rootComponent) {
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (animation != null && animation.getPercentage() >= 1) {
            animation = null;
        }
        if (animation == null) {
            rawRender(matrices, mouseX, mouseY, delta);
            return;
        }
        animation.render(matrices, delta, mouseX, mouseY);
    }

    protected void rawRender(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        super.render(matrixStack, mouseX, mouseY, delta);
    }

}
