package de.twometer.amongus.core;

import de.twometer.amongus.event.UpdateEvent;
import de.twometer.amongus.game.GameObject;
import de.twometer.amongus.game.GameObjectDecoder;
import de.twometer.amongus.gui.*;
import de.twometer.amongus.io.FileSystem;
import de.twometer.amongus.model.ClientSession;
import de.twometer.amongus.model.GameState;
import de.twometer.amongus.model.PlayerRole;
import de.twometer.amongus.net.client.NetClient;
import de.twometer.amongus.physics.CollidingPlayerController;
import de.twometer.amongus.render.HighlightEngine;
import de.twometer.amongus.render.PickEngine;
import de.twometer.amongus.util.Config;
import de.twometer.amongus.util.Scheduler;
import de.twometer.amongus.util.UserSettings;
import de.twometer.neko.core.NekoApp;
import de.twometer.neko.event.Events;
import de.twometer.neko.event.KeyPressedEvent;
import de.twometer.neko.event.MouseClickedEvent;
import de.twometer.neko.render.filter.FrustumCullingFilter;
import de.twometer.neko.render.light.LightSource;
import de.twometer.neko.render.model.CompositeModel;
import de.twometer.neko.render.model.ModelPart;
import de.twometer.neko.render.overlay.FXAAOverlay;
import de.twometer.neko.render.overlay.VignetteOverlay;
import de.twometer.neko.res.ModelLoader;
import de.twometer.neko.res.TextureLoader;
import de.twometer.neko.util.Log;
import org.greenrobot.eventbus.Subscribe;
import org.lwjgl.glfw.GLFW;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class AmongUs extends NekoApp {

    public static final int VERSION_ID = 1;

    // Game services
    private final StateController stateController = new StateController();
    private final Scheduler scheduler = new Scheduler();
    private final FileSystem fileSystem = new FileSystem();
    private final PickEngine pickEngine = new PickEngine();
    private final HighlightEngine highlightEngine = new HighlightEngine();
    private final SoundController soundController = new SoundController();
    private List<GameObject> gameObjects;
    private GameObject hoveringGameObject;
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

        getI18n().setFallbackLocale("en-US");

        getRenderManager().addModelFilter(new FrustumCullingFilter());

        // Visual effects
        reloadFxConfig();

        // Base map
        var skeld = ModelLoader.loadModel("TheSkeld.obj");
        skeld.streamTree()
                .filter(m -> m instanceof ModelPart && m.getName().contains("Luces"))
                .forEach(m -> getScene().addLight(new LightSource(m.getCenter())));

        var decoder = new GameObjectDecoder();
        gameObjects = new ArrayList<>();
        skeld.streamTree()
                .filter(m -> m instanceof CompositeModel)
                .map(decoder::decode)
                .filter(Objects::nonNull)
                .forEach(this::addGameObject);

        getScene().addModel(skeld);

        // Services
        pickEngine.initialize();
        highlightEngine.initialize();

        // Sky
        var skyboxCubemap = TextureLoader.loadCubemap("Sky/right.png", "Sky/left.png", "Sky/top.png", "Sky/bottom.png", "Sky/front.png", "Sky/back.png");
        getScene().getSkybox().setActive(true);
        getScene().getSkybox().setTexture(skyboxCubemap);

        // Player
        getCamera().getOffset().y = 0.65f;
        setPlayerController(new CollidingPlayerController());

        // UI
        getGuiManager().registerGlobalJsObject("_api", new ApiGui());
        getGuiManager().showPage(new MainMenuPage());

        // Sounds
        soundController.initialize();

        Log.i("Connecting to server: " + Config.get().getServerIp());
        client = new NetClient();
        client.connect();

        stateController.changeState(GameState.Menus);

        (new Thread(() -> {
            try {
                var url = new URL("https://twometer.de/amongus/version.txt");
                var in = url.openStream();
                try (var reader = new BufferedReader(new InputStreamReader(in))) {
                    var ver = Integer.parseInt(reader.readLine());
                    if (ver > VERSION_ID) {
                        var p = new JOptionPane("<html><body><h3>New version</h3>A new version is available from <a href='https://twometer.de/amongus'>https://twometer.de/amongus</a>.</body></html>", JOptionPane.INFORMATION_MESSAGE);
                        var d = p.createDialog("New version available");
                        d.setAlwaysOnTop(true);
                        d.setVisible(true);
                    }
                }
            } catch (Exception e) {
                Log.e("Update check failed", e);
            }
        })).start();
    }

    @Override
    protected void onTick() {
        super.onTick();
        scheduler.update();
        for (var obj : gameObjects)
            obj.onUpdate();
        Events.post(new UpdateEvent());

        hoveringGameObject = null;
        var clicked = pickEngine.getHoveringId();
        for (var obj : gameObjects)
            if (obj.getId() == clicked && obj.canInteract()) {
                hoveringGameObject = obj;
                return;
            }
    }

    @Override
    protected void onRender() {
        super.onRender();
        if (stateController.isRunning()) {
            pickEngine.render();
            highlightEngine.render();
        }
    }

    @Subscribe
    public void onClick(MouseClickedEvent event) {
        if (!stateController.isRunning()) return;
        if (!(getGuiManager().getCurrentPage() instanceof IngamePage))
            return;

        if (hoveringGameObject != null)
            hoveringGameObject.onClick();
    }

    @Subscribe
    public void onKeyPress(KeyPressedEvent event) {
        if (stateController.isRunning() && event.key == GLFW.GLFW_KEY_Q && AmongUs.get().getSession().getMyself().role == PlayerRole.Impostor && (getGuiManager().getCurrentPage() instanceof IngamePage)) {
            getGuiManager().showPage(new SabotagePage());
        }
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

    public void addGameObject(GameObject object) {
        gameObjects.add(object);
        object.onAdded();
    }

    public void removeGameObjects(Predicate<GameObject> selector) {
        for (var obj : gameObjects)
            if (selector.test(obj))
                obj.onRemoved();
        gameObjects.removeIf(selector);
    }

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    public PickEngine getPickEngine() {
        return pickEngine;
    }

    public GameObject getHoveringGameObject() {
        return hoveringGameObject;
    }
}
