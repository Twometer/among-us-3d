package de.twometer.amongus.render;

import de.twometer.amongus.core.AmongUs;
import de.twometer.neko.event.Events;
import de.twometer.neko.event.SizeChangedEvent;
import de.twometer.neko.gl.Framebuffer;
import de.twometer.neko.render.shaders.CopyShader;
import org.greenrobot.eventbus.Subscribe;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

public class HighlightEngine {

    private Framebuffer highlightBuffer;

    public void initialize() {
        Events.register(this);
        resizeBuffer();
    }

    @Subscribe
    public void resize(SizeChangedEvent e) {
        resizeBuffer();
    }

    private void resizeBuffer() {
        if (highlightBuffer != null)
            highlightBuffer.destroy();
        highlightBuffer = Framebuffer.create()
                .withColorTexture(0)
                .finish();
    }

    public void render() {
        glClearColor(0, 0, 0, 0);
        highlightBuffer.bind();
        var amongUs = AmongUs.get();
        var camPos = amongUs.getCamera().getInterpolatedPosition(amongUs.getTimer().getPartial());
        var copyShader = amongUs.getShaderProvider().getShader(CopyShader.class);

        glClear(GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        var strategy = new UnshadedShadingStrategy();
        AmongUs.get().getRenderManager().setShadingStrategy(strategy);

        for (var obj : AmongUs.get().getGameObjects()) {
            if (!obj.isHighlighted()) continue;

            var model = obj.getModel();
            var radius = model.getSize().length() / 2;

            var distance = Math.abs(model.getCenter().distance(camPos) - radius);
            if (distance > 8)
                continue;

            strategy.color = obj.getHighlightColor();
            obj.getModel().render();
        }
        Framebuffer.unbind();
        copyShader.bind();
        amongUs.getPostRenderer().begin();
        amongUs.getPostRenderer().bindTexture(0, highlightBuffer.getColorTexture());
        amongUs.getPostRenderer().renderTo(null);
        amongUs.getPostRenderer().end();
    }

}
