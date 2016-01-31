package se.walkercrou.composer;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.effect.Viewer;
import org.spongepowered.api.effect.sound.SoundTypes;

/**
 * Represents a single note in a {@link Measure}.
 */
public class Note {
    private final double pitch;
    private final int type;

    /**
     * Creates new Note with the specified {@link Pitch} value and type. The note type corresponds to how many beats
     * it should last. For instance, in common time, a quarter note (type 4) lasts one beat.
     *
     * @param pitch of note
     * @param type of
     */
    public Note(double pitch, int type) {
        this.pitch = pitch;
        this.type = type;
    }

    /**
     * Returns the "Minecraft" pitch of this note.
     *
     * @return pitch
     * @see Pitch
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
        viewer.playSound(SoundTypes.NOTE_PIANO, pos, 2, pitch);
    }

    /**
     * Returns a whole note with the specified pitch.
     *
     * @param pitch of note
     * @return whole note
     */
    public static Note whole(double pitch) {
        return new Note(pitch, 1);
    }

    /**
     * Returns a half note with the specified pitch.
     *
     * @param pitch of note
     * @return half note
     */
    public static Note half(double pitch) {
        return new Note(pitch, 2);
    }

    /**
     * Returns a quarter note with the specified pitch.
     *
     * @param pitch of note
     * @return quarter note
     */
    public static Note quarter(double pitch) {
        return new Note(pitch, 4);
    }

    /**
     * Returns an eighth note with the specified pitch.
     *
     * @param pitch of note
     * @return eighth note
     */
    public static Note eighth(double pitch) {
        return new Note(pitch, 8);
    }

    /**
     * Returns a sixteenth note with the specified pitch.
     *
     * @param pitch of note
     * @return sixteenth note
     */
    public static Note sixteenth(double pitch) {
        return new Note(pitch, 16);
    }
}
