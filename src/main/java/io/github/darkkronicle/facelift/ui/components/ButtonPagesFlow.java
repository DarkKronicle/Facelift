package io.github.darkkronicle.facelift.ui.components;

import io.github.darkkronicle.facelift.ui.animation.AnimatableFloat;
import io.wispforest.owo.ui.container.HorizontalFlowLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

public class ButtonPagesFlow extends BackgroundHorizontalFlow {

    private final boolean rightAnimation;
    private SubMenu queuedMenu = null;
    private SubMenu currentMenu = null;
    private boolean animated = false;
    private AnimatableProperty<AnimatableFloat> size = AnimatableProperty.of(new AnimatableFloat(0));

    public ButtonPagesFlow(
            Sizing horizontalSizing, Sizing verticalSizing,
            Color color, boolean rightAnimation
    ) {
        super(horizontalSizing, verticalSizing, color);
        this.rightAnimation = rightAnimation;
    }

    public SubMenu createMenu() {
        return new SubMenu();
    }

    public void setMenu(SubMenu menu) {
        size.animate(200, Easing.QUADRATIC, new AnimatableFloat(0)).reverse();
        queuedMenu = menu;
    }

    public static class SubMenu {

        protected final List<CleanButtonComponent> buttons;

        protected SubMenu() {
            this(new ArrayList<>());
        }

        public void addButton(CleanButtonComponent component) {
            buttons.add(component);
        }

        public SubMenu(List<CleanButtonComponent> buttons) {
            this.buttons = buttons;
        }

    }

    @Override
    protected void drawChildren(MatrixStack matrices, int mouseX, int mouseY, float partialTicks, float delta, List<Component> children) {
        matrices.push();
        size.update(delta);
        float val = size.get().getValue();
        float translate;
        if (rightAnimation) {
            translate = 1 - 1 / val;
        } else {
             translate = - 1 + 1 / val;
        }
        matrices.translate(translate, 0, 0);
//        matrices.translate((x + width / 2f) * (1 - val), (y + height / 2f) * (1 - val), 0);
//        matrices.scale(1 / val, 1, 1);
        if (val <= 0 && queuedMenu != null) {
            clearChildren();
            // Have to cast into components
            children(queuedMenu.buttons.stream().map(button -> (Component) button).toList());
            queuedMenu.buttons.forEach(button -> button.inAnimation(true));
            currentMenu = queuedMenu;
            queuedMenu = null;
            size.animate(200, Easing.QUADRATIC, new AnimatableFloat(1)).reverse();
            animated = true;
        }
        if (animated && currentMenu != null && val >= 1) {
            currentMenu.buttons.forEach(button -> button.inAnimation(false));
            animated = false;
        }
        super.drawChildren(matrices, mouseX, mouseY, partialTicks, delta, children);
        matrices.pop();
    }

}
