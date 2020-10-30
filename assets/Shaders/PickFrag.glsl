#version 330 core

out vec4 FragColor;

uniform int modelId;

void main(void) {
    float c = modelId / 255.0f;
    FragColor = vec4(c, c, c, 1.0f);
}