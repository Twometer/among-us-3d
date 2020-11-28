package de.twometer.amongus.gui;

import com.google.gson.Gson;
import de.twometer.amongus.core.AmongUs;
import de.twometer.amongus.net.NetMessage;
import de.twometer.neko.event.Events;
import de.twometer.neko.event.KeyPressedEvent;
import de.twometer.neko.gui.Page;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public abstract class BasePage extends Page {

    protected final Gson gson = new Gson();

    protected final AmongUs amongUs;

    private final Page previous;

    public BasePage(String path) {
        super(path);
        amongUs = AmongUs.get();
        previous = amongUs.getGuiManager().getCurrentPage();
        Events.register(this);
    }

    @Override
    public void onDomReady() {
        super.onDomReady();

        if (!EventBus.getDefault().isRegistered(this))
            Events.register(this);
    }

    protected boolean escapeGoesBack() {
        return false;
    }

    protected void goBack() {
        AmongUs.get().getGuiManager().showPage(previous);
    }

    @Override
    public void onUnload() {
        super.onUnload();
        ApiGui.reset();
        Events.unregister(this);

        if (previous != null && previous instanceof IngamePage)
            AmongUs.get().getWindow().setCursorPosition(new Vector2f(
                    AmongUs.get().getWindow().getWidth() / 2.0f,
                    AmongUs.get().getWindow().getHeight() / 2.0f
            ));
    }

    protected final void showLoading(String msg) {
        context.call("showDialog", "loading");
        context.setElementText("loadingMessage", msg);
    }

    protected final void hideLoading() {
        context.call("hideDialog", "loading");
    }

    protected final void showError(String msg) {
        hideLoading();
        context.call("showDialog", "error");
        context.setElementText("errorMessage", msg);
    }

    protected final void networkError() {
        showError("Server connection failed");
    }

    protected final void sendJoinMessage(String gameCode, Runnable done) {
        amongUs.getClient().sendMessage(new NetMessage.SessionJoin(amongUs.getUserSettings().getUsername(), gameCode))
                .await(NetMessage.SessionJoined.class, r -> {
                    if (r.result == NetMessage.SessionJoined.Result.InvalidUsername)
                        showError("Invalid username!");
                    else if (r.result == NetMessage.SessionJoined.Result.LobbyFull)
                        showError("Sorry, this lobby is full!");
                    else if (r.result == NetMessage.SessionJoined.Result.UsernameTaken)
                        showError("Sorry, your username is already taken.");
                    else if (r.result == NetMessage.SessionJoined.Result.Other)
                        showError("An unknown error occurred joining a session.");
                    else if (r.result == NetMessage.SessionJoined.Result.InvalidGameCode)
                        showError("Sorry, that game does not exist.");
                    else done.run();
                }).handleError(this::networkError);
    }

    protected final void runOnUiThread(Runnable runnable) {
        AmongUs.get().getScheduler().run(runnable);
    }

    @Subscribe
    public void onKeyPress(KeyPressedEvent e) {
        if (e.key == GLFW.GLFW_KEY_ESCAPE && escapeGoesBack()) {
            goBack();
        }
    }
}
