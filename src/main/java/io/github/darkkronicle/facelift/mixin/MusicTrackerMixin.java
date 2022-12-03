package io.github.darkkronicle.facelift.mixin;

import io.github.darkkronicle.facelift.sound.Sounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.*;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundCategory;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicTracker.class)
public class MusicTrackerMixin {

    @Shadow private @Nullable SoundInstance current;

    @Shadow @Final private MinecraftClient client;

    @Shadow private int timeUntilNextSong;

    @Inject(at = @At("HEAD"), method = "play", cancellable = true)
    private void play(MusicSound sound, CallbackInfo ci) {
        if (sound != MusicType.MENU && sound != Sounds.MENU) {
            // Don't care about non-menu
            return;
        }
        // Set category to master to bypass restrictions
        this.current = new PositionedSoundInstance(Sounds.MENU_MUSIC.getId(), SoundCategory.MASTER, 0.6f, 1.0f, SoundInstance.createRandom(), false, 0, SoundInstance.AttenuationType.NONE, 0.0, 0.0, 0.0, true);
        if (this.current.getSound() != SoundManager.MISSING_SOUND) {
            client.getSoundManager().play(this.current);
        }
        this.timeUntilNextSong = Integer.MAX_VALUE;
        ci.cancel();
    }

}
