package de.twometer.amogus.render

import de.twometer.amogus.client.AmongUsClient
import de.twometer.neko.render.EffectsPipeline
import de.twometer.neko.render.FboManager
import de.twometer.neko.render.Primitives
import de.twometer.neko.render.pipeline.PipelineStep
import de.twometer.neko.res.ShaderCache
import de.twometer.neko.util.Profiler
import org.lwjgl.glfw.GLFW.glfwGetTime
import org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT
import org.lwjgl.opengl.GL11.glClear

class DepthFogFilter : PipelineStep() {

    private val shader = ShaderCache.get("postproc.depth_fog.nks")

    private val buffer = FboManager.request({
        it.addColorTexture()
    })

    override fun render(pipeline: EffectsPipeline) {
        Profiler.begin("Depth fog")
        pipeline.import("_Main").bind(4)
        shader.bind()
        shader["visionRadius"] = AmongUsClient.visionRadius
        buffer.bind()
        glClear(GL_COLOR_BUFFER_BIT)
        Primitives.unitQuad.render()
        pipeline.export("_Main", buffer.fbo.getColorTexture())
        Profiler.end()
    }

}