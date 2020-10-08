#version 140

in vec2 textureCoords;
out vec4 out_Colour;
uniform sampler2D aSampler;
uniform sampler2D bSampler;

void main(void){
    vec4 A = texture(aSampler, textureCoords);
    vec4 B = texture(bSampler, textureCoords);

    out_Colour = B.r * A;
}