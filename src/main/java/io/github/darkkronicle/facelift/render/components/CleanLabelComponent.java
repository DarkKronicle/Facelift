package io.github.darkkronicle.facelift.render.components;

import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.core.Sizing;
import me.x150.renderer.renderer.color.Color;
import me.x150.renderer.renderer.font.TTFFontRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public class CleanLabelComponent extends LabelComponent {

    protected final TTFFontRenderer renderer;

    public CleanLabelComponent(TTFFontRenderer renderer, Text text) {
        super(text);
        this.renderer = renderer;
    }

    @Override
    protected int determineHorizontalContentSize(Sizing sizing) {
        int widestText = 0;
        for (OrderedText line : this.wrappedText) {
            String string = orderToString(line);
            int width = (int) Math.ceil(renderer.getStringWidth(string));
            if (width > widestText) widestText = width;
        }

        return widestText;
    }

    @Override
    protected int determineVerticalContentSize(Sizing sizing) {
        return (int) ((this.wrappedText.size() * (renderer.getFontHeight() + 2)) - 1);
    }

    public static String orderToString(OrderedText text) {
        StringBuilder renderStringBuilder = new StringBuilder();
        text.accept((index, style, codePoint) -> {
            renderStringBuilder.append(Character.toChars(codePoint));
            return true;
        });
        return renderStringBuilder.toString();
    }

    @Override
    public void draw(MatrixStack matrices, int mouseX, int mouseY, float partialTicks, float delta) {
        int x = this.x;
        int y = this.y;

        if (this.horizontalSizing.get().isContent()) {
            x += this.horizontalSizing.get().value;
        }
        if (this.verticalSizing.get().isContent()) {
            y += this.verticalSizing.get().value;
        }

        switch (this.verticalTextAlignment) {
            case CENTER -> y += (this.height - ((this.wrappedText.size() * (this.textRenderer.fontHeight + 2)) - 2)) / 2;
            case BOTTOM -> y += this.height - ((this.wrappedText.size() * (this.textRenderer.fontHeight + 2)) - 2);
        }


        for (int i = 0; i < this.wrappedText.size(); i++) {
            var renderText = this.wrappedText.get(i);
            int renderX = x;

            String renderString = orderToString(renderText);

            switch (this.horizontalTextAlignment) {
                case CENTER -> renderX += (this.width - renderer.getStringWidth(renderString)) / 2;
                case RIGHT -> renderX += this.width - renderer.getStringWidth(renderString);
            }

            io.wispforest.owo.ui.core.Color currentColor = this.color.get();
            this.renderer.drawString(matrices, renderString, renderX, y + i * 11, new Color(currentColor.red(), currentColor.green(), currentColor.blue(), currentColor.alpha()));
        }
    }
}
