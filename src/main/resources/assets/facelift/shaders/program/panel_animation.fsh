#version 330

uniform sampler2D DiffuseSampler;
uniform sampler2D AfterSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform vec2 InSize;

uniform int Panels;
uniform float Percentage;

out vec4 fragColor;

void main() {
    float width = InSize[0];
    float height = InSize[1];
    float panelWidth = width / Panels;
//    float x = texCoord[0] / oneTexel[0];
    float x = mod(texCoord[0] / oneTexel[0], panelWidth);
    if (x <= panelWidth * Percentage) {
        fragColor = texture(AfterSampler, texCoord);
    } else {
        fragColor = texture(DiffuseSampler, texCoord);
    }
}