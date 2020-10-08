#version 140
in vec2 position;

out vec2 blurTextureCoords[5];

uniform float targetHeight;

void main(void){
    gl_Position = vec4(position, 0.0, 1.0);
    vec2 centerTexCoords = position * 0.5 + 0.5;
    float pixelSize = 1.0 / targetHeight;
    for (int i = -2; i <= 2; i++)
    {
        blurTextureCoords[i+2] =centerTexCoords + vec2(0.0, pixelSize * i * 2);
    }
}