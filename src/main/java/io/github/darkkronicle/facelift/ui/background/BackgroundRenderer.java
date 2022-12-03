package io.github.darkkronicle.facelift.ui.background;

import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface BackgroundRenderer {

    void render(MatrixStack matrices, int mouseX, int mouseY, float delta);

    void load();

    @Nullable
    CompletableFuture<Void> loadAsync(Executor executor);

}
