#version 330 core

layout(location = 0) in vec3 vertexPosition;
layout(location = 1) in vec3 vertexColor;
layout(location = 2) in vec3 vertexNormal;
layout(location = 3) in vec2 vertexTexture;

uniform mat4 projMatrix;
uniform mat4 viewMatrix;

out vec3 fragmentColor;
out vec2 fragmentTexture;

void main(void) {
    gl_Position = projMatrix * viewMatrix * vec4(vertexPosition, 1.0);
    fragmentColor = vertexColor;
    fragmentTexture = vertexTexture;
}