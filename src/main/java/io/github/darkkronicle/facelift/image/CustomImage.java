package io.github.darkkronicle.facelift.image;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import org.apache.commons.io.FileUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class CustomImage {

    @Getter protected boolean loaded = false;
    protected ImageData[] images;
    protected FrameController frameController;
    protected final boolean gif;

    private final File file;

    private Buffers buffers;

    protected CustomImage(File file) {
        this(file.getName().endsWith(".gif"), file);
    }

    protected CustomImage(boolean gif, File file) {
        this.gif = gif;
        this.file = file;
        if (!this.gif) {
            frameController = new FrameController(0, 0);
            images = new ImageData[1];
        }
    }

    public ImageData get(int frame) {
        if (images == null) {
            return null;
        }
        return images[frame];
    }

    public void reload() {
        close();
        loadImages();
    }

    protected Buffers setupBuffers() {
        try {
            buffers = ImageUtil.getGifBuffer(NativeImage.Format.RGBA, ImageUtil.getStreamByteBuffer(FileUtils.openInputStream(file)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        frameController = new FrameController(0, buffers.frames);
        images = new ImageData[buffers.frames];
        frameController.setDelays(buffers.delays);
        return buffers;
    }

    protected ImageData loadImage() {
        try {
            NativeImage image = NativeImage.read(file.getName().endsWith(".png") ? NativeImage.Format.RGBA : null, ImageUtil.getStreamByteBuffer(FileUtils.openInputStream(file)));
            NativeImageBackedTexture texture = new NativeImageBackedTexture(image);
            images[0] = new ImageData(texture, image.getWidth(), image.getHeight());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return images[0];
    }

    protected ImageData loadFrame(int i) {
        int width = buffers.width;
        int height = buffers.height;
        ByteBuffer bytes = buffers.gifBytes;
        int channel = buffers.channel;

        long offset = ImageUtil.getOffset(width, height, i, 4);

        NativeImage image = new NativeImage(
                ImageUtil.fromGl(channel),
                width,
                height,
                true,
                MemoryUtil.memAddress(bytes) + offset
        );
        NativeImageBackedTexture texture = new NativeImageBackedTexture(image);
        images[i] = new ImageData(texture, image.getWidth(), image.getHeight());
        //reset tex parameters

        return images[i];
    }

    public void loadImages() {
        if (!gif) {
            loadImage();
            images[0].getTexture().getImage().upload(0, 0, 0, false);
            return;
        }
        setupBuffers();
        for (int i = 0; i < images.length; i++) {
            loadFrame(i);
            images[i].upload();
        }
        loaded = true;
    }

    /**
     * Closes all the images. Should be used whenever needs to be removed
     */
    public void close() {
        for (ImageData texture : images) {
            texture.close();
        }
    }

    public void update(float delta) {
        if (this.gif && frameController != null) {
            frameController.update(delta);
        }
    }

    public int getCurrentFrame() {
        if (!gif || frameController == null) {
            return 0;
        }
        if (!loaded) {
            int target = frameController.getFrame();
            for (int i = target; i >= 0; i--) {
                if (images[i] != null) {
                    return i;
                }
            }
            return 0;
        }
        return frameController.getFrame();
    }

    public void setShaderTexture() {
        ImageData data = get(getCurrentFrame());
        if (data == null) {
            return;
        }
        RenderSystem.setShaderTexture(0, data.texture.getGlId());
    }

    @AllArgsConstructor
    @Value
    public static class ImageData {
        NativeImageBackedTexture texture;
        int width;
        int height;

        public void close() {
            texture.close();
        }

        /**
         * This has to be on the render thread
         */
        protected void upload() {
            GlStateManager._pixelStore(GL11.GL_UNPACK_SKIP_PIXELS, 0);
            GlStateManager._pixelStore(GL11.GL_UNPACK_SKIP_ROWS, 0);
            texture.upload();
        }
    }

    public int getWidth() {
        if (images == null || images[0] == null) {
            return -1;
        }
        return images[0].getWidth();
    }

    public int getHeight() {
        if (images == null || images[0] == null) {
            return -1;
        }
        return images[0].getHeight();
    }

    public boolean partialLoaded() {
        return images != null;
    }
}
