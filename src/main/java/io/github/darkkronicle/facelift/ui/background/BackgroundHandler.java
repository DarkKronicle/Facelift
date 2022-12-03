package io.github.darkkronicle.facelift.ui.background;

import io.github.darkkronicle.facelift.config.FaceliftConfig;
import io.github.darkkronicle.facelift.image.CustomImage;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.util.Util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class BackgroundHandler {


    public enum Type {
        IMAGE,
        ROTATING
    }

    private final static BackgroundHandler INSTANCE = new BackgroundHandler();


    public static BackgroundHandler getInstance() {
        return INSTANCE;
    }

    private BackgroundHandler() {}

    public File getConfiguredFile() {
        return FaceliftConfig.DIR.resolve("background").resolve(FaceliftConfig.getInstance().getBackground().getValue()).toFile();
    }

    public BackgroundRenderer getConfiguredRenderer() {
        return getRenderer(getConfiguredFile());
    }

    public void loadRendererAsync(BackgroundRenderer renderer) {
        renderer.loadAsync(Util.getMainWorkerExecutor());
    }

    public Type getType(File file) {
        if (file.isDirectory()) {
            return Type.ROTATING;
        }
        return Type.IMAGE;
    }

    public BackgroundRenderer getRenderer(File file) {
        if (getType(file) == Type.IMAGE) {
            return new ImageBackgroundRenderer(file);
        }
        return new RotatingCubeBackgroundRenderer(file);
    }

}
