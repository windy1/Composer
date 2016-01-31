package se.walkercrou.composer;

/**
 * Represents a musical time signature within a {@link Score}.
 */
public class TimeSignature {
    /**
     * Common (four-four) time.
     */
    public static final TimeSignature COMMON = new TimeSignature(4, 4);
    /**
     * Cut (two-two) time.
     */
    public static final TimeSignature CUT = new TimeSignature(2, 2);

    private final int beats, singleBeatNote;

    public TimeSignature(int beats, int singleBeatNote) {
        this.beats = beats;
        this.singleBeatNote = singleBeatNote;
    }

    /**
     * Returns the amount of beats in a single measure.
     *
     * @return amount of beats in measure
     */
    public int getBeatsPerMeasure() {
        return beats;
    }

    /**
     * Returns the note type that represents a single beat. For instance, in common time, a quarter note (4) represents
     * one beat.
     *
     * @return single beat note
     */
    public int getSingleBeatNote() {
        return singleBeatNote;
    }
}
