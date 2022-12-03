package io.github.darkkronicle.facelift.image;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class AsyncCustomImage extends CustomImage {

    @Getter
    protected CompletableFuture<CustomImage> future;
    protected Executor executor;

    protected AsyncCustomImage(File file, Executor executor) {
        this(file.getName().endsWith(".gif"), file, executor);
    }

    protected AsyncCustomImage(boolean gif, File file, Executor executor) {
        super(gif, file);
        this.executor = executor;
    }

    public void createFuture() {
        if (gif) {
            future = CompletableFuture.supplyAsync(this::setupBuffers, executor).thenComposeAsync(buffers -> {
                CompletableFuture[] futures = new CompletableFuture[images.length];
                for (int i = 0; i < images.length; i++) {
                    final int index = i;
                    futures[i] = CompletableFuture.supplyAsync(() -> loadFrame(index), executor).thenApplyAsync(imageData -> {
                        imageData.upload();
                        return null;
                    }, createRenderThreadExecutor(executor));
                }
                return CompletableFuture.allOf(futures);
            }, executor).whenComplete((r, t) -> {
                loaded = true;
                future = null;
            }).thenApply((v) -> this);
        } else {
            future = CompletableFuture.supplyAsync(this::loadImage, executor).thenApplyAsync(imageData -> {
                imageData.getTexture().getImage().upload(0, 0, 0, false);
                return null;
            }, createRenderThreadExecutor(executor)).whenComplete((r, t) -> {
                loaded = true;
                future = null;
            }).thenApply((v) -> this);
        }
    }

    protected static Executor createRenderThreadExecutor(Executor executor) {
        return runnable -> executor.execute(() -> RenderSystem.recordRenderCall(runnable::run));
    }

}
