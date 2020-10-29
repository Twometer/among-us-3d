package de.twometer.amongus.core;

import de.twometer.amongus.gui.ApiGui;
import de.twometer.amongus.gui.LoadingPage;
import de.twometer.amongus.gui.MainMenuPage;
import de.twometer.amongus.io.FileSystem;
import de.twometer.amongus.model.ClientSession;
import de.twometer.amongus.net.client.NetClient;
import de.twometer.amongus.util.Config;
import de.twometer.amongus.util.Scheduler;
import de.twometer.amongus.util.UserSettings;
import de.twometer.neko.core.NekoApp;
import de.twometer.neko.render.filter.FrustumCullingFilter;
import de.twometer.neko.render.overlay.FXAAOverlay;
import de.twometer.neko.render.overlay.VignetteOverlay;
import de.twometer.neko.util.Log;

public class AmongUs extends NekoApp {

    // Game services
    private final StateController stateController = new StateController();
    private final Scheduler scheduler = new Scheduler();
    private final FileSystem fileSystem = new FileSystem();
    private NetClient client;
    private UserSettings userSettings;
    private ClientSession session;

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
        // Config
        Log.i("Loading configuration...");
        fileSystem.initialize();
        userSettings = fileSystem.load(UserSettings.FILE_NAME, UserSettings.class, new UserSettings());
        userSettings.save();

        // Window
        getWindow().setCursorVisible(false);
        getWindow().setIcon("Icon.png");

        getRenderManager().addModelFilter(new FrustumCullingFilter());

        // Visual effects
        reloadFxConfig();

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

        Log.i("Connecting to server: " + Config.get().getServerIp());
        client = new NetClient();
        client.connect();
    }

    @Override
    protected void onTick() {
        super.onTick();
        scheduler.update();
    }

    public void reloadFxConfig() {
        getFxManager().getSsao().setActive(userSettings.isUseAO());
        getFxManager().getSsao().setSamples(userSettings.getAoSamples());
        getFxManager().getBloom().setActive(userSettings.isUseBloom());

        getOverlayManager().removeOverlay(FXAAOverlay.class);
        if (userSettings.isUseFxaa())
            getOverlayManager().addOverlay(new FXAAOverlay());

        getOverlayManager().removeOverlay(VignetteOverlay.class);
        if (userSettings.isUseVignette())
            getOverlayManager().addOverlay(new VignetteOverlay(20.0f, 0.15f));
    }

    public StateController getStateController() {
        return stateController;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public UserSettings getUserSettings() {
        return userSettings;
    }

    public NetClient getClient() {
        return client;
    }

    public ClientSession getSession() {
        return session;
    }

    public void setSession(ClientSession session) {
        this.session = session;
    }
}
