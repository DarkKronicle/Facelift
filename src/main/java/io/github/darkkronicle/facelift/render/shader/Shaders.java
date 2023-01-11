package io.github.darkkronicle.facelift.render.shader;

import io.github.darkkronicle.facelift.Facelift;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import net.minecraft.util.Identifier;

public class Shaders {

    public final static ManagedShaderEffect PANEL_ANIMATION = ShaderEffectManager.getInstance().manage(new Identifier(Facelift.MOD_ID, "shaders/post/panel_animation.json"));
    public final static ManagedShaderEffect SLIDE_ANIMATION = ShaderEffectManager.getInstance().manage(new Identifier(Facelift.MOD_ID, "shaders/post/slide_animation.json"));

    public static void init() {
        // Just need to load the class for stuff to be initialized
    }

}
