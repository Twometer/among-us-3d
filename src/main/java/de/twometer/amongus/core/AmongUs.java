package de.twometer.amongus.core;

import de.twometer.amongus.gui.*;
import de.twometer.amongus.gui.ApiGui;
import de.twometer.amongus.util.Config;
import de.twometer.amongus.util.Scheduler;
import de.twometer.neko.core.NekoApp;
import de.twometer.neko.render.filter.FrustumCullingFilter;
import de.twometer.neko.util.Log;

public class AmongUs extends NekoApp {

    // Game services
    private final StateController stateController = new StateController();
    private final Scheduler scheduler = new Scheduler();

    // Singleton
    private static AmongUs instance;

    public static AmongUs get() {
        return instance;
    }

    // Entry point
    public static void launch() {
        (instance = new AmongUs()).launch("Among Us 3D", 1280, 800);
    }

    // Callbacks
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
        /*var skeld = ModelLoader.loadModel("TheSkeld.obj");
        skeld.streamTree()
                .filter(m -> m instanceof ModelPart && m.getName().contains("Luces"))
                .forEach(m -> getScene().addLight(new LightSource(m.getCenter())));
        getScene().addModel(skeld);

        // Sky
        var skyboxCubemap = TextureLoader.loadCubemap("Sky/right.png", "Sky/left.png", "Sky/top.png", "Sky/bottom.png", "Sky/front.png", "Sky/back.png");
        getScene().getSkybox().setActive(true);
        getScene().getSkybox().setTexture(skyboxCubemap);*/

        // UI
        getGuiManager().registerGlobalJsObject("_api", new ApiGui());
        getGuiManager().showPage(new MainMenuPage());

        Log.i("Server IP: " + Config.get().getServerIp());
    }

    @Override
    protected void onTick() {
        super.onTick();
        scheduler.update();
    }

    public StateController getStateController() {
        return stateController;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

}
