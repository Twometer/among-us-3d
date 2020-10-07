#version 330 core

in vec3 fragmentColor;
in vec3 fragmentNormal;
in vec2 fragmentTexture;

out vec4 color;

void main(void) {
    vec3 norm = normalize(fragmentNormal);
    vec3 lightDir = vec3(1, 1, 0);
    float ambient = 0.5;
    float diff = max(dot(norm, lightDir) + ambient, 0.0);

    color =  vec4(diff * fragmentColor, 1.0);

}