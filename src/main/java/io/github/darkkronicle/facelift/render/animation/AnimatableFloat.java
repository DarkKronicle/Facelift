package io.github.darkkronicle.facelift.render.animation;

import io.wispforest.owo.ui.core.Animatable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.math.MathHelper;

@AllArgsConstructor
public class AnimatableFloat implements Animatable<AnimatableFloat> {

    @Getter
    private float value;

    @Override
    public AnimatableFloat interpolate(AnimatableFloat next, float delta) {
        return new AnimatableFloat(MathHelper.lerp(delta, value, next.value));
    }

}
