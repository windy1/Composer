package se.walkercrou.composer.cmd;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandPermissionException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import se.walkercrou.composer.Composer;
import se.walkercrou.composer.Playlist;
import se.walkercrou.composer.util.TextUtil;
import se.walkercrou.composer.nbs.MusicPlayer;

import static org.spongepowered.api.command.args.GenericArguments.*;

/**
 * Main commands for plugin.
 */
public class ComposerCommands {
    private final Composer plugin;
    private final CommandSpec list = CommandSpec.builder()
            .arguments(optional(integer(Text.of("page"))))
            .description(Text.of("Lists the currently loaded tracks."))
            .executor(this::listTracks)
            .build();
    private final CommandSpec play = CommandSpec.builder()
            .arguments(flags()
                    .valueFlag(player(Text.of("player")), "p")
                    .buildWith(integer(Text.of("trackNumber")))
            )
            .description(Text.of("Plays the specified track."))
            .executor(this::playTrack)
            .build();
    private final CommandSpec pause = CommandSpec.builder()
            .arguments(optional(player(Text.of("player"))))
            .description(Text.of("Pauses the track that is currently playing."))
            .executor(this::pauseTrack)
            .build();
    private final CommandSpec resume = CommandSpec.builder()
            .arguments(optional(player(Text.of("player"))))
            .description(Text.of("Resumes playing or starts playing the first track."))
            .executor(this::resumeTrack)
            .build();
    private final CommandSpec shuffle = CommandSpec.builder()
            .arguments(optional(player(Text.of("player"))))
            .description(Text.of("Shuffles the tracks and starts playing."))
            .executor(this::shuffleTracks)
            .build();
    private final CommandSpec queue = CommandSpec.builder()
            .arguments(optional(player(Text.of("player"))))
            .description(Text.of("Prints the specified player's play queue."))
            .executor(this::printPlayQueue)
            .build();
    private final CommandSpec next = CommandSpec.builder()
            .arguments(optional(player(Text.of("player"))))
            .description(Text.of("Starts the next song in the queue."))
            .executor(this::nextTrack)
            .build();
    private final CommandSpec previous = CommandSpec.builder()
            .arguments(optional(player(Text.of("player"))))
            .description(Text.of("Goes back to the previous song in the queue."))
            .executor(this::previousTrack)
            .build();
    private final CommandSpec selectPlaylist = CommandSpec.builder()
            .arguments(new PlaylistCommandElement(Text.of("playlist"))
                    ,optional(player(Text.of("player"))))
            .description(Text.of("Selects a playlist"))
            .executor(this::selectPlaylist)
            .build();
    private final CommandSpec listPlaylists = CommandSpec.builder()
            .description(Text.of("Lists all available playlists."))
            .executor(this::listPlaylists)
            .build();
    private final CommandSpec base = CommandSpec.builder()
            .permission("composer.musicplayer")
            .description(Text.of("Main parent command for plugin."))
            .executor(this::listTracks)
            .child(list, "list", "list-tracks", "tracks", "track-list")
            .child(play, "play", "start", ">")
            .child(pause, "pause", "stop", "||")
            .child(resume, "resume")
            .child(shuffle, "shuffle")
            .child(queue, "queue", "order")
            .child(next, "next", "skip", ">|")
            .child(previous, "previous", "back", "|<")
            .child(selectPlaylist,"playlist")
            .child(listPlaylists,"list-playlist","playlists")
            .build();

    public ComposerCommands(Composer plugin) {
        this.plugin = plugin;
    }

    public void register() {
        Sponge.getCommandManager().register(plugin, base, "music", "composer");
    }

    @NonnullByDefault
    private CommandResult previousTrack(CommandSource src, CommandContext context) throws CommandException {
        Player player = getPlayer(src, context);
        plugin.getMusicPlayer(player).previous(player);
        return CommandResult.success();
    }

    @NonnullByDefault
    private CommandResult nextTrack(CommandSource src, CommandContext context) throws CommandException {
        Player player = getPlayer(src, context);
        plugin.getMusicPlayer(player).next(player);
        return CommandResult.success();
    }

    @NonnullByDefault
    private CommandResult printPlayQueue(CommandSource src, CommandContext context) throws CommandException {
        Player player = getPlayer(src, context);
        TextUtil.trackList(plugin.getMusicPlayer(player).getTracks()).sendTo(src);
        return CommandResult.success();
    }

    @NonnullByDefault
    private CommandResult shuffleTracks(CommandSource src, CommandContext context) throws CommandException {
        Player player = getPlayer(src, context);
        plugin.getMusicPlayer(player).shuffle(player);
        return CommandResult.success();
    }

    @NonnullByDefault
    private CommandResult resumeTrack(CommandSource src, CommandContext context) throws CommandException {
        Player player = getPlayer(src, context);
        MusicPlayer mp = plugin.getMusicPlayer(player);
        if (mp.isPlaying())
            throw new CommandException(Text.of("Music already playing."));
        mp.play(player);
        return CommandResult.success();
    }

    @NonnullByDefault
    private CommandResult pauseTrack(CommandSource src, CommandContext context) throws CommandException {
        Player player = getPlayer(src, context);
        MusicPlayer mp = plugin.getMusicPlayer(player);
        if (!mp.isPlaying())
            throw new CommandException(Text.of("No music playing."));
        mp.pause();
        return CommandResult.success();
    }

    @NonnullByDefault
    private CommandResult playTrack(CommandSource src, CommandContext context) throws CommandException {
        int trackIndex = context.<Integer>getOne("trackNumber").get() - 1;
        Player player = getPlayer(src, context);
        plugin.getMusicPlayer(player).play(player, trackIndex);
        return CommandResult.success();
    }

    @NonnullByDefault
    private @NotNull CommandResult selectPlaylist(CommandSource src, CommandContext context) throws CommandException {
        Player player = getPlayer(src,context);
        Playlist playlist = context.<Playlist>getOne("playlist").get();
        if(playlist == null)
            throw new CommandException(Text.of("This playlist doesn't exist."));

        plugin.getMusicPlayer(player).setPlaylist(playlist);
        player.sendMessage(Text.builder("Selected: ")
                .color(TextColors.GOLD)
                .append(Text.of(getPlaylistName(playlist)))
                .build());
        return CommandResult.success();
    }

    private String getPlaylistName(final Playlist playlist){
        return Composer.getInstance().getPlaylists().keySet()
                .stream()
                .filter(key -> playlist.equals(Composer.getInstance().getPlaylists().get(key)))
                .findFirst().get();
    }

    @NonnullByDefault
    private CommandResult listTracks(CommandSource source, CommandContext context) throws CommandException {
        if(Composer.getInstance().getConfig().getNode("use-playlists").getBoolean())
            TextUtil.trackList(plugin.getMusicPlayer((Player)source).getTracks()).sendTo(source);
        TextUtil.trackList(plugin.getNbsTracks()).sendTo(source);
        return CommandResult.success();
    }

    @NonnullByDefault
    private CommandResult listPlaylists(final CommandSource source,final CommandContext context) throws CommandException {
        Player player = (Player) source;
        player.sendMessage(Text.of(StringUtils.join(Composer.getInstance().getPlaylists().keySet(),"\n")));
        return CommandResult.success();
    }

    private Player getPlayer(CommandSource src, CommandContext context) throws CommandException {
        Player player = context.<Player>getOne("player").orElse(null);
        if (player == null) {
            if (!(src instanceof Player))
                throw new CommandException(Text.of("Cannot run command as non-player without player argument."));
            player = (Player) src;
        } else if (!src.hasPermission("composer.musicplayer.others"))
            throw new CommandPermissionException(
                    Text.of("You do not have permission to control other player's music"));
        return player;
    }
}
