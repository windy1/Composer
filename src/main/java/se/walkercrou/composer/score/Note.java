package se.walkercrou.composer.score;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.effect.Viewer;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;
import se.walkercrou.composer.util.PitchUtils;

/**
 * Represents a single note in a {@link Measure}.
 */
public class Note {
    public static final int WHOLE = 1;
    public static final int HALF = 2;
    public static final int QUARTER = 4;
    public static final int EIGHTH = 8;
    public static final int SIXTEENTH = 16;

    private final SoundType instrument;
    private final double pitch;
    private final int type;
    private final double volume;

    /**
     * Creates new Note with the specified {@link PitchUtils} value and type. The note type corresponds to how many beats
     * it should last. For instance, in common time, a quarter note (type 4) lasts one beat.
     *
     *  @param instrument of note
     * @param pitch of note
     * @param type of
     * @param volume of note 1 is full volume
     */
    public Note(SoundType instrument, double pitch, int type, double volume) {
        this.instrument = instrument;
        this.pitch = pitch;
        this.type = type;
        this.volume = volume;
    }

    public Note(double pitch, int type, double volume) {
        this(SoundTypes.BLOCK_NOTE_HARP, pitch, type, volume);
    }

    public Note(SoundType instrument, double pitch, int type) {
        this(instrument, pitch, type, 1);
    }

    public Note(double pitch, int type) {
        this(pitch, type, 1);
    }

    /**
     * Returns the "Minecraft" pitch of this note.
     *
     * @return pitch
     * @see PitchUtils
     */
    public double getPitch() {
        return pitch;
    }

    /**
     * Returns the type of note this is as an integer value. For instance, a quarter note has a type of "4".
     *
     * @return note type
     */
    public int getType() {
        return type;
    }

    /**
     * Returns the amount of beats this note lasts in the specified {@link TimeSignature}.
     *
     * @param time to check
     * @return amount of beats this note lasts
     */
    public double getBeatsForTime(TimeSignature time) {
        return time.getSingleBeatNote() / (double) type;
    }

    /**
     * Plays this note for the specified {@link Viewer} at the specified {@link Vector3d} position.
     *
     * @param viewer to play for
     * @param pos to play at
     */
    public void play(Viewer viewer, Vector3d pos) {
        if (volume == 0)
            return;
        viewer.playSound(instrument, pos, volume * 2, pitch);
    }

    /**
     * Creates a new "rest" note.
     *
     * @param type of rest
     * @return new note
     */
    public static Note rest(int type) {
        return new Note(null, -1, type, 0);
    }
}
