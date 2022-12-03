package io.github.darkkronicle.facelift.image;

import java.nio.ByteBuffer;

// https://github.com/d4annn/Minecraft7tv/blob/39a644b3eaec3d80944ddfab40ddccc5a3ff7804/src/main/java/com/dan/minecraft7tv/client/emote/Buffers.java
public class Buffers {

    public final ByteBuffer gifBytes;
    public final int width;
    public final int height;
    public final int frames;
    public final int[] delays;
    public final int channelCounts;
    public final int channel;

    public Buffers(ByteBuffer gifBytes, int width, int height, int frames, int[] delays, int channelCounts, int channel) {
        this.gifBytes = gifBytes;
        this.width = width;
        this.height = height;
        this.frames = frames;
        this.delays = delays;
        this.channelCounts = channelCounts;
        this.channel = channel;
    }

}
