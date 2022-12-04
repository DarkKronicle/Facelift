package io.github.darkkronicle.facelift.config.gui;

import io.github.darkkronicle.darkkore.util.render.RenderUtil;
import io.github.darkkronicle.facelift.config.FaceliftConfig;
import io.github.darkkronicle.facelift.shader.Shaders;
import io.github.darkkronicle.facelift.theme.Theme;
import io.github.darkkronicle.facelift.theme.ThemeHandler;
import io.github.darkkronicle.facelift.title.ModernTitleMenu;
import io.github.darkkronicle.facelift.ui.AnimatableOwoScreen;
import io.github.darkkronicle.facelift.ui.background.BackgroundHandler;
import io.github.darkkronicle.facelift.ui.background.BackgroundRenderer;
import io.github.darkkronicle.facelift.ui.config.BackgroundSelector;
import io.wispforest.owo.Owo;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.container.VerticalFlowLayout;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.util.UIErrorToast;
import me.x150.renderer.renderer.Renderer2d;
import me.x150.renderer.renderer.font.TTFFontRenderer;
import me.x150.renderer.renderer.util.BlurMaskFramebuffer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class BackgroundSelectorScreen extends AnimatableOwoScreen<VerticalFlowLayout> {

    private BackgroundRenderer renderer;
    private File selected;
    private TTFFontRenderer font;
    private ModernTitleMenu parent;
    private File[] files;

    public BackgroundSelectorScreen(ModernTitleMenu screen, TTFFontRenderer font) {
        super(screen, Shaders.PANEL_ANIMATION_SHADER, () -> Shaders.PANEL_ANIMATION_SHADER.setUniformValue("Panels", 5));
        parent = screen;
        this.font = font;
        this.renderer = parent.getBackground();
    }

    @Override
    public void close() {
        if (selected != null) {
            FaceliftConfig.getInstance().getBackground().setValue(selected.getName());
        }
        parent.setBackground(renderer);
        this.client.setScreen(parent);
    }

    protected void rebuildFiles() {
        File directory = FaceliftConfig.DIR.resolve("background").toFile();
        if (!directory.exists() || !directory.isDirectory()) {
            files = new File[0];
            return;
        }
        files = directory.listFiles();
    }

    @Override
    protected @NotNull OwoUIAdapter<VerticalFlowLayout> createAdapter() {
        rebuildFiles();
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    private io.wispforest.owo.ui.core.Color convert(io.github.darkkronicle.darkkore.util.Color color) {
        return new io.wispforest.owo.ui.core.Color(color.floatRed(), color.floatGreen(), color.floatBlue(), color.floatAlpha());
    }

    @Override
    protected void build(VerticalFlowLayout rootComponent) {
        super.build(rootComponent);
        Theme theme = ThemeHandler.getInstance().getConfiguredTheme();
        rootComponent.horizontalAlignment(HorizontalAlignment.CENTER);
        GridLayout grid = Containers.grid(Sizing.content(), Sizing.content(), (int) Math.ceil(files.length / 2f), 2);
        ScrollContainer<GridLayout> scroll = Containers.verticalScroll(Sizing.content(), Sizing.fill(100), grid);
        grid.horizontalAlignment(HorizontalAlignment.CENTER);
        int row = 0;
        int col = 0;
        for (File file : files) {
            if (col >= 2) {
                col = 0;
                row++;
            }
            renderer.loadAsync(Util.getMainWorkerExecutor());
            String name = file.getName();
            if (name.length() > 19) {
                name = name.substring(0, 16) + "...";
            }
            grid.child(
                    new BackgroundSelector(file, 5, 8, (button) -> {
                        selected = file;
                        renderer = BackgroundHandler.getInstance().getRenderer(file);
                        renderer.loadAsync(Util.getMainWorkerExecutor());
                    }).cleanText(font, Text.of(name), Insets.of(3), convert(theme.text()))
                      .backgroundColor(convert(theme.surface0()))
                      .borderColor(convert(theme.accent()))
                      .hoverColor(convert(theme.overlay0()))
                      .margins(Insets.of(6))
                    , row, col);
            col++;
        }
        rootComponent.child(scroll);
    }

    public void refresh() {
        client.setScreen(new BackgroundSelectorScreen(parent, font));
    }

    @Override
    protected void rawRender(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (renderer != null) {
            renderer.render(matrices, mouseX, mouseY, delta);
//            BlurMaskFramebuffer.useAndDraw(() -> {
//                Renderer2d.renderQuad(matrices, me.x150.renderer.renderer.color.Color.WHITE, 0, 0, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight());
//            }, 4f);
        }
        RenderUtil.drawRectangle(matrices, 0, 0, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight(),
                FaceliftConfig.getInstance().getBackgroundOverlay().getValue()
        );
        super.rawRender(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void filesDragged(List<Path> paths) {

        StringBuilder incompatible = new StringBuilder("These files are not supported: ");
        boolean incompatibleError = false;
        boolean anySuccess = false;
        for (Path path : paths) {
            File file = path.toFile();
            if (!file.exists() || (
                    !file.isDirectory() && !(
                            file.getName().endsWith(".png") || file.getName().endsWith(".jpg") || file.getName().endsWith(".gif")
                    )
            )) {
                incompatibleError = true;
                incompatible.append(file.getName()).append(", ");
                continue;
            }
            try {
                File copyTo = FaceliftConfig.DIR.resolve("background").resolve(file.getName()).toFile();
                int num = 0;
                String name = FilenameUtils.getBaseName(copyTo.getName());
                String ext = FilenameUtils.getExtension(copyTo.getName());
                if (ext.length() > 0) {
                    ext = "." + ext;
                }
                while (copyTo.exists()) {
                    copyTo = FaceliftConfig.DIR.resolve("background").resolve(name + num + "." + ext).toFile();
                    num++;
                }
                if (file.isDirectory()) {
                    FileUtils.copyDirectory(file, copyTo);
                } else {
                    FileUtils.copyFile(file, copyTo);
                }
                anySuccess = true;
            } catch (IOException e) {
                UIErrorToast.report(e);
            }
        }
        if (incompatibleError) {
            String error = incompatible.substring(0, incompatible.length() - 2);
            UIErrorToast.report(error);
        }
        if (anySuccess) {
            refresh();
        }
    }

}
