#version 140

in vec2 textureCoords;
out vec4 out_Colour;
uniform mat4 projMatrix;
uniform sampler2D colorSampler;
uniform sampler2D depthSampler;
uniform sampler2D normalSampler;
uniform sampler2D randomSampler;

uniform float noiseScale;

float linearizeDepth(float d) {
    float f= 200.0;
    float n = 0.1;
    float z = (2 * n) / (f + n - d * (f - n));
    return z;
}

float saturate(float f) {
    if (f > 1) f = 1;
    if (f < 0) f = 0;
    return f;
}

vec2 saturate(vec2  v) {
    return vec2(saturate(v.x), saturate(v.y));
}

float ssao_fast()
{
    // thank u <3    http://theorangeduck.com/page/pure-depth-ssao

    const float total_strength = 1.0;
    const float base = 0.2;

    const float area = 0.00095;
    const float falloff = 0.000002;

    const float radius = 0.006;

    const int samples = 16;
    vec3 sample_sphere[samples] = vec3[](
    vec3(0.5381, 0.1856, -0.4319), vec3(0.1379, 0.2486, 0.4430),
    vec3(0.3371, 0.5679, -0.0057), vec3(-0.6999, -0.0451, -0.0019),
    vec3(0.0689, -0.1598, -0.8547), vec3(0.0560, 0.0069, -0.1843),
    vec3(-0.0146, 0.1402, 0.0762), vec3(0.0100, -0.1924, -0.0344),
    vec3(-0.3577, -0.5301, -0.4358), vec3(-0.3169, 0.1063, 0.0158),
    vec3(0.0103, -0.5869, 0.0046), vec3(-0.0897, -0.4940, 0.3287),
    vec3(0.7119, -0.0154, -0.0918), vec3(-0.0533, 0.0596, -0.5411),
    vec3(0.0352, -0.0631, 0.5460), vec3(-0.4776, 0.2847, -0.0271)
    );


    // Randomly chosen by fair dice roll
    //
    vec3 random = normalize(normalize(texture(randomSampler, textureCoords * vec2(16*250,9*250)).rgb));

    float depth = texture(depthSampler, textureCoords).r;

    vec3 position = vec3(textureCoords, depth);
    vec3 normal = normalize(texture(normalSampler, textureCoords).xyz);

    float radius_depth = radius / depth;
    float occlusion = 0.0;
    for (int i=0; i < samples; i++) {

        vec3 ray = radius_depth * reflect(sample_sphere[i], random);
        vec3 hemi_ray = position + sign(dot(ray, normal)) * ray;

        float occ_depth = texture(depthSampler, saturate(hemi_ray.xy)).r;
        float difference = depth - occ_depth;

        occlusion += step(falloff, difference) * (1.0-smoothstep(falloff, area, difference));
    }

    float ao = 1.0 - total_strength * occlusion * (1.0 / samples);
    return saturate(ao + base);
}

/*
const float totStrength = 1.0;
const float strength = 0.2;
const float offset = 17.0;
const float falloff = 0.000002;
const float rad = 0.0005;
#define SAMPLES 10// 10 is good
const float invSamples = -1.38/10.0;



float ssao() {
    vec2 uv = textureCoords;
    // these are the random vectors inside a unit sphere
    vec3 pSphere[10] = vec3[](vec3(-0.010735935, 0.01647018, 0.0062425877), vec3(-0.06533369, 0.3647007, -0.13746321), vec3(-0.6539235, -0.016726388, -0.53000957), vec3(0.40958285, 0.0052428036, -0.5591124), vec3(-0.1465366, 0.09899267, 0.15571679), vec3(-0.44122112, -0.5458797, 0.04912532), vec3(0.03755566, -0.10961345, -0.33040273), vec3(0.019100213, 0.29652783, 0.066237666), vec3(0.8765323, 0.011236004, 0.28265962), vec3(0.29264435, -0.40794238, 0.15964167));

    // grab a normal for reflecting the sample rays later on
    vec3 fres = normalize(texture2D(randomSampler, uv*offset).xyz*2.0) - vec3(1.0);

    //vec4 currentPixelSample = texture2D(normalMap, uv);

    float currentPixelDepth = linearizeDepth(texture2D(depthSampler, uv).x);

    // current fragment coords in screen space
    vec3 ep = vec3(uv.xy, currentPixelDepth);
    // get the normal of current fragment
    vec3 norm = texture2D(normalSampler, uv).xyz;

    float bl = 0.0;
    // adjust for the depth ( not shure if this is good..)
    float radD = rad/currentPixelDepth;

    //vec3 ray, se, occNorm;
    float occluderDepth, depthDifference;
    vec3 occluderNormal;
    vec3 ray;
    for (int i=0; i< SAMPLES;++i)
    {
        // get a vector (randomized inside of a sphere with radius 1.0) from a texture and reflect it
        ray = radD*reflect(pSphere[i], fres);

        // get the depth of the occluder fragment
        vec2 uv2 = ep.xy + sign(dot(ray, norm))*ray.xy;
        occluderDepth = linearizeDepth(texture2D(depthSampler, uv2).r);
        occluderNormal = texture2D(normalSampler, uv2).xyz;

        // if depthDifference is negative = occluder is behind current fragment
        depthDifference = currentPixelDepth-occluderDepth;

        // calculate the difference between the normals as a weight
        // the falloff equation, starts at falloff and is kind of 1/x^2 falling
        bl += step(falloff, depthDifference)*(1.0-dot(occluderNormal.xyz, norm))*(1.0-smoothstep(falloff, strength, depthDifference));
    }

    // output the result
    return 1.0+bl*invSamples;
}

vec3 getViewRay() {
    vec2 ndc = textureCoords * 2.0 - 1.0;
    //float thfov = tan(fov / 2.0);// can do this on the CPU
    return vec3(
        ndc.x * camThFov * camAspect,
        ndc.y * camThFov,
        1.0
    );
}

float ssao_2() {
    const vec2 noise_scale = vec2(256.0f, 192.0f);
    const float radius = 1.0f;

    vec3 origin = getViewRay() * linearizeDepth(texture2D(depthSampler, textureCoords).r);
    vec3 normal = texture(normalSampler, textureCoords).xyz * 2.0 - 1.0;
    normal = normalize(normal);

    vec3 rvec = texture2D(randomSampler, textureCoords * noise_scale).xyz * 2.0 - 1.0;
    vec3 tangent = normalize(rvec - normal * dot(rvec, normal));
    vec3 bitangent = cross(normal, tangent);
    mat3 tbn = mat3(tangent, bitangent, normal);

    float occlusion = 0.0;
    for (int i = 0; i < _kernelSize; ++i) {
        // get sample position:
        vec3 s = tbn * _kernel[i];
        s = s * radius + origin;

        // project sample position:
        vec4 offset = vec4(s, 1.0);
        offset = projMatrix * offset;
        offset.xy /= offset.w;
        offset.xy = offset.xy * 0.5 + 0.5;

        // get sample depth:
        float sampleDepth = linearizeDepth(texture(depthSampler, offset.xy).r);

        // range check & accumulate:
        float rangeCheck= abs(origin.z - sampleDepth) < radius ? 1.0 : 0.0;
        occlusion += (sampleDepth <= s.z ? 1.0 : 0.0) * rangeCheck;
    }

    return 1.0 - (occlusion / _kernelSize);
}
*/

void main(void){
    float ssao = ssao_fast();// linearizeDepth(texture2D(depthSampler, textureCoords).x);

    //out_Colour = vec4(texture(colorSampler, textureCoords).rgb * ssao, 1.0f);
    out_Colour = vec4(ssao, 0.0f, 0.0f, 1.0f);
}