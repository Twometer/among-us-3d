#version 330 core

layout(location = 0) in vec3 vertexPosition;
layout(location = 2) in vec3 vertexNormal;
layout(location = 3) in vec2 vertexTexture;

uniform mat4 projMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;
uniform vec3 vertexColor;

out vec3 fragmentAONormal;
out vec3 fragmentNormal;
out vec3 fragmentColor;
out vec2 fragmentTexture;

void main(void) {
    mat3 normalMatrix = transpose(inverse(mat3(viewMatrix * modelMatrix)));

    gl_Position = projMatrix * viewMatrix * modelMatrix * vec4(vertexPosition, 1.0);
    fragmentNormal = vertexNormal;
    fragmentAONormal = normalMatrix * vertexNormal;
    fragmentColor = vertexColor;
    fragmentTexture = vertexTexture;
}