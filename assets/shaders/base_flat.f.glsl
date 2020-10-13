#version 330 core

in vec3 fragmentColor;
in vec3 fragmentNormal;
in vec2 fragmentTexture;
in vec3 fragmentAONormal;
in vec3 fragmentPos;
in float visibility;

uniform vec3 cameraPos;

layout(location = 0) out vec4 color;
layout(location = 1) out vec4 normal;

void main(void) {
    vec3 lightColor = vec3(1, 1, 1);
    vec3 ambient = lightColor * 0.25f;

    vec3 norm = normalize(fragmentNormal);
    vec3 lightDir = normalize(cameraPos - fragmentPos);
    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * lightColor;

    vec3 result = (ambient + diffuse) * fragmentColor;

    color = vec4(result, 1.0f);
    color = mix(vec4(0, 0, 0, 1), color, visibility);
    normal = vec4(fragmentNormal, 1.0);
}