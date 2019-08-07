package se.walkercrou.composer.nbs;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import se.walkercrou.composer.Composer;
import se.walkercrou.composer.score.Score;
import se.walkercrou.composer.util.TextUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Plays music for a {@link Player}.
 */
public class MusicPlayer {
    private final Composer plugin;
    private final List<NoteBlockStudioSong> tracks;
    private int currentTrack = 0;
    private Score currentSong;
    private boolean playing = false;

    /**
     * Creates a new MusicPlayer with the specified tracks.
     *
     * @param plugin context
     * @param tracks tracks
     */
    public MusicPlayer(Composer plugin, List<NoteBlockStudioSong> tracks) {
        this.plugin = plugin;
        this.tracks = new ArrayList<>(tracks);
    }

    /**
     * Returns this player's current track.
     *
     * @return current track
     */
    public NoteBlockStudioSong getCurrentTrack() {
        return tracks.get(currentTrack);
    }

    /**
     * Returns the tracks in this player.
     *
     * @return tracks in player
     */
    public List<NoteBlockStudioSong> getTracks() {
        return Collections.unmodifiableList(tracks);
    }

    /**
     * Returns true if currently playing
     *
     * @return true if playing
     */
    public boolean isPlaying() {
        return playing;
    }

    /**
     * Starts playing or resumes the specified track.
     *
     * @param player player
     * @param trackIndex index of track
     */
    public void play(Player player, int trackIndex) {
        if (trackIndex != currentTrack) {
            currentTrack = trackIndex;
            if (currentSong != null) {
                currentSong.pause();
                currentSong = null;
            }
        }

        if (currentSong == null)
            currentSong = tracks.get(currentTrack).toScore().onFinish(() -> next(player));

        player.sendMessage(Text.builder("Now playing: ")
                .color(TextColors.GOLD)
                .append(TextUtil.track(getCurrentTrack()).build())
                .build());

        currentSong.play(plugin, player);
        playing = true;
    }

    /**
     * Starts playing or resumes the current track.
     *
     * @param player player
     */
    public void play(Player player) {
        play(player, currentTrack);
    }

    /**
     * Pauses the player.
     */
    public void pause() {
        playing = false;
        if (currentSong != null)
            currentSong.pause();
    }

    /**
     * Shuffles the player tracks and starts playing track zero.
     *
     * @param player player
     */
    public void shuffle(Player player) {
        Collections.shuffle(tracks);
        if (currentSong != null) {
            currentSong.pause();
            currentSong = null;
        }
        currentTrack = 0;
        play(player);
    }

    /**
     * Skips the specified amount of tracks and starts playing.
     *
     * @param player player
     * @param jumps tracks to skip
     */
    public void skip(Player player, int jumps) {
        int newIndex = currentTrack + jumps;
        if (newIndex < 0 || newIndex >= tracks.size()) {
            pause();
            currentSong = null;
            currentTrack = 0;
            return;
        }
        play(player, newIndex);
    }

    /**
     * Skips one track.
     *
     * @param player player
     */
    public void next(Player player) {
        skip(player, 1);
    }

    /**
     * Goes back one track.
     *
     * @param player player
     */
    public void previous(Player player) {
        skip(player, -1);
    }
}
