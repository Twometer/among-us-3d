#version 140

in vec2 textureCoords;
out vec4 out_Colour;
uniform sampler2D sampler;
uniform sampler2D samplerStencil;

void main(void){
    vec4 color = texture(sampler, textureCoords);
    vec4 colorStencil = texture(samplerStencil, textureCoords);
    if (colorStencil.a != 0)
        color.a = 0;
    out_Colour = color;
}