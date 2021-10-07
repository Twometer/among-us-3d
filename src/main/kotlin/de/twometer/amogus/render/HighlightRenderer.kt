package de.twometer.amogus.render

import de.twometer.neko.render.FboManager
import de.twometer.neko.render.OpenGL
import de.twometer.neko.render.Primitives
import de.twometer.neko.res.ShaderCache
import de.twometer.neko.scene.Color
import de.twometer.neko.scene.nodes.Geometry
import de.twometer.neko.util.Profiler
import org.joml.Vector2f
import org.lwjgl.opengl.GL11.*

object HighlightRenderer {

    private var buffer = FboManager.request({
        it.addColorTexture()
    })
    private var buffer2 = FboManager.request({
        it.addColorTexture()
    }, 0.65f)
    private var buffer3 = FboManager.request({
        it.addColorTexture()
    }, 0.65f)

    private var shader = ShaderCache.get("highlight.extract.nks")
    private var dilateShader = ShaderCache.get("highlight.dilate.nks")
    private var blurShader = ShaderCache.get("highlight.blur.nks")
    private var maskShader = ShaderCache.get("highlight.mask.nks")

    fun begin() {
        Profiler.begin("Highlights")
        buffer.bind()
        glClearColor(0f, 0f, 0f, 0f)
        glClear(GL_COLOR_BUFFER_BIT)
        shader.bind()
    }

    fun addNode(node: Geometry, color: Color) {
        shader["modelMatrix"] = node.compositeTransform.matrix
        shader["highlightColor"] = color
        node.render(shader)
    }

    fun finish() {
        OpenGL.enable(GL_BLEND)

        Profiler.begin("Highlights dilate")
        buffer2.bind()
        glClear(GL_COLOR_BUFFER_BIT)
        buffer.fbo.getColorTexture().bind(4)
        dilateShader.bind()
        dilateShader["direction"] = Vector2f(0f, 1f)
        Primitives.unitQuad.render()

        buffer3.bind()
        glClear(GL_COLOR_BUFFER_BIT)
        buffer2.fbo.getColorTexture().bind(4)
        dilateShader.bind()
        dilateShader["direction"] = Vector2f(1f, 0f)
        Primitives.unitQuad.render()
        Profiler.end()

        Profiler.begin("Highlights blur")
        buffer2.bind()
        glClear(GL_COLOR_BUFFER_BIT)
        buffer3.fbo.getColorTexture().bind(4)
        blurShader.bind()
        blurShader["direction"] = Vector2f(0f, 1f)
        Primitives.unitQuad.render()

        buffer3.bind()
        glClear(GL_COLOR_BUFFER_BIT)
        buffer2.fbo.getColorTexture().bind(4)
        blurShader.bind()
        blurShader["direction"] = Vector2f(1f, 0f)
        Primitives.unitQuad.render()
        Profiler.end()

        Profiler.begin("Highlights mask")
        buffer3.unbind()
        buffer3.fbo.getColorTexture().bind(4)
        buffer.fbo.getColorTexture().bind(5)
        maskShader.bind()
        Primitives.unitQuad.render()
        Profiler.end()

        Profiler.end()
    }


}