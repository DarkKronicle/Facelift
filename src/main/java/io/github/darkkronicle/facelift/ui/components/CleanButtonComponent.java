package io.github.darkkronicle.facelift.ui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.darkkronicle.facelift.image.CustomImage;
import io.github.darkkronicle.facelift.sound.Sounds;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.VerticalFlowLayout;
import io.wispforest.owo.ui.core.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.x150.renderer.renderer.Renderer2d;
import me.x150.renderer.renderer.font.TTFFontRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

@Accessors(fluent = true, chain = true)
public class CleanButtonComponent extends VerticalFlowLayout {

    protected Consumer<CleanButtonComponent> onPress;

    protected int cornerRadius;
    protected int samples;
    protected AnimatableProperty<Color> backgroundColor = AnimatableProperty.of(new Color(0.4f, 0.4f, 0.4f, 0.4f));
    protected AnimatableProperty<Color> hoverColor = AnimatableProperty.of(new Color(0.6f, 0.6f, 0.6f, 0.6f));
    protected AnimatableProperty<Color> currentColor = AnimatableProperty.of(backgroundColor.get());
    protected AnimatableProperty<Color> borderColor = AnimatableProperty.of(new Color(0, 0, 0, 0));
    protected AnimatableProperty<Color> currentBorderColor = AnimatableProperty.of(borderColor.get());

    protected final static int ANIMATION_MS = 200;

    @Getter
    @Setter
    private boolean inAnimation = false;

    protected boolean doHover = true;

    public CleanButtonComponent(Sizing horizontalSizing, int cornerRadius, int samples, Consumer<CleanButtonComponent> onPress) {
        this(horizontalSizing, Sizing.content(), cornerRadius, samples, onPress);
    }

    public CleanButtonComponent(Sizing horizontalSizing, Sizing verticalSizing, int cornerRadius, int samples, Consumer<CleanButtonComponent> onPress) {
        super(horizontalSizing, verticalSizing);
        horizontalAlignment(HorizontalAlignment.CENTER);
        verticalAlignment(VerticalAlignment.CENTER);
        padding(Insets.of(3));
        this.cornerRadius = cornerRadius;
        this.onPress = onPress;
        this.samples = samples;
        this.mouseEnterEvents.source().subscribe(() -> {
            if (doHover) {
                currentColor.animate(ANIMATION_MS, Easing.SINE, hoverColor.get()).reverse();
            }
            currentBorderColor.animate(ANIMATION_MS, Easing.SINE, borderColor.get()).reverse();
            if (!inAnimation) {
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(Sounds.HOVER, 0.7f, 1.0f));
            }
        });
        this.mouseLeaveEvents.source().subscribe(() -> {
            if (doHover) {
                currentColor.animate(ANIMATION_MS, Easing.SINE, backgroundColor.get()).reverse();
            }

            Color curBorderColor = borderColor.get();
            currentBorderColor.animate(ANIMATION_MS, Easing.SINE, new Color(curBorderColor.red(), curBorderColor.green(), curBorderColor.blue(), 0)).reverse();
        });
    }

    public CleanButtonComponent doHover(boolean doHover) {
        this.doHover = doHover;
        return this;
    }

    public CleanButtonComponent backgroundColor(Color color) {
        backgroundColor.set(color);
        currentColor.set(color);
        return this;
    }

    public CleanButtonComponent borderColor(Color color) {
        borderColor.set(color);
        currentBorderColor.set(new Color(color.red(), color.green(), color.blue(), 0));
        return this;
    }

    public CleanButtonComponent hoverColor(Color color) {
        hoverColor.set(color);
        return this;
    }

    public CleanButtonComponent text(Text text) {
        child(Components.label(text));
        return this;
    }

    public CleanButtonComponent texture(CustomImage image, int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight, Insets padding, Color color) {
        TextureComponent comp = new TextureComponent(image, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
        comp.margins(padding);
        if (color != null) {
            comp.color(color);
        }
        child(comp);
        return this;
    }

    public CleanButtonComponent texture(Identifier texture, int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight, Insets padding) {
        return this.texture(texture, u, v, regionWidth, regionHeight, textureWidth, textureHeight, padding, null);
    }

    public CleanButtonComponent texture(Identifier texture, int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight, Insets padding, Color color) {
        TextureComponent comp = new TextureComponent(texture, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
        comp.margins(padding);
        if (color != null) {
            comp.color(color);
        }
        child(comp);
        return this;
    }

    public CleanButtonComponent cleanText(TTFFontRenderer renderer, Text text, Insets padding, Color color) {
        child(new CleanLabelComponent(renderer, text).color(color).margins(padding));
        return this;
    }



    @Override
    public void draw(MatrixStack matrices, int mouseX, int mouseY, float partialTicks, float delta) {
        currentColor.update(delta);
        currentBorderColor.update(delta);
        RenderSystem.enableBlend();
        Color renderColor = currentColor.get();
        Color renderBorderColor = currentBorderColor.get();
        if (renderBorderColor.alpha() > 0.01) {
            Renderer2d.renderRoundedQuad(
                    matrices, new me.x150.renderer.renderer.color.Color(renderBorderColor.red(), renderBorderColor.green(), renderBorderColor.blue(),
                            renderBorderColor.alpha()
                    ), x(), y(), x() + width(), y() + height(), cornerRadius, samples);
        }
        Renderer2d.renderRoundedQuad(matrices, new me.x150.renderer.renderer.color.Color(renderColor.red(), renderColor.green(), renderColor.blue(), renderColor.alpha()), x() + 1, y() + 1, x() + width() - 1, y() + height() - 1, cornerRadius, samples);
        super.draw(matrices, mouseX, mouseY, partialTicks, delta);
    }

    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        super.onMouseDown(mouseX, mouseY, button);
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(Sounds.BUTTON_CLICK, 1.2f, 1.3f));
        onPress.accept(this);
        return true;
    }

}
