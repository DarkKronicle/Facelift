package io.github.darkkronicle.facelift.theme;

import io.github.darkkronicle.darkkore.config.impl.ConfigObject;
import io.github.darkkronicle.darkkore.config.options.ColorOption;
import io.github.darkkronicle.darkkore.intialization.Saveable;
import io.github.darkkronicle.darkkore.util.Color;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Map;

@Accessors(fluent = true)
public class ConfigTheme implements Theme, Saveable {

    @Getter private String name = "Theme";
    @Getter private Color text = new Color(0);
    @Getter private Color base = new Color(0);
    @Getter private Color accent = new Color(0);
    @Getter private Color pop0 = new Color(0);
    @Getter private Color pop1 = new Color(0);
    @Getter private Color pop2 = new Color(0);
    @Getter private Color pop3 = new Color(0);
    @Getter private Color pop4 = new Color(0);
    @Getter private Color mantle = new Color(0);
    @Getter private Color crust = new Color(0);
    @Getter private Color subtext1 = new Color(0);
    @Getter private Color subtext0 = new Color(0);
    @Getter private Color overlay2 = new Color(0);
    @Getter private Color overlay1 = new Color(0);
    @Getter private Color overlay0 = new Color(0);
    @Getter private Color surface2 = new Color(0);
    @Getter private Color surface1 = new Color(0);
    @Getter private Color surface0 = new Color(0);

    private Map<String, Color> getNameMap() {
        return Map.ofEntries(
                Map.entry("text", text),
                Map.entry("base", base),
                Map.entry("accent", accent),
                Map.entry("pop0", pop0),
                Map.entry("pop1", pop1),
                Map.entry("pop2", pop2),
                Map.entry("pop3", pop3),
                Map.entry("pop4", pop4),
                Map.entry("mantle", mantle),
                Map.entry("crust", crust),
                Map.entry("subtext1", subtext1),
                Map.entry("subtext0", subtext0),
                Map.entry("overlay2", overlay2),
                Map.entry("overlay1", overlay1),
                Map.entry("overlay0", overlay0),
                Map.entry("surface2", surface2),
                Map.entry("surface1", surface1),
                Map.entry("surface0", surface0)
        );
    }

    private void setFromKey(String key, Color color) {
        switch (key) {
            case "text" -> text = color;
            case "base" -> base = color;
            case "accent" -> accent = color;
            case "pop0" -> pop0 = color;
            case "pop1" -> pop1 = color;
            case "pop2" -> pop2 = color;
            case "pop3" -> pop3 = color;
            case "pop4" -> pop4 = color;
            case "mantle" -> mantle = color;
            case "crust" -> crust = color;
            case "subtext1" -> subtext1 = color;
            case "subtext0" -> subtext0 = color;
            case "overlay2" -> overlay2 = color;
            case "overlay1" -> overlay1 = color;
            case "overlay0" -> overlay0 = color;
            case "surface2" -> surface2 = color;
            case "surface1" -> surface1 = color;
            case "surface0" -> surface0 = color;
        }
    }

    @Override
    public void save(ConfigObject object) {
        object.set("name", name);
        for (Map.Entry<String, Color> entry : getNameMap().entrySet()) {
            ColorOption option = new ColorOption(entry.getKey(), "", "", entry.getValue());
            option.save(object);
        }
    }

    @Override
    public void load(ConfigObject object) {
        name = (String) object.getOptional("name").orElse("Theme");
        for (Map.Entry<String, Color> entry : getNameMap().entrySet()) {
            ColorOption option = new ColorOption(entry.getKey(), "", "", new Color(0));
            option.load(object);
            setFromKey(entry.getKey(), option.getValue());
        }
    }

}
