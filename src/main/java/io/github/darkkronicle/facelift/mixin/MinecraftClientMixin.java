package io.github.darkkronicle.facelift.mixin;

import io.github.darkkronicle.facelift.sound.Sounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.MusicSound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(at = @At(value = "TAIL"), method = "getMusicType", cancellable = true)
    private void getMusicType(CallbackInfoReturnable<MusicSound> ci) {
        ci.setReturnValue(Sounds.MENU);
    }


}
