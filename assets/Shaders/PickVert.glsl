#version 330 core

layout(location = 0) in vec3 vertexPosition;
layout(location = 2) in vec3 vertexNormal;
layout(location = 3) in vec2 vertexTexture;

uniform mat4 projMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

void main(void) {
    vec4 worldPos = modelMatrix * vec4(vertexPosition, 1.0);
    gl_Position = projMatrix * viewMatrix * worldPos;
}