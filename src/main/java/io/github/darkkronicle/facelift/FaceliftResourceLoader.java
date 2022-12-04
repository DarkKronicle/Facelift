package io.github.darkkronicle.facelift;

import io.github.darkkronicle.facelift.config.FaceliftConfig;
import io.github.darkkronicle.facelift.render.theme.ThemeHandler;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Identifier;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FaceliftResourceLoader implements SynchronousResourceReloader, IdentifiableResourceReloadListener {

    private static final Map<String, Font> FONTS = new HashMap<>();

    public static Optional<Font> getFont(String name) {
        return Optional.ofNullable(FONTS.get(name));
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(Facelift.MOD_ID, "font");
    }

    @Override
    public void reload(ResourceManager manager) {
        if (!FaceliftConfig.DIR.resolve("background").toFile().exists()) {
            Facelift.LOGGER.info("Facelift directory not found, copying defaults");
            FaceliftConfig.DIR.toFile().mkdirs();
            manager.findResources("facelift_default", identifier -> true).forEach((identifier, resource) -> {
                File toSave = FaceliftConfig.DIR.resolve(identifier.getPath().substring("facelift_default/".length())).toFile();
                try {
                    FileUtils.copyInputStreamToFile(resource.getInputStream(), toSave);
                } catch (IOException e) {
                    Facelift.LOGGER.error("Couldn't copy file " + toSave, e);
                }
            });
        }
        FONTS.clear();
        manager.findResources("ui_font", identifier -> identifier.getPath().endsWith(".ttf")).forEach(((identifier, resource) -> {
            try {
                String[] parts = identifier.getPath().split("/");
                FONTS.put(parts[parts.length - 1], Font.createFont(Font.PLAIN, resource.getInputStream()));
            } catch (IOException | FontFormatException e) {
                Facelift.LOGGER.warn("Could not load font " + identifier);
            }
        }));
        ThemeHandler.getInstance().reload(manager);
    }
}
