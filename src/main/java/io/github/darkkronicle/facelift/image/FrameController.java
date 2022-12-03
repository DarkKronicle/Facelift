package io.github.darkkronicle.facelift.image;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class FrameController {

    @Getter private final int start;
    @Getter private final int end;
    @Getter private int[] delays;
    @Getter private int totalTime;
    @Getter private int frame = 0;

    private float ms = 0;

    public void setDelays(int[] delays) {
        this.delays = delays;
        totalTime = 0;
        for (int d : delays) {
            totalTime += d;
        }
    }

    public void update(float delta) {
        if (delays == null || delays.length <= 1) {
            frame = start;
            return;
        }
        ms += 50 * delta;
        ms %= totalTime;
        int currentDelay = 0;
        for (int i = 0; i < delays.length; i++) {
            int d = delays[i];
            if (currentDelay <= ms && ms < (currentDelay += d)) {
                frame = i;
                return;
            }
        }
    }
}
