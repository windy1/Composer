package se.walkercrou.composer;

/**
 * Represents a single measure within a {@link Score}.
 */
public class Measure {
    private final Note[] notes;

    /**
     * Creates a new measure with the specified sequence of {@link Note}s.
     *
     * @param notes note sequence
     */
    public Measure(Note... notes) {
        this.notes = notes;
    }

    /**
     * Returns the notes in this measure.
     *
     * @return notes in the measure
     */
    public Note[] getNotes() {
        return notes;
    }
}
