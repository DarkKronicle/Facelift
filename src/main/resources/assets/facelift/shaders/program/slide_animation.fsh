#version 330

uniform sampler2D DiffuseSampler;
uniform sampler2D AfterSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform vec2 InSize;

uniform vec2 SlideDir;
uniform float Percentage;

out vec4 fragColor;

void main() {
    float width = InSize[0];
    float height = InSize[1];
    float x = texCoord[0] / oneTexel[0];
    float y = texCoord[1] / oneTexel[1];
    float offset = -width * Percentage;
    if (x + offset < width) {
        fragColor = texture(DiffuseSampler, vec2((x + offset) / oneTexel[0], texCoord[1]));
    } else {
        fragColor = texture(DiffuseSampler, vec2((x - offset) / oneTexel[0], texCoord[1]));
    }
}