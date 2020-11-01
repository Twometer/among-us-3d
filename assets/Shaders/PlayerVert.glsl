#version 330 core

layout(location = 0) in vec3 vertexPosition;
layout(location = 2) in vec3 vertexNormal;
layout(location = 3) in vec2 vertexTexture;

uniform vec4 modelColor;
uniform vec3 playerColor;

uniform mat4 projMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform float visionRadius;

out vec3 fragmentPosition;
out vec3 fragmentNormal;
out vec2 fragmentTexture;
out vec4 fragmentColor;
out float visibility;

const float fogDensity = 0.815;
const float fogGradient = 1.25;

void main(void) {
    // Normal geometry stuff
    vec4 worldPos = modelMatrix * vec4(vertexPosition, 1.0);
    fragmentPosition = worldPos.xyz;
    gl_Position = projMatrix * viewMatrix * worldPos;

    mat3 normalMatrix = transpose(inverse(mat3(modelMatrix)));
    fragmentNormal = normalMatrix * vertexNormal;

    fragmentTexture = vertexTexture;
    fragmentColor = modelColor;

    // Vision radius
    vec4 positionRelativeToCam = viewMatrix * modelMatrix * vec4(vertexPosition, 1.0);
    float distance = length(positionRelativeToCam.xyz);
    if (distance > visionRadius) {
        visibility = exp(-pow(((distance-visionRadius)*fogDensity), fogGradient));
        visibility = clamp(visibility, 0.0, 1.0);
    } else visibility = 1.0f;

    // Recolor
    if (fragmentColor.r == 1.0f && fragmentColor.b == 1.0f) {
        fragmentColor.rgb = playerColor;
    }
}