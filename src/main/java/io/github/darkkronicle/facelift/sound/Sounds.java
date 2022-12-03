package io.github.darkkronicle.facelift.sound;

import io.github.darkkronicle.facelift.Facelift;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Sounds {

    private Sounds() {}

    private final static Identifier HOVER_ID = new Identifier(Facelift.MOD_ID, "ui_hover");
    public final static SoundEvent HOVER = new SoundEvent(HOVER_ID);
    private final static Identifier BUTTON_CLICK_ID = new Identifier(Facelift.MOD_ID, "button_click");
    public final static SoundEvent BUTTON_CLICK = new SoundEvent(BUTTON_CLICK_ID);

    private final static Identifier MENU_MUSIC_ID = new Identifier(Facelift.MOD_ID, "menu_music");
    public final static SoundEvent MENU_MUSIC = new SoundEvent(MENU_MUSIC_ID);
    public final static MusicSound MENU = new MusicSound(MENU_MUSIC, 1, 2, true);


    public static void register() {
        Registry.register(Registry.SOUND_EVENT, HOVER_ID, HOVER);
        Registry.register(Registry.SOUND_EVENT, BUTTON_CLICK_ID, BUTTON_CLICK);
        Registry.register(Registry.SOUND_EVENT, MENU_MUSIC_ID, MENU_MUSIC);
    }

}
