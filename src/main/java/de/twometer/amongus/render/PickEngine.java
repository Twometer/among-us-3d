package de.twometer.amongus.render;

import de.twometer.amongus.core.AmongUs;
import de.twometer.neko.event.Events;
import de.twometer.neko.event.SizeChangedEvent;
import de.twometer.neko.gl.Framebuffer;
import de.twometer.neko.util.Log;
import org.greenrobot.eventbus.Subscribe;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public class PickEngine {

    private final ByteBuffer pickedBytes = BufferUtils.createByteBuffer(3);
    private Framebuffer pickBuffer;
    private int hoveringId;

    public void initialize() {
        Events.register(this);
        resizeBuffer();
    }

    @Subscribe
    public void resize(SizeChangedEvent e) {
        resizeBuffer();
    }

    private void resizeBuffer() {
        if (pickBuffer != null)
            pickBuffer.destroy();
        pickBuffer = Framebuffer.create()
                .withColorTexture(0)
                .withDepthBuffer()
                .finish();
    }

    public void render() {
        pickBuffer.bind();
        glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
        var strategy = new PickShadingStrategy();
        AmongUs.get().getRenderManager().setShadingStrategy(strategy);
        for (var obj : AmongUs.get().getGameObjects()) {
            strategy.canInteract = obj.canInteract();
            obj.getModel().render();
        }

        pickedBytes.clear();
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glReadPixels(pickBuffer.getWidth() / 2, pickBuffer.getHeight() / 2, 1, 1, GL_RGB, GL_UNSIGNED_BYTE, pickedBytes);

        hoveringId = pickedBytes.get(0);
        Framebuffer.unbind();
    }

    public int getHoveringId() {
        return hoveringId;
    }

}
