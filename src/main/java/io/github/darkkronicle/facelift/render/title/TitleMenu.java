package io.github.darkkronicle.facelift.render.title;

import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;

public interface TitleMenu {

    <T extends Element> void convertElement(T element);

    void renderBackground(MatrixStack matrices, int mouseX, int mouseY, float delta);

    void setClientScreen();

}
