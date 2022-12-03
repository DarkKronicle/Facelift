package io.github.darkkronicle.facelift.impl;

import com.terraformersmc.modmenu.gui.ModsScreen;
import net.minecraft.client.gui.screen.Screen;

import java.util.function.Function;

public class ModMenuSupplier implements Function<Screen, Screen> {

    @Override
    public Screen apply(Screen screen) {
        return new ModsScreen(screen);
    }

}
