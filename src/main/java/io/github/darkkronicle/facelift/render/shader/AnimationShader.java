package io.github.darkkronicle.facelift.render.shader;

import io.github.darkkronicle.facelift.render.animation.AnimatableFloat;
import io.wispforest.owo.ui.core.AnimatableProperty;
import io.wispforest.owo.ui.core.Animation;
import io.wispforest.owo.ui.core.Easing;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;

public class AnimationShader {

    private final ManagedShaderEffect shader;
    private final AnimatableProperty<AnimatableFloat> percentage;
    private final boolean zeroToOne;
    private final Renderable renderBefore;
    private final Renderable renderAfter;
    private final Runnable setConfig;

    public AnimationShader(ManagedShaderEffect shader, boolean zeroToOne, Renderable renderBefore, Renderable renderAfter, @Nullable Runnable setConfig) {
        this.shader = shader;
        this.zeroToOne = zeroToOne;
        this.percentage = AnimatableProperty.of(new AnimatableFloat(zeroToOne ? 0 : 1));
        this.renderBefore = renderBefore;
        this.renderAfter = renderAfter;
        this.setConfig = setConfig;
    }

    public void render(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        percentage.update(delta);
        renderBefore.render(matrices, delta, mouseX, mouseY);
        AnimationFramebuffer.use(shader, setConfig, getPercentage(),() -> renderAfter.render(matrices, delta, mouseX, mouseY));
    }

    public float getPercentage() {
        return percentage.get().getValue();
    }

    public void setPercentage(float percent) {
        percentage.set(new AnimatableFloat(percent));
    }

    public Animation<AnimatableFloat> animate(int duration, Easing easing) {
        return percentage.animate(duration, easing, new AnimatableFloat(zeroToOne ? 1 : 0)).reverse();
    }

}
