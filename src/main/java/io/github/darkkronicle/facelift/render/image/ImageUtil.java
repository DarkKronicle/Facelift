package io.github.darkkronicle.facelift.render.image;

import lombok.experimental.UtilityClass;
import net.minecraft.client.texture.NativeImage;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

// https://github.com/d4annn/Minecraft7tv/blob/main/src/main/java/com/dan/minecraft7tv/client/utils/EmoteUtils.java
@UtilityClass
public class ImageUtil {

    public int getGifSize(File gif) {
        ImageReader reader = ImageIO.getImageReadersBySuffix("GIF").next();
        try {
            ImageInputStream in = ImageIO.createImageInputStream(gif);
            reader.setInput(in);
            return reader.getNumImages(true);
        } catch (IOException | IllegalStateException exception) {
            exception.printStackTrace();
        }
        return -1;
    }

    public ByteBuffer getStreamByteBuffer(InputStream inputStream) throws IOException {
        byte[] bytes = inputStream.readAllBytes();
        ByteBuffer bb = MemoryUtil.memAlloc(bytes.length);
        bb.put(bytes);
        bb.rewind();
        return bb;
    }

    public Buffers getGifBuffer(@Nullable NativeImage.Format format, ByteBuffer buffer) throws IOException {
        if (format != null && !format.isWriteable()) {
            throw new UnsupportedOperationException("Don't know how to read format " + format);
        } else {
            MemoryStack memoryStack = MemoryStack.stackPush();
            try {
                IntBuffer width = memoryStack.mallocInt(1);
                IntBuffer height = memoryStack.mallocInt(1);
                IntBuffer layers = memoryStack.mallocInt(1);
                IntBuffer channels = memoryStack.mallocInt(1);
                PointerBuffer delays = memoryStack.callocPointer(1);
                int form = format == null ? 0 : channel(format);
                ByteBuffer byteBuffer = STBImage.stbi_load_gif_from_memory(buffer, delays, width, height, layers, channels, form);
                int layersSize = layers.get(0);
                if (byteBuffer == null) {
                    System.out.println(STBImage.stbi_failure_reason());
                    throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
                }
                IntBuffer intBuf = delays.getIntBuffer(layersSize);
                int[] actualDelays = new int[layersSize];
                for (int i = 0; i < layersSize; i++) {
                    actualDelays[i] = intBuf.get(i);
                }
                return new Buffers(byteBuffer, width.get(0), height.get(0), layersSize, actualDelays, channels.get(0), form);
            } catch (Throwable var9) {
                if (memoryStack != null) {
                    try {
                        memoryStack.close();
                    } catch (Throwable var8) {
                        var9.addSuppressed(var8);
                    }
                }
                throw var9;
            }
        }
    }

    public long getOffset(int width, int height, int frame_index, int channel_count) {
        return (long) width * height * frame_index * channel_count;
    }

    public static NativeImage.Format fromGl(int glFormat) {
        return switch (glFormat) {
            case 1 -> NativeImage.Format.LUMINANCE;
            case 2 -> NativeImage.Format.LUMINANCE_ALPHA;
            case 3 -> NativeImage.Format.RGB;
            default -> NativeImage.Format.RGBA;
        };
    }

    public int channel(NativeImage.Format glFormat) {
        return switch (glFormat) {
            case LUMINANCE -> 1;
            case LUMINANCE_ALPHA -> 2;
            case RGB -> 3;
            default -> 4;
        };
    }

}
