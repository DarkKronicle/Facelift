package io.github.darkkronicle.facelift.mixin;

import io.github.darkkronicle.facelift.Facelift;
import io.github.darkkronicle.facelift.sound.Sounds;
import io.github.darkkronicle.facelift.ui.AnimatableOwoScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.sound.MusicSound;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow @Nullable public Screen currentScreen;

    @Inject(at = @At(value = "TAIL"), method = "getMusicType", cancellable = true)
    private void getMusicType(CallbackInfoReturnable<MusicSound> ci) {
        ci.setReturnValue(Sounds.MENU);
    }

    @Inject(at = @At(value = "HEAD"), method = "setScreen")
    private void setScreen(Screen newScreen, CallbackInfo ci) {
        if (newScreen instanceof AnimatableOwoScreen) {
            Facelift.lastScreen = this.currentScreen;
        } else {
            Facelift.lastScreen = null;
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "getFramebuffer", cancellable = true)
    private void getFramebuffer(CallbackInfoReturnable<Framebuffer> cir) {
        if (Facelift.renderToBuffer != null) {
            cir.setReturnValue(Facelift.renderToBuffer);
        }
    }

}
