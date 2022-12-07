#version 330

uniform sampler2D DiffuseSampler;
uniform sampler2D vanilla;
uniform sampler2D AfterSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform vec2 InSize;

uniform int Panels;
uniform float Percentage;

out vec4 fragColor;

vec4 blend(vec4 source, vec4 dest, float alpha) {
    return vec4((source * alpha + dest * (1.0 - alpha)).rgb, source.a);
}

void main() {
    float width = InSize[0];
    float height = InSize[1];
    float panelWidth = width / Panels;
    float x = mod(texCoord[0] / oneTexel[0], panelWidth);
    if (x <= panelWidth * Percentage) {
        vec4 color = texture(AfterSampler, texCoord);
        if (color.a < 1) {
            color = blend(texture(vanilla, texCoord), color, 1 - color.a);
        }
        fragColor = color;
    } else {
        vec4 color = texture(DiffuseSampler, texCoord);
        if (color.a < 1) {
            color = blend(texture(vanilla, texCoord), color, 1 - color.a);
        }
        fragColor = color;
    }
    //    fragColor = texture(vanilla, texCoord);
}