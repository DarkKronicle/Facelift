package io.github.darkkronicle.facelift.ui;

import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import net.minecraft.client.render.Shader;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.ResourceManager;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

public class UIShaders {


    private interface ShaderInit {

        Shader loadShader(ResourceManager resourceManager, List<Pair<Shader, Consumer<Shader>>> extraShaderList) throws IOException;

    }

    public enum CustomShader {
        ANTI_ALIASED_TEXTURE((resourceManager, extraShaderList) -> new Shader(resourceManager, "facelift_texture", VertexFormats.POSITION_TEXTURE)),
        ;

        private final ShaderInit init;

        @Getter
        private Shader shader;

        CustomShader(ShaderInit onInit) {
            this.init = onInit;
        }

        private void loadShader(ResourceManager manager, List<Pair<Shader, Consumer<Shader>>> extraShaderList) throws IOException {
            Shader createdShader = init.loadShader(manager, extraShaderList);
            extraShaderList.add(Pair.of(createdShader, innerShader -> shader = innerShader));
        }

    }

    private final static UIShaders INSTANCE = new UIShaders();

    public static UIShaders getInstance() {
        return INSTANCE;
    }

    private UIShaders() {}

    public void loadShaders(ResourceManager resourceManager, List<Pair<Shader, Consumer<Shader>>> extraShaderList) throws IOException {
        for (CustomShader custom : CustomShader.values()) {
            custom.loadShader(resourceManager, extraShaderList);
        }
    }

}
