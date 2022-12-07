package io.github.darkkronicle.facelift.mixin;


import io.github.darkkronicle.darkkore.util.render.RenderUtil;
import io.github.darkkronicle.facelift.Facelift;
import io.github.darkkronicle.facelift.render.screen.AnimatableScreen;
import io.github.darkkronicle.facelift.render.shader.AnimationShader;
import io.github.darkkronicle.facelift.render.shader.Renderable;
import io.wispforest.owo.ui.core.Easing;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.Matrix4f;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRenderer.class)
public class GameRendererMixin implements AnimatableScreen {

    @Shadow @Final private MinecraftClient client;
    @Getter @Nullable private AnimationShader animation;

    @Inject(at = @At(value = "TAIL"), method = "render", locals = LocalCapture.CAPTURE_FAILSOFT)
    private void render(
            float tickDelta, long startTime, boolean tick1, CallbackInfo ci, int i, int j, Window window, Matrix4f matrix4f, MatrixStack matrixStack, MatrixStack matrixStack2
    ) {
        if (client.currentScreen == null && Facelift.lastScreen != null) {
            renderAnimation(matrixStack2, i, j, client.getLastFrameDuration());
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V"), method = "render", cancellable = true)
    private void renderScreen(
            float tickDelta, long startTime, boolean tick, CallbackInfo ci
    ) {
        AnimatableScreen animate = (AnimatableScreen) client.currentScreen;
        if (animate.isNotAnimating()) {
            return;
        }
        int i = (int)(this.client.mouse.getX() * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth());
        int j = (int)(this.client.mouse.getY() * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight());
        if (!animate.renderAnimation(new MatrixStack(), i, j, client.getLastFrameDuration())) {
            return;
        }
        ci.cancel();
        try {
            if (this.client.currentScreen != null) {
                this.client.currentScreen.updateNarrator();
            }
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Narrating screen");
            CrashReportSection crashReportSection = crashReport.addElement("Screen details");
            crashReportSection.add("Screen name", () -> this.client.currentScreen.getClass().getCanonicalName());
            throw new CrashException(crashReport);
        }
    }

    @Override
    public void animate(ManagedShaderEffect effect, Runnable setShaderConfig, int duration, Easing easing) {
        Renderable renderable;
        Screen previousScreen = Facelift.lastScreen;
        renderable = (matrices, delta, mouseX, mouseY) -> previousScreen.render(matrices, mouseX, mouseY, delta);
        animation = new AnimationShader(effect, true, renderable, ((matrices, delta, mouseX, mouseY) ->
                RenderUtil.drawRectangle(matrices, 0, 0, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight(), 0)
        ), setShaderConfig);
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
