package se.walkercrou.composer.util;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.ShiftClickAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import se.walkercrou.composer.Composer;
import se.walkercrou.composer.Playlist;
import se.walkercrou.composer.nbs.NoteBlockStudioSong;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for text handling.
 */
public final class TextUtil {
    private TextUtil() {
        throw new IllegalStateException("Util class");
    }



    /**
     * Returns a pagination builder for the specified tracks.
     *
     * @param tracks to build list for
     * @return pagination builder
     * @throws CommandException if list is empty
     */
    public static PaginationList.Builder trackList(List<NoteBlockStudioSong> tracks) throws CommandException {
        List<Text> trackListings = new ArrayList<>();
        for (int i = 0; i < tracks.size(); i++) {
            trackListings.add(TextUtil.track(tracks.get(i))
                    .onClick(TextActions.runCommand("/music > " + (i + 1)))
                    .onHover(TextActions.showText(Text.of("Click to play.")))
                    .append(Text.of("  "))
                    .append(Text.builder("Play Once")
                            .color(TextColors.DARK_GRAY)
                            .onClick(TextActions.runCommand("/music play-once " + (i +1)))
                            .onHover(TextActions.showText(Text.of("Click to play once.")))
                            .build()
                    )
                    .build());
        }

        if (trackListings.isEmpty())
            throw new CommandException(Text.of("There are no tracks currently loaded."));

        return Sponge.getServiceManager().provide(PaginationService.class).get().builder()
                .contents(trackListings)
                .title(Text.builder("Tracks").color(TextColors.GOLD).build())
                .header(Text.builder("Play")
                        .color(TextColors.DARK_GREEN)
                        .style(TextStyles.BOLD)
                        .onClick(TextActions.runCommand("/music resume"))
                        .onHover(TextActions.showText(Text.of("Resume a track.")))
                        .append(Text.of("  "))
                        .append(Text.builder("Pause")
                                .color(TextColors.YELLOW)
                                .onClick(TextActions.runCommand("/music pause"))
                                .onHover(TextActions.showText(Text.of("Pause a track.")))
                                .build())
                        .append(Text.of("  "))
                        .append(Text.builder("Stop")
                            .color(TextColors.RED)
                            .onClick(TextActions.runCommand("/music stop"))
                            .onHover(TextActions.showText(Text.of("Stop a track.")))
                            .build())
                        .append(Text.of("  "))
                        .append(Text.builder("Shuffle")
                                .color(TextColors.AQUA)
                                .onClick(TextActions.runCommand("/music shuffle"))
                                .onHover(TextActions.showText(Text.of("Shuffle the tracklist.")))
                                .build())
                        .append(Text.of("  "))
                        .append(Text.builder("Loop Track")
                                .color(TextColors.AQUA)
                                .onClick(TextActions.runCommand("/music loop"))
                                .onHover(TextActions.showText(Text.of("Repeat the track.")))
                                .build())
                        .append(Text.of("  "))
                        .append(Text.builder("Loop Playlist")
                                .color(TextColors.AQUA)
                                .onClick(TextActions.runCommand("/music loop-all"))
                                .onHover(TextActions.showText(Text.of("Repeat the playlist.")))
                                .build())
                        .append(Text.of("  "))
                        .append(Text.builder("«")
                                .color(TextColors.LIGHT_PURPLE)
                                .style(TextStyles.RESET)
                                .onClick(TextActions.runCommand("/music |<"))
                                .build())
                        .append(Text.of(" "))
                        .append(Text.builder("»")
                                .color(TextColors.LIGHT_PURPLE)
                                .style(TextStyles.RESET)
                                .onClick(TextActions.runCommand("/music >|"))
                                .build())
                        .build())
                .footer(Text.builder("Click a track to start playing.").color(TextColors.GRAY).build())
                .padding(Text.of("-"));
    }

    public static PaginationList.Builder playlistList() throws CommandException {
        List<Text> playlistListing = new ArrayList<>();
        for(Playlist playlist: Composer.getInstance().getPlaylists().values()){
            playlistListing.add(TextUtil.playlist(playlist)
                    .onClick(TextActions.runCommand("/music playlist "+getPlaylistName(playlist))).build());
        }

        if(playlistListing.isEmpty())
            throw new CommandException(Text.of("There aren't any loaded playlists."));

        return Sponge.getServiceManager().provide(PaginationService.class).get().builder()
                .contents(playlistListing)
                .title(Text.builder("Playlists").color(TextColors.GOLD).build())
                .footer(Text.builder("Click a playlist to select it.").color(TextColors.GRAY).build())
                .padding(Text.of("-"));

    }

    /**
     * Returns a track listing Text for the specified track.
     *
     * @param track listing
     * @return listing
     */
    public static Text.Builder track(NoteBlockStudioSong track) {
        return Text.builder(strOrUnknown(track.getName()))
                .color(TextColors.GREEN)
                .append(Text.builder(" by ").color(TextColors.GRAY).build())
                .append(Text.builder(strOrUnknown(track.getOgAuthor()).equals("Unknown")
                        ? strOrUnknown(track.getOgAuthor()) : track.getOgAuthor())
                        .color(TextColors.GREEN)
                        .build());
    }

    public static Text.Builder playlist(Playlist playlist){
        return Text.builder(getPlaylistName(playlist))
                .color(TextColors.GREEN)
                .onHover(TextActions.showText(Text.of(playlist.getTracks().size()+" tracks")));
    }

    public static String getPlaylistName(final Playlist playlist){
        return Composer.getInstance().getPlaylists().keySet()
                .stream()
                .filter(key -> playlist.equals(Composer.getInstance().getPlaylists().get(key)))
                .findFirst().get();
    }

    private static String strOrUnknown(String str) {
        return str == null || str.isEmpty() ? "Unknown" : str;
    }
}
