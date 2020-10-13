#version 330 core

layout(location = 0) in vec3 vertexPosition;
layout(location = 2) in vec3 vertexNormal;
layout(location = 3) in vec2 vertexTexture;

uniform mat4 projMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform vec3 vertexColor;

uniform float vision;
out float visibility;
const float density = 0.815;
const float gradient = 1.25;

out vec3 fragmentAONormal;
out vec3 fragmentNormal;
out vec3 fragmentColor;
out vec2 fragmentTexture;
out vec3 fragmentPos;

void main(void) {
    mat3 normalMatrix = transpose(inverse(mat3(viewMatrix * modelMatrix)));

    gl_Position = projMatrix * viewMatrix * modelMatrix * vec4(vertexPosition, 1.0);
    fragmentPos = vec3(modelMatrix * vec4(vertexPosition, 1.0));
    fragmentNormal = vertexNormal;
    fragmentAONormal = normalMatrix * vertexNormal;
    fragmentColor = vertexColor;
    fragmentTexture = vertexTexture;

    vec4 positionRelativeToCam = viewMatrix * modelMatrix * vec4(vertexPosition, 1.0);
    float distance = length(positionRelativeToCam.xyz);
    if (distance > vision){
        visibility = exp(-pow(((distance-vision)*density), gradient));
        visibility = clamp(visibility, 0.0, 1.0);
    } else visibility = 1.0;
}