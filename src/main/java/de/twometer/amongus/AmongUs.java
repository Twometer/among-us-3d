package de.twometer.amongus;

import de.twometer.amongus.gui.LoadingPage;
import de.twometer.amongus.gui.MainMenuPage;
import de.twometer.neko.core.NekoApp;
import de.twometer.neko.render.filter.FrustumCullingFilter;
import de.twometer.neko.render.light.LightSource;
import de.twometer.neko.render.model.ModelPart;
import de.twometer.neko.res.ModelLoader;
import de.twometer.neko.res.TextureLoader;

public class AmongUs extends NekoApp {

    private static AmongUs instance;

    public static void main(String[] args) {
        (instance = new AmongUs()).launch("Among Us 3D", 1280, 720);
    }

    public static AmongUs get() {
        return instance;
    }

    @Override
    protected void onPreLoad() {
        getGuiManager().setLoadingScreen(new LoadingPage());
    }

    @Override
    protected void onInitialize() {
        // Window
        getWindow().setCursorVisible(false);
        getWindow().setIcon("Icon.png");

        getRenderManager().addModelFilter(new FrustumCullingFilter());

        // Visual effects
        getFxManager().getSsao().setActive(true);
        getFxManager().getSsao().setSamples(12);
        getFxManager().getBloom().setActive(true);

        // Base map
        var skeld = ModelLoader.loadModel("TheSkeld.obj");
        skeld.streamTree()
                .filter(m -> m instanceof ModelPart && m.getName().contains("Luces"))
                .forEach(m -> getScene().addLight(new LightSource(m.getCenter())));
        getScene().addModel(skeld);


        // Sky
        var skyboxCubemap = TextureLoader.loadCubemap("Sky/right.png", "Sky/left.png", "Sky/top.png", "Sky/bottom.png", "Sky/front.png", "Sky/back.png");
        getScene().getSkybox().setActive(true);
        getScene().getSkybox().setTexture(skyboxCubemap);

        getGuiManager().showPage(new MainMenuPage());
    }
}