package io.github.darkkronicle.facelift;

import io.github.darkkronicle.darkkore.config.ConfigurationManager;
import io.github.darkkronicle.darkkore.intialization.InitializationHandler;
import io.github.darkkronicle.facelift.config.FaceliftConfig;
import io.github.darkkronicle.facelift.sound.Sounds;
import io.github.darkkronicle.facelift.theme.ThemeHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Environment(EnvType.CLIENT)
public class Facelift implements ClientModInitializer {

    public final static String MOD_ID = "facelift";
    public final static Logger LOGGER = LogManager.getLogger("facelift");

    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new FaceliftResourceLoader());
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(ThemeHandler.getInstance());
        Sounds.register();
        InitializationHandler.getInstance().registerInitializer("facelift", 0, new FaceliftInit());
        ConfigurationManager.getInstance().add(FaceliftConfig.getInstance());
    }

}
