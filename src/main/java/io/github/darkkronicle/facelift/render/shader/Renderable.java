package io.github.darkkronicle.facelift.render.shader;

import net.minecraft.client.util.math.MatrixStack;

public interface Renderable {

    void render(MatrixStack matrices, float delta, int mouseX, int mouseY);

}
