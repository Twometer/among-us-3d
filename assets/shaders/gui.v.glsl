#version 330 core

layout(location = 0) in vec2 position;
layout(location = 1) in vec3 color;
layout(location = 3) in vec2 texCoords;

out vec2 textureCoords;
out vec4 fragmentColor;

uniform vec4 flatColor;
uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;


void main(void) {
    gl_Position = projectionMatrix * transformationMatrix * vec4(position, 0.0, 1.0);
    fragmentColor = flatColor * vec4(color, 1.0f);
    textureCoords = texCoords;
}