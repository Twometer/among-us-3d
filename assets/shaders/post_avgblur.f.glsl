#version 150

in vec2 blurTextureCoords[5];
uniform sampler2D originalTexture;
out vec4 out_colour;

void main(void){
    out_colour = vec4(0.0);
    for (int i = 0; i < 5; i++) {
        vec4 col = texture(originalTexture, blurTextureCoords[i]);

        out_colour += col;
    }
    out_colour /= 5;
    if (out_colour.r > 0) out_colour.r = 1; else out_colour.r = 0;
    if (out_colour.g > 0) out_colour.g = 1; else out_colour.g = 0;
    if (out_colour.b > 0) out_colour.b = 1; else out_colour.b = 0;
    if (out_colour.a > 0)
        out_colour.a = 0.8;
}