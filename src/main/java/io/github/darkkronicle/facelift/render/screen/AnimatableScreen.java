package io.github.darkkronicle.facelift.render.screen;

import io.github.darkkronicle.facelift.render.shader.AnimationShader;
import io.wispforest.owo.ui.core.Easing;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;

public interface AnimatableScreen {

    @Nullable AnimationShader getAnimation();

    void animate(ManagedShaderEffect effect, Runnable setShaderConfig, int duration, Easing easing);

    boolean renderAnimation(MatrixStack matrices, int mouseX, int mouseY, float delta);

    boolean isNotAnimating();

}
