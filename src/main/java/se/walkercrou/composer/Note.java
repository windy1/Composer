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

    public double getBeatsForTime(TimeSignature time) {
        return time.getSingleBeatNote() / type;
    }

    public void play(Viewer viewer, Vector3d pos) {
        viewer.playSound(SoundTypes.NOTE_PIANO, pos, 2, pitch);
    }

    public static Note whole(double pitch) {
        return new Note(pitch, 1);
    }

    public static Note half(double pitch) {
        return new Note(pitch, 2);
    }

    public static Note quarter(double pitch) {
        return new Note(pitch, 4);
    }

    public static Note eighth(double pitch) {
        return new Note(pitch, 8);
    }

    public static Note sixteenth(double pitch) {
        return new Note(pitch, 16);
    }
}
