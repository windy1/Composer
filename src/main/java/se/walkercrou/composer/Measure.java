package se.walkercrou.composer;

/**
 * Represents a single measure within a {@link Score}.
 */
public class Measure {
    private final Note[] notes;

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
