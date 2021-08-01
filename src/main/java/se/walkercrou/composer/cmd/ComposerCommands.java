package se.walkercrou.composer.cmd;

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
import org.spongepowered.api.util.annotation.NonnullByDefault;
import se.walkercrou.composer.Composer;
import se.walkercrou.composer.Playlist;
import se.walkercrou.composer.util.TextUtil;
import se.walkercrou.composer.nbs.MusicPlayer;

import static org.spongepowered.api.command.args.GenericArguments.*;
import static se.walkercrou.composer.util.TextUtil.getPlaylistName;

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
    private final CommandSpec loopTrack = CommandSpec.builder()
            .arguments(optional(player(Text.of("player"))))
            .description(Text.of("Loops a track"))
            .executor(this::loopTrack)
            .build();
    private final CommandSpec loopPlaylist = CommandSpec.builder()
            .arguments(optional(player(Text.of("player"))))
            .description(Text.of("Loops a playlist"))
            .executor(this::loopPlaylist)
            .build();
    private final CommandSpec stop = CommandSpec.builder()
            .arguments(optional(player(Text.of("player"))))
            .description(Text.of("Stops a track"))
            .executor(this::stopTrack)
            .build();
    private final CommandSpec playOnce = CommandSpec.builder()
            .arguments(flags()
                    .valueFlag(player(Text.of("player")), "p")
                    .buildWith(integer(Text.of("trackNumber")))
            )
            .description(Text.of("Plays a track once."))
            .executor(this::playOnce)
            .build();
    private final CommandSpec base = CommandSpec.builder()
            .permission("composer.musicplayer")
            .description(Text.of("Main parent command for plugin."))
            .executor(this::listTracks)
            .child(list, "list", "list-tracks", "tracks", "track-list")
            .child(play, "play", "start", ">")
            .child(pause, "pause", "||")
            .child(stop,"stop")
            .child(resume, "resume")
            .child(shuffle, "shuffle")
            .child(queue, "queue", "order")
            .child(next, "next", "skip", ">|")
            .child(previous, "previous", "back", "|<")
            .child(loopTrack,"loop","loop-track")
            .child(loopPlaylist,"loop-all","loop-playlist")
            .child(selectPlaylist,"playlist")
            .child(listPlaylists,"list-playlist","playlists")
            .child(playOnce,"play-once")
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
        final String title = getPlaylistName(plugin.getMusicPlayer(player).getPlaylist());
        TextUtil.trackList(plugin.getMusicPlayer(player).getTracks(),title).sendTo(src);
        return CommandResult.success();
    }

    @NonnullByDefault
    private CommandResult shuffleTracks(CommandSource src, CommandContext context) throws CommandException {
        Player player = getPlayer(src, context);
        plugin.getMusicPlayer(player).shuffle(player);
        return CommandResult.success();
    }

    @NonnullByDefault
    //TODO: Doesn't resume
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
        player.sendMessage(Text.builder("Paused: ")
                .color(TextColors.GOLD)
                .append(TextUtil.track(mp.getCurrentTrack()).build())
                .build());
        return CommandResult.success();
    }

    @NonnullByDefault
    private CommandResult playTrack(CommandSource src, CommandContext context) throws CommandException {
        int trackIndex = context.<Integer>getOne("trackNumber").orElse(1) - 1;
        Player player = getPlayer(src, context);
        plugin.getMusicPlayer(player).play(player, trackIndex);
        return CommandResult.success();
    }

    @NonnullByDefault
    private @NotNull CommandResult selectPlaylist(CommandSource src, CommandContext context) throws CommandException {
        Player player = getPlayer(src,context);
        Playlist playlist = context.<Playlist>getOne("playlist").orElse(null);
        if(playlist == null)
            throw new CommandException(Text.of("This playlist doesn't exist."));

        plugin.getMusicPlayer(player).setPlaylist(playlist);
        player.sendMessage(Text.builder("Selected: ")
                .color(TextColors.GOLD)
                .append(Text.of(getPlaylistName(playlist)))
                .build());
        return CommandResult.success();
    }


    @NonnullByDefault
    private CommandResult listTracks(CommandSource source, CommandContext context) throws CommandException {
        final String title = getPlaylistName(plugin.getMusicPlayer((Player)source).getPlaylist());
        if(Composer.getInstance().getConfig().getNode("use-playlists").getBoolean())
            TextUtil.trackList(plugin.getMusicPlayer((Player)source).getTracks(), title).sendTo(source);
        else
            TextUtil.trackList(plugin.getNbsTracks(), title).sendTo(source);
        return CommandResult.success();
    }

    @NonnullByDefault
    private CommandResult loopTrack(final CommandSource source, final CommandContext context) throws  CommandException {
        Player player = getPlayer(source,context);
        final MusicPlayer musicPlayer = plugin.getMusicPlayer(player);
        musicPlayer.setLoopTrack(!musicPlayer.isLoopTrack());
        musicPlayer.getCurrentTrack().toScore().onFinish(() -> {
            try {
                previousTrack(source,context);
            } catch (CommandException e) {
                e.printStackTrace();
            }
        });
        player.sendMessage(Text.builder("Turned track looping ")
                .color(TextColors.GOLD)
                .append(Text.of(onOrOff(musicPlayer.isLoopTrack()))).build());
        return CommandResult.success();
    }

    @NonnullByDefault
    private CommandResult loopPlaylist(final CommandSource source, final CommandContext context) throws CommandException {
        Player player = getPlayer(source,context);
        final MusicPlayer musicPlayer = plugin.getMusicPlayer(player);
        musicPlayer.setLoopPlaylist(!musicPlayer.isLoopPlaylist());
        player.sendMessage(Text.builder("Turned playlist looping ")
                .color(TextColors.GOLD)
                .append(onOrOff(musicPlayer.isLoopPlaylist()).build()).build());
        return CommandResult.success();
    }

    private Text.Builder onOrOff(boolean value) {
        return value ? Text.builder("on").color(TextColors.GREEN) : Text.builder("off").color(TextColors.RED);
    }

    @NonnullByDefault
    private CommandResult stopTrack(final CommandSource source, final CommandContext context) throws CommandException {
        Player player = getPlayer(source,context);
        plugin.getMusicPlayer(player).stop(player);
        return CommandResult.success();
    }

    @NonnullByDefault
    private CommandResult listPlaylists(final CommandSource source,final CommandContext context) throws CommandException {
        Player player = getPlayer(source,context);
        if(!Composer.getInstance().getConfig().getNode("use-playlists").getBoolean()){
            player.sendMessage(Text.builder("Playlists are disabled.").color(TextColors.RED).build());
            return CommandResult.success();
        }

        TextUtil.playlistList().sendTo(source);
        return CommandResult.success();
    }

    @NonnullByDefault
    private CommandResult playOnce(final  CommandSource source, final CommandContext context) throws CommandException {
        Player player = getPlayer(source,context);
        int trackIndex = context.<Integer>getOne("trackNumber").orElse(1) - 1;
        final MusicPlayer musicPlayer = plugin.getMusicPlayer(player);
        musicPlayer.play(player,trackIndex, true);
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
