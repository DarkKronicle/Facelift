package io.github.darkkronicle.facelift.render.image;

import lombok.Getter;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class LazyAsyncCustomImage extends AsyncCustomImage {

    @Getter
    private boolean onlyLazy = true;

    protected LazyAsyncCustomImage(File file, Executor executor) {
        this(file.getName().endsWith(".gif"), file, executor);
    }

    protected LazyAsyncCustomImage(boolean gif, File file, Executor executor) {
        super(gif, file, executor);
    }

    @Override
    public void createFuture() {
        onlyLazy = false;
        super.createFuture();
    }

    public void createLazyFuture() {
        if (gif) {
            // Just load first frame if lazy
            future = CompletableFuture
                    .supplyAsync(this::setupBuffers, executor).thenComposeAsync(
                            buffers -> CompletableFuture.supplyAsync(
                                    () -> loadFrame(0), executor).thenApplyAsync(
                                            imageData -> {
                                                imageData.upload();
                                                return null;
                                            }, createRenderThreadExecutor(executor)), executor)
                    .whenComplete((r, t) -> future = null).thenApply((v) -> this);
        } else {
            onlyLazy = false;
            future = CompletableFuture.supplyAsync(this::loadImage, executor).thenApplyAsync(imageData -> {
                imageData.getTexture().getImage().upload(0, 0, 0, false);
                return null;
            }, createRenderThreadExecutor(executor)).whenComplete((r, t) -> {
                loaded = true;
                future = null;
            }).thenApply((v) -> this);
        }
    }

}
