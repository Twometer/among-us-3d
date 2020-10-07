#version 330 core

in vec3 fragmentColor;
in vec2 fragmentTexture;

out vec4 color;

uniform sampler2D texSampler;

void main(void) {
    color = vec4(1.0f, 1.0f, 1.0f, 1.0f); //vec4(fragmentColor, 1.0) * texture(texSampler, fragmentTexture);
}