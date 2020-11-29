#version 330 core
out vec4 FragColor;

in vec2 TexCoords;

uniform sampler2D texInput;

uniform float time;

vec3 Desaturate(vec3 color, float Desaturation)
{
    vec3 grayXfer = vec3(0.3, 0.59, 0.11);
    vec3 gray = vec3(dot(grayXfer, color));
    return mix(color, gray, Desaturation);
}

void main() {
    vec2 tc = vec2(TexCoords.x, TexCoords.y);

    // Screen curve code from: https://github.com/wessles/GLSL-CRT/blob/master/shader.frag

    // Square it to smooth the edges
    float dx = abs(0.5-tc.x);
    float dy = abs(0.5-tc.y);
    dx *= dx;
    dy *= dy;

    tc.x -= 0.5;
    tc.x *= 1.0 + (dy * 0.5);
    tc.x += 0.5;

    tc.y -= 0.5;
    tc.y *= 1.0 + (dx * 0.5);
    tc.y += 0.5;

    // scanlines
    vec4 tex = texture(texInput, tc);
    vec3 gray = Desaturate(tex.rgb, 0.3);

    float scl = (sin(tc.y * 1900) + 1) * 0.5;
    float scl2 = 0.75 + (scl * 0.25);

    //vec2 size = textureSize(texInput);
    float bscl = min(max(1-sin(tc.y * 5 + time * 1), 0.9), 1.0);

    FragColor = vec4(gray, tex.a) * scl2 * bscl;

    if(tc.y > 1.0 || tc.x < 0.0 || tc.x > 1.0 || tc.y < 0.0)
        FragColor = vec4(0.0);
}