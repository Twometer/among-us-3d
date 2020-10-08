#version 330 core

uniform int modelId;

out vec4 color;

void main(void) {
    int first = modelId & 0xFF;
    int second = (modelId >> 8) & 0xFF;

    color = vec4(first / 255.0f, second / 255.f, 1.0f, 1.0f);
}