package se.walkercrou.composer.score;

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

    private final int beatsPerMeasure;
    private final int singleBeatNote;

    /**
     * Creates a new TimeSignature. The first parameter represents the upper number in a traditional key signature
     * which represents the amount of beats in a measure. The second parameter represents the lower number in a key
     * signature which represents the type of note that is to receive a single beat.
     *
     * @param beatsPerMeasure beats in a measure
     * @param singleBeatNote note that is one beat long
     */
    public TimeSignature(int beatsPerMeasure, int singleBeatNote) {
        this.beatsPerMeasure = beatsPerMeasure;
        this.singleBeatNote = singleBeatNote;
    }

    /**
     * Returns the amount of beats in a single measure.
     *
     * @return amount of beats in measure
     */
    public int getBeatsPerMeasure() {
        return beatsPerMeasure;
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
