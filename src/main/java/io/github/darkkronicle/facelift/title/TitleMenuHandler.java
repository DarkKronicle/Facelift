package io.github.darkkronicle.facelift.title;

import net.minecraft.client.gui.screen.TitleScreen;

public class TitleMenuHandler {

    private final static TitleMenuHandler INSTANCE = new TitleMenuHandler();

    public static TitleMenuHandler getInstance() {
        return INSTANCE;
    }

    private TitleMenuHandler() {}

    public void create(TitleScreen screen) {
        TitleMenu theme = new ModernTitleMenu();
        theme.setClientScreen();
    }

}
