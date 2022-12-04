package io.github.darkkronicle.facelift;

import io.github.darkkronicle.darkkore.intialization.Initializer;
import io.github.darkkronicle.facelift.render.shader.Shaders;

public class FaceliftInit implements Initializer {


    @Override
    public void init() {
        Shaders.init();
    }

}
