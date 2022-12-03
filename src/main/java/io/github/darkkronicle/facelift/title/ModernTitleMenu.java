package io.github.darkkronicle.facelift.title;

import io.github.darkkronicle.darkkore.util.Color;
import io.github.darkkronicle.darkkore.util.FileUtil;
import io.github.darkkronicle.darkkore.util.render.RenderUtil;
import io.github.darkkronicle.facelift.Facelift;
import io.github.darkkronicle.facelift.FaceliftResourceLoader;
import io.github.darkkronicle.facelift.config.FaceliftConfig;
import io.github.darkkronicle.facelift.config.gui.BackgroundSelectorScreen;
import io.github.darkkronicle.facelift.impl.ModMenuSupplier;
import io.github.darkkronicle.facelift.theme.Theme;
import io.github.darkkronicle.facelift.theme.ThemeHandler;
import io.github.darkkronicle.facelift.ui.background.BackgroundHandler;
import io.github.darkkronicle.facelift.ui.background.BackgroundRenderer;
import io.github.darkkronicle.facelift.ui.background.RotatingCubeBackgroundRenderer;
import io.github.darkkronicle.facelift.ui.components.*;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.HorizontalFlowLayout;
import io.wispforest.owo.ui.container.VerticalFlowLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.core.Insets;
import lombok.Getter;
import lombok.Setter;
import me.x150.renderer.renderer.font.TTFFontRenderer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ModernTitleMenu extends AbstractTitleMenu {

    private TTFFontRenderer fontRenderer;
    private Theme theme;
    @Getter
    private BackgroundRenderer background;

    @Override
    public <T extends Element> void convertElement(T element) {
        if (element instanceof ButtonWidget button) {
            Text message = button.getMessage();
            if (
                    message.equals(Text.translatable("menu.multiplayer"))
                            || message.equals(Text.translatable("menu.online"))
                            || message.equals(Text.translatable("menu.singleplayer"))
                            || message.equals(Text.translatable("menu.options"))
                            || message.equals(Text.translatable("menu.quit"))
            ) {
                // We already recreate these ones
                return;
            }
        }
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    private io.wispforest.owo.ui.core.Color convert(Color color) {
        return new io.wispforest.owo.ui.core.Color(color.floatRed(), color.floatGreen(), color.floatBlue(), color.floatAlpha());
    }

    private CleanButtonComponent constructButton(int buttonSize, Identifier texture, int textureLength, Text text, Runnable onClick) {
        return constructButton(buttonSize, texture, textureLength, text, onClick, theme.surface0(), theme.accent(), theme.text());
    }

    private CleanButtonComponent constructButton(int buttonSize, Identifier texture, int textureLength, Text text, Runnable onClick,
                                                 Color backgroundColor, Color borderColor, Color textTexture
    ) {
        CleanButtonComponent clean = new CleanButtonComponent(
                Sizing.fixed(buttonSize), Sizing.fixed(buttonSize), 5, 8,
                (button) -> onClick.run()
        );
        clean.texture(
                     texture, 0, 0, 19, 19, textureLength, textureLength, Insets.bottom(3), convert(textTexture)
             )
             .cleanText(fontRenderer, text, Insets.top(8), convert(textTexture))
             .backgroundColor(convert(backgroundColor))
             .borderColor(convert(borderColor))
             .doHover(false)
             .margins(Insets.horizontal(2));
        return clean;
    }

    public void setBackground(BackgroundRenderer renderer) {
        background = renderer;
        BackgroundHandler.getInstance().loadRendererAsync(background);
    }

    public void setBackgroundToConfig() {
        setBackground(BackgroundHandler.getInstance().getConfiguredRenderer());
    }

    protected void setup() {
        client = MinecraftClient.getInstance();
        theme = ThemeHandler.getInstance().get("catppuccin_mocha");
        fontRenderer = TTFFontRenderer.create(
                FaceliftResourceLoader.getFont("sourcesans-light.ttf").orElseGet(() -> new Font(Font.SANS_SERIF, Font.PLAIN, 11)),
                11
        );
        setBackgroundToConfig();
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        setup();
        HorizontalFlowLayout topBar = new BackgroundHorizontalFlow(Sizing.fill(100), Sizing.content(), convert(theme.crust()));
        rootComponent.child(topBar);
        topBar.child(
                new CleanButtonComponent(
                        Sizing.content(), Sizing.content(), 5, 8, (button) -> client.setScreen(new BackgroundSelectorScreen(this, fontRenderer))
                ).cleanText(fontRenderer, Text.of("background"), Insets.of(3), convert(theme.text())).padding(Insets.of(3))
        );
        VerticalFlowLayout bottom = Containers.verticalFlow(Sizing.fill(100), Sizing.fill(100));
        bottom.verticalAlignment(VerticalAlignment.CENTER);
        bottom.horizontalAlignment(HorizontalAlignment.CENTER);

        BackgroundHorizontalFlow line = new BackgroundHorizontalFlow(Sizing.fill(100), Sizing.content(), convert(theme.base()));
        line.verticalAlignment(VerticalAlignment.CENTER);
        line.child(Containers.horizontalFlow(Sizing.fixed(8), Sizing.fixed(50)));

        int buttonSize = 60;

        ButtonPagesFlow back = new ButtonPagesFlow(Sizing.content(), Sizing.content(), convert(theme.base()), false);
        back.margins(Insets.right(3));
        line.child(back);

        line.allowOverflow(true);
        line.child(new CircleTextureComponent(new Identifier(Facelift.MOD_ID, "textures/gui/logo.png"), 0, 0, 191, 191, 191, 191, 85, 88).innerColor(convert(theme.crust())).outerColor(convert(theme.base())).zIndex(4));

        ButtonPagesFlow game = new ButtonPagesFlow(Sizing.fill(100), Sizing.content(), convert(theme.base()), true);
        line.child(game);
        game.margins(Insets.of(6));
        ButtonPagesFlow.SubMenu parent = game.createMenu();

        ButtonPagesFlow.SubMenu play = game.createMenu();

        ButtonPagesFlow.SubMenu backButton = back.createMenu();
        ButtonPagesFlow.SubMenu quitButton = back.createMenu();
        backButton.addButton(
                constructButton(
                        buttonSize, new Identifier(Facelift.MOD_ID, "textures/gui/chevron_left.png"), 80, Text.of("back"),
                        () -> {
                            game.setMenu(parent);
                            back.setMenu(quitButton);
                        }, theme.crust(), new Color(243, 139, 168, 255), theme.text()
                )
        );

        quitButton.addButton(
                constructButton(
                        buttonSize, new Identifier(Facelift.MOD_ID, "textures/gui/x.png"), 80, Text.of("quit"),
                        () -> client.scheduleStop(), theme.crust(), new Color(243, 139, 168, 255), theme.text()
                )
        );
        back.setMenu(quitButton);

        play.addButton(
                constructButton(
                        buttonSize, new Identifier(Facelift.MOD_ID, "textures/gui/user.png"), 80, Text.of("solo"),
                        () -> client.setScreen(new SelectWorldScreen(this)), theme.surface0(), theme.accent(), theme.pop2()
                )
        );
        play.addButton(
                constructButton(
                        buttonSize, new Identifier(Facelift.MOD_ID, "textures/gui/users.png"), 80, Text.of("multi"),
                        () -> client.setScreen(new MultiplayerScreen(this)), theme.surface0(), theme.accent(), theme.pop2()
                )
        );
        play.addButton(
                constructButton(
                        buttonSize, new Identifier(Facelift.MOD_ID, "textures/gui/door.png"), 80, Text.of("realms"),
                        () -> client.setScreen(new RealmsMainScreen(this)), theme.surface0(), theme.accent(), theme.pop2()
                )
        );
        parent.addButton(
                constructButton(
                        buttonSize, new Identifier(Facelift.MOD_ID, "textures/gui/player_play.png"), 80, Text.of("play"),
                        () -> {
                            game.setMenu(play);
                            back.setMenu(backButton);
                        }, theme.surface0(), theme.accent(), theme.pop2()
                )
        );
        if (FabricLoader.getInstance().isModLoaded("modmenu")) {
            // Add mod menu to here
            ButtonPagesFlow.SubMenu config = game.createMenu();
            config.addButton(
                    constructButton(
                            buttonSize, new Identifier(Facelift.MOD_ID, "textures/gui/tools.png"), 80, Text.of("game"),
                            () -> client.setScreen(new OptionsScreen(this, client.options)), theme.surface0(), theme.accent(), theme.pop1()
                    )
            );
            config.addButton(
                    constructButton(
                            buttonSize, new Identifier(Facelift.MOD_ID, "textures/gui/tool.png"), 80, Text.of("mod"),
                            () -> client.setScreen(new ModMenuSupplier().apply(this)), theme.surface0(), theme.accent(), theme.pop1()
                    )
            );
            parent.addButton(
                    constructButton(
                            buttonSize, new Identifier(Facelift.MOD_ID, "textures/gui/settings.png"), 80, Text.of("config"),
                            () -> {
                                game.setMenu(config);
                                back.setMenu(backButton);
                            }, theme.surface0(), theme.accent(), theme.pop1()
                    )
            );
        } else {
            parent.addButton(
                    constructButton(
                            buttonSize, new Identifier(Facelift.MOD_ID, "textures/gui/settings.png"), 80, Text.of("config"),
                            () -> client.setScreen(new OptionsScreen(this, client.options)), theme.surface0(), theme.accent(), theme.pop1()
                    )
            );
        }
        game.setMenu(parent);
        bottom.child(line);
        line.setRenderHeight(72);

        rootComponent.child(bottom);
    }

    @Override
    public void renderBackground(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        background.render(matrices, mouseX, mouseY, delta);
        RenderUtil.drawRectangle(matrices, 0, 0, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight(), FaceliftConfig.getInstance().getBackgroundOverlay().getValue());
    }

}
