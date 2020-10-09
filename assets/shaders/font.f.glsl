#version 330 core

uniform sampler2D colorTexture;
uniform vec4 color;

in vec2 textureCoords;
out vec4 outColor;

const float width = 0.5;
const float edge = 0.11;

void main(void) {
    float distance = 1.0 - texture(colorTexture, textureCoords).a;
    float alpha = 1.0 - smoothstep(width, width + edge, distance);

    outColor = vec4(1.0f, 1.0f, 1.0f, alpha) * color;
}
