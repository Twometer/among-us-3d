package de.twometer.amongus3d.postproc;

import de.twometer.amongus3d.core.Game;
import de.twometer.amongus3d.io.ShaderLoader;
import de.twometer.amongus3d.render.ShaderProvider;
import de.twometer.amongus3d.util.Log;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class SSAOFilter {

    private int gBuffer, gPosition, gNormal, gAlbedo;
    private int rboDepth, ssaoFBO, ssaoBlurFBO, ssaoColorBuffer, ssaoColorBufferBlur, noiseTexture;

    private static class Shader {
        private int id;

        public Shader(String vert, String frag) {
            id = ShaderLoader.loadShader("shaders/" + vert, "shaders/" + frag);
        }

        public void use() {
            glUseProgram(id);
        }

        public void setInt(String name, int val) {
            glUniform1i(glGetUniformLocation(id, name), val);
        }

        public void setVec3(String name, Vector3f vec) {
            glUniform3f(glGetUniformLocation(id, name), vec.x, vec.y, vec.z);
        }

        public void setFloat(String name, float f) {
            glUniform1f(glGetUniformLocation(id, name), f);
        }

        public void setMat4(String name, Matrix4f mat) {
            FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
            mat.get(buffer);
            glUniformMatrix4fv(glGetUniformLocation(id, name), false, buffer);
        }

    }

    public void init() {
        shaderLightingPass = new Shader("ssao.vs", "ssao_lighting.fs");
        shaderSSAO = new Shader("ssao.vs", "ssao.fs");
        shaderSSAOBlur = new Shader("ssao.vs", "ssao_blur.fs");
        setupBuffers();
    }

    private Shader shaderLightingPass;
    private Shader shaderSSAO;
    private Shader shaderSSAOBlur;

    private List<Vector3f> ssaoKernel;

    private void setupBuffers() {
        int screenWidth = Game.instance().getWindow().getWidth();
        int screenHeight = Game.instance().getWindow().getHeight();

        gBuffer = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, gBuffer);

        // position color buffer
        gPosition = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, gPosition);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, screenWidth, screenHeight, 0, GL_RGBA, GL_FLOAT, NULL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, gPosition, 0);
        // normal color buffer
        gNormal = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, gNormal);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, screenWidth, screenHeight, 0, GL_RGBA, GL_FLOAT, NULL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, GL_TEXTURE_2D, gNormal, 0);
        // color + specular color buffer
        gAlbedo = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, gAlbedo);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, screenWidth, screenHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, NULL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT2, GL_TEXTURE_2D, gAlbedo, 0);
        // tell OpenGL which color attachments we'll use (of this framebuffer) for rendering
        glDrawBuffers(new int[]{GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1, GL_COLOR_ATTACHMENT2});
        // create and attach depth buffer (renderbuffer)
        int rboDepth = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rboDepth);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, screenWidth, screenHeight);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboDepth);
        // finally check if framebuffer is complete
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            Log.w("Framebuffer not complete!");
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        // also create framebuffer to hold SSAO processing stage
        // -----------------------------------------------------
        ssaoFBO = glGenFramebuffers();
        ssaoBlurFBO = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, ssaoFBO);
        // SSAO color buffer
        ssaoColorBuffer = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, ssaoColorBuffer);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, screenWidth, screenHeight, 0, GL_RED, GL_FLOAT, NULL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, ssaoColorBuffer, 0);
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            Log.w("SSAO Framebuffer not complete!");
        // and blur stage
        glBindFramebuffer(GL_FRAMEBUFFER, ssaoBlurFBO);
        ssaoColorBufferBlur = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, ssaoColorBufferBlur);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, screenWidth, screenHeight, 0, GL_RED, GL_FLOAT, NULL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, ssaoColorBufferBlur, 0);
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            Log.w("SSAO Blur Framebuffer not complete!");
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        ssaoKernel = new ArrayList<>();
        for (int i = 0; i < 64; i++) {
            Vector3f sample = new Vector3f((float) Math.random() * 2 - 1, (float) Math.random() * 2.0f - 1.0f, (float) Math.random());
            sample = sample.normalize();
            sample = sample.mul((float) Math.random());
            float scale = i / 64.0f;
            scale = lerp(0.1f, 1.0f, scale * scale);
            sample = sample.mul(scale);
            ssaoKernel.add(sample);
        }

        FloatBuffer buffer = FloatBuffer.allocate(16 * 3);
        for (int i = 0; i < 16; i++) {
            buffer.put((float) Math.random() * 2 - 1);
            buffer.put((float) Math.random() * 2 - 1);
            buffer.put(0.0f);
        }
        buffer.flip();

        noiseTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, noiseTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA32F, 4, 4, 0, GL_RGB, GL_FLOAT, buffer);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        shaderLightingPass.use();
        shaderLightingPass.setInt("gPosition", 0);
        shaderLightingPass.setInt("gNormal", 1);
        shaderLightingPass.setInt("gAlbedo", 2);
        shaderLightingPass.setInt("ssao", 3);
        shaderSSAO.use();
        shaderSSAO.setInt("gPosition", 0);
        shaderSSAO.setInt("gNormal", 1);
        shaderSSAO.setInt("texNoise", 2);
        shaderSSAOBlur.use();
        shaderSSAOBlur.setInt("ssaoInput", 0);
    }

    private Vector3f lightPos = new Vector3f(2, 4, -2);
    private Vector3f lightColor = new Vector3f(0.2f, 0.2f, 0.7f);

    private float lerp(float a, float b, float f) {
        return a + f * (b - a);
    }

    public void begin() {
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glBindFramebuffer(GL_FRAMEBUFFER, gBuffer);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void end() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);


        // 2. generate SSAO texture
        // ------------------------
        glBindFramebuffer(GL_FRAMEBUFFER, ssaoFBO);
        glClear(GL_COLOR_BUFFER_BIT);
        shaderSSAO.use();
        // Send kernel + rotation
        for (int i = 0; i < 64; ++i)
            shaderSSAO.setVec3("samples[" + i + "]", ssaoKernel.get(i));
        shaderSSAO.setMat4("projection", Game.instance().getProjMatrix());
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, gPosition);
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, gNormal);
        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, noiseTexture);
        renderQuad();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);


        // 3. blur SSAO texture to remove noise
        // ------------------------------------
        glBindFramebuffer(GL_FRAMEBUFFER, ssaoBlurFBO);
        glClear(GL_COLOR_BUFFER_BIT);
        shaderSSAOBlur.use();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, ssaoColorBuffer);
        renderQuad();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        // 4. lighting pass: traditional deferred Blinn-Phong lighting with added screen-space ambient occlusion
        // -----------------------------------------------------------------------------------------------------
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        shaderLightingPass.use();
        // send light relevant uniforms
        Vector3f lightPosView = lightPos.mulProject(Game.instance().getViewMatrix());
        shaderLightingPass.setVec3("light.Position", lightPosView);
        shaderLightingPass.setVec3("light.Color", lightColor);
        // Update attenuation parameters
        float linear = 0.09f;
        float quadratic = 0.032f;
        shaderLightingPass.setFloat("light.Linear", linear);
        shaderLightingPass.setFloat("light.Quadratic", quadratic);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, gPosition);
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, gNormal);
        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, gAlbedo);
        glActiveTexture(GL_TEXTURE3); // add extra SSAO texture to lighting pass
        glBindTexture(GL_TEXTURE_2D, ssaoColorBufferBlur);
        renderQuad();

        System.out.println(glGetError());
    }

    int quadVAO = 0;
    int quadVBO;

    void renderQuad() {
        if (quadVAO == 0) {
            float quadVertices[] = {
                    // positions        // texture Coords
                    -1.0f, 1.0f, 0.0f, 0.0f, 1.0f,
                    -1.0f, -1.0f, 0.0f, 0.0f, 0.0f,
                    1.0f, 1.0f, 0.0f, 1.0f, 1.0f,
                    1.0f, -1.0f, 0.0f, 1.0f, 0.0f,
            };
            // setup plane VAO
            quadVAO = glGenVertexArrays();
            quadVBO = glGenBuffers();
            glBindVertexArray(quadVAO);
            glBindBuffer(GL_ARRAY_BUFFER, quadVBO);
            glBufferData(GL_ARRAY_BUFFER, quadVertices, GL_STATIC_DRAW);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * 4, 0L);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * 4, 3 * 4);
        }
        glBindVertexArray(quadVAO);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        glBindVertexArray(0);
    }

}
