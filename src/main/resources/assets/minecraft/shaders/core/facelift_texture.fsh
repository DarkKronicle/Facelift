#version 150

// Source: https://www.shadertoy.com/view/csX3RH

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;

in vec2 texCoord0;

out vec4 fragColor;

vec4 texture2DAA(sampler2D tex, vec2 uv) {
    vec2 texsize = vec2(textureSize(tex, 0));
    vec2 uv_texspace = uv*texsize;
    vec2 seam = floor(uv_texspace+.5);
    uv_texspace = (uv_texspace-seam)/fwidth(uv_texspace)+seam;
    uv_texspace = clamp(uv_texspace, seam-.5, seam+.5);
    return texture(tex, uv_texspace/texsize);
}


void main() {
    vec4 color = texture2DAA(Sampler0, texCoord0) * ColorModulator;//anti aliased scaling
    if (color.a == 0.0) {
        discard;
    }
    fragColor = color;
}
