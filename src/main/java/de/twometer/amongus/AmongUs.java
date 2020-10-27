package de.twometer.amongus;

import de.twometer.amongus.game.GameStateManager;
import de.twometer.amongus.gui.*;
import de.twometer.amongus.model.TaskType;
import de.twometer.amongus.util.RemoteGuiApi;
import de.twometer.amongus.util.Scheduler;
import de.twometer.neko.core.NekoApp;
import de.twometer.neko.render.filter.FrustumCullingFilter;
import de.twometer.neko.render.light.LightSource;
import de.twometer.neko.render.model.ModelPart;
import de.twometer.neko.res.ModelLoader;
import de.twometer.neko.res.TextureLoader;

public class AmongUs extends NekoApp {

    // Game services
    private final GameStateManager gameStateManager = new GameStateManager();
    private final Scheduler scheduler = new Scheduler();

    // Singleton
    private static AmongUs instance;

    public static AmongUs get() {
        return instance;
    }

    // Entry point
    public static void main(String[] args) {
        (instance = new AmongUs()).launch("Among Us 3D", 1280, 720);
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
        getGuiManager().registerGlobalJsObject("_api", new RemoteGuiApi());
        getGuiManager().showPage(new MainMenuPage());
        getGuiManager().showPage(new TaskPage(TaskType.FixLights));
    }

    @Override
    protected void onTick() {
        super.onTick();
        scheduler.update();
    }

    public GameStateManager getGameStateManager() {
        return gameStateManager;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }
}
