package se.walkercrou.composer.cmd;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import se.walkercrou.composer.Composer;
import se.walkercrou.composer.Score;
import se.walkercrou.composer.nbs.NoteBlockStudioSong;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Main commands for plugin.
 */
public class ComposerCommands {
    private final Composer plugin;
    private final CommandSpec list = CommandSpec.builder()
            .arguments(GenericArguments.optional(GenericArguments.integer(Text.of("page"))))
            .description(Text.of("Lists the currently loaded tracks."))
            .executor(this::listTracks)
            .build();
    private final CommandSpec main = CommandSpec.builder()
            .description(Text.of("Main parent command for plugin."))
            .child(list, "list", "list-tracks", "tracks", "track-list")
            .build();

    public ComposerCommands(Composer plugin) {
        this.plugin = plugin;
    }

    public void register() {
        Sponge.getCommandManager().register(plugin, main, "music", "composer");
    }

    /**
     * Lists the currently loaded tracks.
     *
     * @param source command source
     * @param context command context
     * @return result
     */
    public CommandResult listTracks(CommandSource source, CommandContext context) throws CommandException {
        List<NoteBlockStudioSong> tracks = plugin.getNbsTracks();
        List<Text> trackListings = new ArrayList<>(tracks.size());

        final Player player = source instanceof Player ? (Player) source : null;
        for (int i = 0; i < tracks.size(); i++) {
            final int index = i;
            NoteBlockStudioSong track = tracks.get(i);
            trackListings.add(trackText(track)
                    .onClick(TextActions.executeCallback(src -> playSong(player, index)))
                    .build());
        }

        if (trackListings.isEmpty())
            throw new CommandException(Text.of("There are no tracks currently loaded."));

        Sponge.getServiceManager().provide(PaginationService.class).get().builder()
                .contents(trackListings)
                .title(Text.builder("Tracks").color(TextColors.GOLD).build())
                .footer(Text.builder("Click a track to play it").color(TextColors.GRAY).build())
                .paddingString("-")
                .sendTo(source);

        return CommandResult.success();
    }

    private Text.Builder trackText(NoteBlockStudioSong track) {
        return Text.builder(strOrUnknown(track.name))
                .color(TextColors.GREEN)
                .append(Text.builder(" by ").color(TextColors.GRAY).build())
                .append(Text.builder(strOrUnknown(track.ogAuthor).equals("Unknown")
                        ? strOrUnknown(track.author) : track.ogAuthor)
                        .color(TextColors.GREEN)
                        .build());
    }

    private String strOrUnknown(String str) {
        return str == null || str.isEmpty() ? "Unknown" : str;
    }

    private void playSong(Player player, int index) {
        Map<UUID, Score> nowPlaying = plugin.nowPlaying();
        UUID id = player.getUniqueId();
        Score currentSong = nowPlaying.get(id);
        NoteBlockStudioSong track = plugin.getNbsTracks().get(index);
        Score newSong = track.toScore().onFinish(() -> nowPlaying.put(id, null));
        if (currentSong != null)
            currentSong.finish();
        newSong.play(plugin, player, player.getLocation().getPosition());
        nowPlaying.put(id, newSong);
        player.sendMessage(Text.builder("Now playing: ")
                .color(TextColors.GOLD)
                .append(trackText(track).build())
                .build());
    }
}
