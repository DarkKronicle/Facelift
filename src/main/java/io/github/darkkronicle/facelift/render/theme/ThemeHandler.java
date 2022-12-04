package io.github.darkkronicle.facelift.render.theme;

import com.electronwill.nightconfig.toml.TomlFormat;
import com.electronwill.nightconfig.toml.TomlParser;
import io.github.darkkronicle.darkkore.config.impl.ConfigObject;
import io.github.darkkronicle.darkkore.config.impl.NightConfigObject;
import io.github.darkkronicle.facelift.Facelift;
import io.github.darkkronicle.facelift.config.FaceliftConfig;
import net.minecraft.resource.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ThemeHandler {

    private final static ThemeHandler INSTANCE = new ThemeHandler();

    public static ThemeHandler getInstance() {
        return INSTANCE;
    }

    private final Map<String, Theme> themes = new HashMap<>();

    private ThemeHandler() {}

    public Theme loadFromConfig(ConfigObject obj) {
        ConfigTheme config = new ConfigTheme();
        config.load(obj);
        return config;
    }

    public Theme getConfiguredTheme() {
        return get(FaceliftConfig.getInstance().getColorTheme().getValue());
    }

    public Theme loadFromToml(InputStream stream) {
        try {
            TomlParser parser = TomlFormat.instance().createParser();
            return loadFromConfig(new NightConfigObject(parser.parse(stream)));
        } catch (Exception e) {
            Facelift.LOGGER.warn("Couldn't load theme!");
            e.printStackTrace();
        }
        return null;
    }

    public void reload(ResourceManager manager) {
        themes.clear();
        manager.findResources("themes", identifier -> identifier.getPath().endsWith(".toml")).forEach(((identifier, resource) -> {
            try {
                Theme theme = loadFromToml(resource.getInputStream());
                if (theme == null) {
                    return;
                }
                themes.put(theme.name(), theme);
            } catch (IOException e) {
                Facelift.LOGGER.warn("Could not load theme " + identifier);
            }
        }));
    }

    public Theme get(String name) {
        return themes.get(name);
    }
}
