#version 140

in vec2 textureCoords;
out vec4 out_Colour;
uniform sampler2D colorSampler;
uniform sampler2D depthSampler;



vec3 normal_from_depth(float depth, vec2 texcoords) {

  const vec2 offset1 = vec2(0.0,0.001);
  const vec2 offset2 = vec2(0.001,0.0);

  float depth1 = texture(depthSampler, texcoords + offset1).r;
  float depth2 = texture(depthSampler, texcoords + offset2).r;

  vec3 p1 = vec3(offset1, depth1 - depth);
  vec3 p2 = vec3(offset2, depth2 - depth);

  vec3 normal = cross(p1, p2);
  normal.z = -normal.z;

  return normalize(normal);
}

float saturate(float f) {
if (f > 1) f = 1;
if (f < 0) f = 0;
return f;
}

vec2 saturate(vec2  v) {
return vec2(saturate(v.x), saturate(v.y));
}

float ps_ssao()
{

  const float total_strength = 1.0;
  const float base = 0.2;

  const float area = 0.0075;
  const float falloff = 0.00001;

  const float radius = 0.006;

  const int samples = 16;
  vec3 sample_sphere[samples] = vec3[](
      vec3( 0.5381, 0.1856,-0.4319), vec3( 0.1379, 0.2486, 0.4430),
      vec3( 0.3371, 0.5679,-0.0057), vec3(-0.6999,-0.0451,-0.0019),
      vec3( 0.0689,-0.1598,-0.8547), vec3( 0.0560, 0.0069,-0.1843),
      vec3(-0.0146, 0.1402, 0.0762), vec3( 0.0100,-0.1924,-0.0344),
      vec3(-0.3577,-0.5301,-0.4358), vec3(-0.3169, 0.1063, 0.0158),
      vec3( 0.0103,-0.5869, 0.0046), vec3(-0.0897,-0.4940, 0.3287),
      vec3( 0.7119,-0.0154,-0.0918), vec3(-0.0533, 0.0596,-0.5411),
      vec3( 0.0352,-0.0631, 0.5460), vec3(-0.4776, 0.2847,-0.0271)
  );


 // Randomly chosen by fair dice roll
  vec3 random = normalize(vec3(0.24,0.31,0.19)); //normalize( texture(RandomTextureSampler, textureCoords * 4.0).rgb );

  float depth = texture(depthSampler, textureCoords).r;

  vec3 position = vec3(textureCoords, depth);
  vec3 normal = normal_from_depth(depth, textureCoords);

  float radius_depth = radius/depth;
  float occlusion = 0.0;
  for(int i=0; i < samples; i++) {

    vec3 ray = radius_depth * reflect(sample_sphere[i], random);
    vec3 hemi_ray = position + sign(dot(ray,normal)) * ray;

    float occ_depth = texture(depthSampler, saturate(hemi_ray.xy)).r;
    float difference = depth - occ_depth;

    occlusion += step(falloff, difference) * (1.0-smoothstep(falloff, area, difference));
  }

  float ao = 1.0 - total_strength * occlusion * (1.0 / samples);

float r = (ao + base);

  return saturate(r);
}


void main(void){
    float ssao = ps_ssao();
    out_Colour = vec4(texture(colorSampler, textureCoords).rgb * ssao, 1.0f);
    //out_Colour = vec4(ssao, ssao, ssao, 1.0f);
}