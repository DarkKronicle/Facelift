package io.github.darkkronicle.facelift.mixin;

import net.minecraft.client.gui.screen.SplashOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SplashOverlay.class)
public interface SplashOverlayAccessor {

    @Accessor("reloadCompleteTime")
    long getReloadCompleteTime();

}
