package io.github.darkkronicle.facelift.image;

import io.github.darkkronicle.facelift.Facelift;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class ImageHandler {
    
    private final static ImageHandler INSTANCE = new ImageHandler();

    private final Map<File, CustomImage> loadedImages = new HashMap<>();

    public static ImageHandler getInstance() {
        return INSTANCE;
    }
    
    private ImageHandler() {}

    public AsyncCustomImage loadImageAsync(File file, Executor executor) {
        try {
            CustomImage image = loadedImages.get(file);
            if (image != null) {
                if (image instanceof LazyAsyncCustomImage lazy) {
                    if (lazy.isOnlyLazy()) {
                        // Unlazy it
                        lazy.createFuture();
                        return lazy;
                    }
                }
                // We only want to return async ones that need to be loaded further
                return null;
            }
            AsyncCustomImage async = new AsyncCustomImage(file, executor);
            async.createFuture();
            loadedImages.put(file, async);
            return async;
        } catch (Exception e) {
            Facelift.LOGGER.warn(e);
        }
        return null;
    }

    public CustomImage loadImage(File file) {
        try {
            CustomImage image = loadedImages.get(file);
            if (image != null) {
                return image;
            }
            image = new CustomImage(file);
            loadedImages.put(file, image);
            return image;
        } catch (Exception e) {
            Facelift.LOGGER.warn(e);
        }
        return null;
    }

    public CustomImage lazyLoadAsync(File file, ExecutorService executor) {
        try {
            CustomImage image = loadedImages.get(file);
            if (image != null) {
                return image;
            }
            LazyAsyncCustomImage async = new LazyAsyncCustomImage(file, executor);
            async.createLazyFuture();
            loadedImages.put(file, async);
            return async;
        } catch (Exception e) {
            Facelift.LOGGER.warn(e);
        }
        return null;
    }
}
