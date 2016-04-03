package se.walkercrou.composer;

import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import se.walkercrou.composer.cmd.ComposerCommands;
import se.walkercrou.composer.cmd.TestCommands;
import se.walkercrou.composer.nbs.MusicPlayer;
import se.walkercrou.composer.nbs.NoteBlockStudioSong;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * Main class for Composer plugin.
 */
@Plugin(id = "se.walkercrou.composer",
        name = "Composer",
        version = "1.1.0",
        description = "Update for SpongeAPI 4.1.0",
        url = "https://ore-staging.spongepowered.org/windy/Composer",
        authors = { "windy" })
public class Composer {
    @Inject public Logger log;
    @Inject @DefaultConfig(sharedRoot = false) private Path configPath;
    @Inject @DefaultConfig(sharedRoot = false) private ConfigurationLoader<CommentedConfigurationNode> configLoader;
    private ConfigurationNode config;
    private final List<NoteBlockStudioSong> nbsTracks = new ArrayList<>();
    private final Map<UUID, MusicPlayer> musicPlayers = new HashMap<>();

    @Listener
    public void onGameStarted(GameStartedServerEvent event) {
        setupConfig();
        if (config.getNode("debugMode").getBoolean())
            new TestCommands(this).register();
        new ComposerCommands(this).register();
        loadTracks();
    }

    /**
     * Returns the specified player's {@link MusicPlayer}. If one does not exist a new one will be created.
     *
     * @param player player
     * @return music player
     */
    public MusicPlayer getMusicPlayer(Player player) {
        UUID playerId = player.getUniqueId();
        MusicPlayer mp = musicPlayers.get(playerId);
        if (mp == null)
            musicPlayers.put(playerId, mp = new MusicPlayer(this, getNbsTracks()));
        return mp;
    }

    /**
     * Returns the currently loaded {@link NoteBlockStudioSong}s.
     *
     * @return list of tracks
     */
    public List<NoteBlockStudioSong> getNbsTracks() {
        return Collections.unmodifiableList(nbsTracks);
    }

    private void loadTracks() {
        File file = new File(configPath.toFile().getParentFile(), "tracks");
        if (!file.exists())
            file.mkdirs();

        new Thread(() -> {
            double progress = 0;
            int total = file.list((d, n) -> n.endsWith(".nbs")).length;
            progress(progress);
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(file.toPath(), "*.nbs")) {
                for (Path path : stream) {
                    try {
                        nbsTracks.add(NoteBlockStudioSong.read(path.toFile()));
                        progress(++progress / total * 100);
                    } catch (IOException e) {
                        log.error("Could not read file (file is likely malformed): " + path, e);
                    }
                }
            } catch (IOException e) {
                log.error("An error occurred while loading the tracks.", e);
            }

        }).start();

    }

    private void progress(double p) {
        log.info("Loading tracks: " + (int) p + "%");
    }

    private void setupConfig() {
        File file = configPath.toFile();
        if (!file.exists())
            createDefaultConfig(file);
        try {
            config = configLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("An error occurred while loading the config file.", e);
        }
    }

    private void createDefaultConfig(File file) {
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
            Files.copy(Composer.class.getResourceAsStream("/default.conf"), configPath,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("An error occurred while creating default config.", e);
        }
    }
}
