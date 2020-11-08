package se.walkercrou.composer;

import com.google.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import se.walkercrou.composer.cmd.ComposerCommands;
import se.walkercrou.composer.cmd.TestCommands;
import se.walkercrou.composer.exception.CorruptedFileException;
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
@Plugin(id = "composer",
		name = "Composer",
		url = "https://github.com/sarhatabaot/Composer",
		authors = {"windy", "sarhatabaot"})
public class Composer {
	@Getter
	@Setter
	private static Composer instance;

	@Inject
	@Getter
	private Logger logger;
	@Inject
	@DefaultConfig(sharedRoot = false)
	private Path configPath;
	@Inject
	@DefaultConfig(sharedRoot = false)
	private ConfigurationLoader<CommentedConfigurationNode> configLoader;

	@Getter
	private ConfigurationNode config;

	@Getter
	private final Map<String, Playlist> playlists = new HashMap<>();

	private final List<NoteBlockStudioSong> nbsTracks = new ArrayList<>();
	private final Map<UUID, MusicPlayer> musicPlayers = new HashMap<>();

	@Listener
	public void onGameStarted(GameStartedServerEvent event) {
		setupConfig();
		setInstance(this);
		if (config.getNode("debugMode").getBoolean())
			new TestCommands(this).register();
		new ComposerCommands(this).register();
		Task.Builder taskBuilder = Task.builder();
		taskBuilder.execute(new LoadTracksRunnable()).submit(this);
		//loadTracks();
	}

	/**
	 * Returns the specified player's {@link MusicPlayer}.
	 * If one does not exist a new one will be created.
	 *
	 * @param player player
	 * @return music player
	 */
	public MusicPlayer getMusicPlayer(Player player) {
		UUID playerId = player.getUniqueId();
		MusicPlayer mp = musicPlayers.get(playerId);
		if (mp == null) {
			if(config.getNode("use-playlists").getBoolean())
				mp = new MusicPlayer(this, playlists.get(config.getNode("default-playlist").getString()).getTracks());
			else
				mp = new MusicPlayer(this, nbsTracks);
			musicPlayers.put(playerId, mp);
		}
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

	public class LoadTracksRunnable implements Runnable {


		@Override
		public void run() {
			File file = new File(configPath.toFile().getParentFile(), "tracks");
			if (!file.exists()) {
				if(file.mkdirs())
					logger.info("Created tracks folder.");
				else logger.warn("Could not create tracks folder.");
			}

			if (config.getNode("use-playlists").getBoolean()) {
				File playlistsFile = new File(configPath.toFile().getParent(), "playlists");
				if (!playlistsFile.exists()) {
					if(playlistsFile.mkdirs())
						logger.info("Created playlists folder.");
					else logger.warn("Could not create playlists folder");
				}
				file = new File(configPath.toFile().getParentFile(), "playlists/" + config.getNode("default-playlist").getString());
				if (!file.exists()) {
					if(file.mkdirs())
						logger.info("Created default playlist folder.");
					else logger.warn("Could not create default playlist folder.");
				}
				for(File playlistDir: playlistsFile.listFiles()){
					if(playlistDir.isDirectory()) {
						loadTracks(playlistDir);
						logger.info("Loaded "+playlistDir.getName());
					}

				}
			} else {
				loadTracks(file);
			}

		}


		private void loadTracks(final File file) {
			double progress = 0;
			int total = Objects.requireNonNull(file.list((d, n) -> n.endsWith(".nbs"))).length;
			progress(progress);
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(file.toPath(), "*.nbs")) {
				List<NoteBlockStudioSong> songs = new ArrayList<>();
				for (Path path : stream) {
					NoteBlockStudioSong song = addTrack(path);
					if (song != null) {
						progress(++progress / total * 100);
						songs.add(song);
					}
				}
				addTracks(file.getName(),songs);
				logger.info(String.format("Loaded %d/%d tracks.", songs.size(), total));
			} catch (IOException e) {
				logger.error("An error occurred while loading the tracks.", e);
			}
		}


		private void progress(double p) {
			getLogger().info("Loading tracks: " + (int) p + "%");
		}

		public void addTracks(final String name, final List<NoteBlockStudioSong> songs){
			if(config.getNode("use-playlists").getBoolean()){
				playlists.put(name,new Playlist(songs));
			} else {
				nbsTracks.addAll(songs);
			}
		}


		@Nullable
		private NoteBlockStudioSong addTrack(final Path path) {
			try {
				return NoteBlockStudioSong.read(path.toFile());
			} catch (IOException | CorruptedFileException e) {
				logger.error("Could not read file (file is likely malformed): "+ path);
				logger.debug(e.getMessage());
			}
			return null;
		}
	}


	private void setupConfig() {
		File file = configPath.toFile();
		if (!file.exists())
			createDefaultConfig(file);
		try {
			config = configLoader.load();
		} catch (IOException e) {
			getLogger().info(e.getMessage());
			getLogger().error("An error occurred while loading the config file.", e);
		}
	}

	private void createDefaultConfig(File file) {
		try {
			file.getParentFile().mkdirs();
			file.createNewFile();
			Files.copy(Composer.class.getResourceAsStream("/assets/se/walkercrou/composer/default.conf"), configPath,
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			getLogger().error(e.getMessage());
			logger.error("An error occurred while creating default config.", e);
		}
	}
}
