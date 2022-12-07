package io.github.darkkronicle.facelift.mixin;

import io.github.darkkronicle.darkkore.util.render.RenderUtil;
import io.github.darkkronicle.facelift.Facelift;
import io.github.darkkronicle.facelift.render.screen.AnimatableScreen;
import io.github.darkkronicle.facelift.render.shader.AnimationShader;
import io.github.darkkronicle.facelift.render.shader.Renderable;
import io.github.darkkronicle.facelift.render.shader.Shaders;
import io.wispforest.owo.ui.core.Easing;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin implements AnimatableScreen {

    @Shadow public abstract void render(MatrixStack matrices, int mouseX, int mouseY, float delta);

    @Shadow @Nullable protected MinecraftClient client;
    @Getter
    @Nullable
    @Unique
    private AnimationShader animation = null;

    @Override
    public void animate(ManagedShaderEffect effect, Runnable setShaderConfig, int duration, Easing easing) {
        Renderable renderable;
        if (Facelift.lastScreen == null) {
            renderable = (matrices, delta, mouseX, mouseY) -> {
            };
        } else {
            renderable = (matrices, delta, mouseX, mouseY) -> Facelift.lastScreen.render(matrices, mouseX, mouseY, delta);
        }
        animation = new AnimationShader(effect, true, renderable, ((matrices, delta, mouseX, mouseY) -> render(matrices, mouseX, mouseY, delta)), setShaderConfig);
        animation.animate(duration, easing);
    }

    @Override
    public boolean renderAnimation(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (animation == null || animation.getPercentage() >= 1) {
            return false;
        }
        animation.render(matrices, delta, mouseX, mouseY);
        return true;
    }

    @Override
    public boolean isNotAnimating() {
        return animation == null || animation.getPercentage() >= 1;
    }
}
