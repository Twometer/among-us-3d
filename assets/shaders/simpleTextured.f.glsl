#version 330 core

in vec3 fragmentColor;
in vec3 fragmentNormal;
in vec2 fragmentTexture;

out vec4 color;

uniform sampler2D texSampler;

void main(void) {
    vec3 norm = normalize(fragmentNormal);
    vec3 lightDir = vec3(1, 1, 0);
    float ambient = 0.5;
    float diff = max(dot(norm, lightDir), 0.25);

    color =  vec4(diff * fragmentColor, 1.0) * texture(texSampler, fragmentTexture);
}