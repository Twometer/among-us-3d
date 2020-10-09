#version 330 core

in vec3 fragmentColor;
in vec3 fragmentNormal;
in vec2 fragmentTexture;
in vec3 fragmentAONormal;

layout(location = 0) out vec4 color;
layout(location = 1) out vec4 normal;

void main(void) {
    vec3 norm = normalize(fragmentNormal);
    vec3 lightDir = vec3(1, 1, 0);
    float ambient = 0.5;
    float diff = max(dot(norm, lightDir), 0.65);

    color = vec4(diff * fragmentColor, 1.0);
    normal = vec4(fragmentAONormal, 1.0);
}