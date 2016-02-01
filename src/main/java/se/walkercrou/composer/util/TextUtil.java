package se.walkercrou.composer.util;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.service.pagination.PaginationBuilder;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import se.walkercrou.composer.nbs.NoteBlockStudioSong;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for text handling.
 */
public final class TextUtil {
    private TextUtil() {
    }

    /**
     * Returns a pagination builder for the specified tracks.
     *
     * @param tracks to build list for
     * @return pagination builder
     * @throws CommandException if list is empty
     */
    public static PaginationBuilder trackList(List<NoteBlockStudioSong> tracks) throws CommandException {
        List<Text> trackListings = new ArrayList<>();
        for (int i = 0; i < tracks.size(); i++) {
            trackListings.add(TextUtil.track(tracks.get(i))
                    .onClick(TextActions.runCommand("/music > " + (i + 1)))
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
                        .append(Text.of("  "))
                        .append(Text.builder("Pause")
                                .color(TextColors.YELLOW)
                                .onClick(TextActions.runCommand("/music ||"))
                                .build())
                        .append(Text.of("  "))
                        .append(Text.builder("Shuffle")
                                .color(TextColors.AQUA)
                                .onClick(TextActions.runCommand("/music shuffle"))
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
                .paddingString("-");
    }

    /**
     * Returns a track listing Text for the specified track.
     *
     * @param track listing
     * @return listing
     */
    public static Text.Builder track(NoteBlockStudioSong track) {
        return Text.builder(strOrUnknown(track.name))
                .color(TextColors.GREEN)
                .append(Text.builder(" by ").color(TextColors.GRAY).build())
                .append(Text.builder(strOrUnknown(track.ogAuthor).equals("Unknown")
                        ? strOrUnknown(track.author) : track.ogAuthor)
                        .color(TextColors.GREEN)
                        .build());
    }

    private static String strOrUnknown(String str) {
        return str == null || str.isEmpty() ? "Unknown" : str;
    }
}
