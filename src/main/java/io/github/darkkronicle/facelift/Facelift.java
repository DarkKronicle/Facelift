package io.github.darkkronicle.facelift;

import io.github.darkkronicle.darkkore.config.ConfigurationManager;
import io.github.darkkronicle.darkkore.intialization.InitializationHandler;
import io.github.darkkronicle.facelift.config.FaceliftConfig;
import io.github.darkkronicle.facelift.sound.Sounds;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.resource.ResourceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Environment(EnvType.CLIENT)
public class Facelift implements ClientModInitializer {

    public final static String MOD_ID = "facelift";
    public final static Logger LOGGER = LogManager.getLogger("facelift");
    public static Screen lastScreen;
    public static Framebuffer renderToBuffer;

    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new FaceliftResourceLoader());
        Sounds.register();
        InitializationHandler.getInstance().registerInitializer("facelift", 0, new FaceliftInit());
        ConfigurationManager.getInstance().add(FaceliftConfig.getInstance());
    }

}
