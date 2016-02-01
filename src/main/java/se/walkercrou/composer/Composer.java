package se.walkercrou.composer;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;

/**
 * Main class for Composer plugin.
 */
@Plugin(id = "composer", name = "Composer", version = "1.0.0")
public class Composer {
    @Inject public Logger log;

    @Listener
    public void onGameStarted(GameStartedServerEvent event) {
        Sponge.getEventManager().registerListeners(this, new TestCommands(this));
    }
}
