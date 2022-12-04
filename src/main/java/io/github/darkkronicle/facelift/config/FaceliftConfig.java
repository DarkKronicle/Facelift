package io.github.darkkronicle.facelift.config;

import io.github.darkkronicle.darkkore.colors.ExtendedColor;
import io.github.darkkronicle.darkkore.config.ModConfig;
import io.github.darkkronicle.darkkore.config.options.ExtendedColorOption;
import io.github.darkkronicle.darkkore.config.options.Option;
import io.github.darkkronicle.darkkore.config.options.StringOption;
import io.github.darkkronicle.darkkore.util.Color;
import io.github.darkkronicle.darkkore.util.FileUtil;
import io.github.darkkronicle.facelift.Facelift;
import io.github.darkkronicle.facelift.image.ImageHandler;
import lombok.Getter;
import net.minecraft.util.Util;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class FaceliftConfig extends ModConfig {

    private final static FaceliftConfig INSTANCE = new FaceliftConfig();

    public final static Path DIR = FileUtil.getConfigDirectory().toPath().resolve(Facelift.MOD_ID);
    public final static File FILE = DIR.resolve("facelift.toml").toFile();
    private boolean firstLoad = true;

    @Getter
    private final StringOption background = new StringOption("background", "facelift.option.background", "facelift.option.info.background", "city");

    @Getter
    private final ExtendedColorOption backgroundOverlay = new ExtendedColorOption(
            "backgroundOverlay", "facelift.option.background", "facelift.option.info.background",
            new ExtendedColor(new Color(0, 0, 0, 80), ExtendedColor.ChromaOptions.getDefault())
    );

    @Getter
    private final StringOption colorTheme = new StringOption("themeColor", "facelift.option.themecolor", "facelift.option.info.themecolor", "catppuccin_mocha");

    public static FaceliftConfig getInstance() {
        return INSTANCE;
    }

    private FaceliftConfig() {}

    @Override
    public void load() {
        if (!FILE.exists()) {
            DIR.toFile().mkdirs();
        }
        super.load();
        if (firstLoad) {
            firstLoad = false;
            File toLoad = DIR.resolve("background").resolve(background.getValue()).toFile();
            if (!toLoad.exists() || toLoad.isDirectory()) {
                return;
            }
            ImageHandler.getInstance().lazyLoadAsync(toLoad, Util.getMainWorkerExecutor());
        }
    }

    @Override
    public File getFile() {
        return FILE;
    }

    @Override
    public List<Option<?>> getOptions() {
        return List.of(background, backgroundOverlay);
    }

}
