#version 140

in vec2 textureCoords;
out vec4 out_Colour;
uniform sampler2D aSampler;
uniform sampler2D bSampler;
uniform sampler2D cSampler;

void main(void){
    vec4 scene = texture(aSampler, textureCoords);
    vec4 ssao = texture(bSampler, textureCoords);
    vec4 highlight = texture(cSampler, textureCoords);

    out_Colour = (max(ssao.r, 0.50) * scene) + highlight;
}