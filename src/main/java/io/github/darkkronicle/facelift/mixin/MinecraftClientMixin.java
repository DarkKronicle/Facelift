package io.github.darkkronicle.facelift.mixin;

import io.github.darkkronicle.facelift.Facelift;
import io.github.darkkronicle.facelift.render.screen.AnimatableScreen;
import io.github.darkkronicle.facelift.render.shader.Shaders;
import io.github.darkkronicle.facelift.sound.Sounds;
import io.wispforest.owo.ui.core.Easing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.sound.MusicSound;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow @Nullable public Screen currentScreen;

    @Shadow @Final public GameRenderer gameRenderer;

    @Inject(at = @At(value = "TAIL"), method = "getMusicType", cancellable = true)
    private void getMusicType(CallbackInfoReturnable<MusicSound> ci) {
        ci.setReturnValue(Sounds.MENU);
    }

    @Inject(at = @At(value = "HEAD"), method = "setScreen")
    private void setScreen(Screen newScreen, CallbackInfo ci) {
        Facelift.lastScreen = this.currentScreen;
        if (newScreen != null) {
            ((AnimatableScreen) newScreen).animate(
                    Shaders.PANEL_ANIMATION,
                    () -> {},
                    400,
                    Easing.SINE
            );
        } else if (this.currentScreen != null) {
            ((AnimatableScreen) this.gameRenderer).animate(Shaders.PANEL_ANIMATION, () -> Shaders.PANEL_ANIMATION.setUniformValue("Panels", 5), 500,
                    Easing.SINE);
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "getFramebuffer", cancellable = true)
    private void getFramebuffer(CallbackInfoReturnable<Framebuffer> cir) {
        if (Facelift.renderToBuffer != null) {
            cir.setReturnValue(Facelift.renderToBuffer);
        }
    }

}
