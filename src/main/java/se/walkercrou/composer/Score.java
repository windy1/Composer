package se.walkercrou.composer;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.Viewer;
import org.spongepowered.api.scheduler.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Represents a musical score to be played in game.
 */
public class Score {
    private String title, artist;
    private int tempoBmp;
    private TimeSignature time;
    private List<Measure> measures;

    public Score(String title, String artist, int tempoBmp, TimeSignature time, List<Measure> measures) {
        this.title = title;
        this.artist = artist;
        this.tempoBmp = tempoBmp;
        this.time = time;
        this.measures = measures;
    }

    /**
     * Returns the title of the piece.
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the artist of the piece.
     *
     * @return piece artist
     */
    public String getArtist() {
        return artist;
    }

    /**
     * Returns the tempo of the piece in beat per minute (bmp).
     *
     * @return tempo of piece
     */
    public int getTempo() {
        return tempoBmp;
    }

    /**
     * Returns the time signature of the piece.
     *
     * @return time signature
     */
    public TimeSignature getTime() {
        return time;
    }

    /**
     * Returns the measures or "bars" in this piece.
     *
     * @return measures in piece
     */
    public List<Measure> getMeasures() {
        return measures;
    }

    /**
     * Plays the score for the specified {@link Viewer} at the specified {@link Vector3d} position.
     *
     * @param context plugin
     * @param viewer viewer to play for
     * @param pos position
     */
    public void play(Composer context, Viewer viewer, Vector3d pos) {
        // get the shortest note in score, these will be used to determine how often we "step"
        // for example, if the shortest note is a sixteenth note in common time, we need to step 4 times per beat
        Note shortestNote = null;
        for (Measure measure : measures) {
            for (Note note : measure.getNotes()) {
                int type = note.getType();
                if (shortestNote == null || type > shortestNote.getType())
                    shortestNote = note;
            }
        }

        if (shortestNote == null)
            return;

        // i.e. in common time: a sixteenth note would be a 1/4 beat long
        double millisPerBeat = 60 / (double) tempoBmp * 1000;
        double millisPerStep = millisPerBeat * shortestNote.getBeatsForTime(time);
        long delay = (long) millisPerStep;

        stepsPerBeat = shortestNote.getType() / time.getSingleBeatNote();

        context.log.info("Millis per beat : " + millisPerBeat);
        context.log.info("Millis per step : " + millisPerStep);
        context.log.info("Steps per beat : " + stepsPerBeat);

        task = Sponge.getScheduler().createTaskBuilder().async()
                .execute(() -> nextStep(context, viewer, pos))
                .name("\"" + title + "\" -- " + artist)
                .delay(delay, TimeUnit.MILLISECONDS)
                .interval(delay, TimeUnit.MILLISECONDS)
                .submit(context);
    }

    private int stepsPerBeat;
    private int currentStep = 1;
    private int currentBeat = 1;
    private int currentMeasure = 1;

    private Task task;
    private int hold = 1;
    private int noteIndex = 0;

    private void nextStep(Composer context, Viewer viewer, Vector3d pos) {
        context.log.info("step = " + currentStep);
        context.log.info("beat = " + currentBeat);
        context.log.info("measure = " + currentMeasure);

        if (--hold <= 0) {
            Measure measure = measures.get(currentMeasure - 1);
            Note note = measure.getNotes()[noteIndex++];
            note.play(viewer, pos);
            double beats = note.getBeatsForTime(time);
            context.log.info("beats = " + beats);
            hold = (int) (beats * stepsPerBeat);
            context.log.info("hold = " + hold);
        }

        // increment counters
        if (currentStep == stepsPerBeat) {
            currentStep = 1;
            if (currentBeat == time.getBeatsPerMeasure()) {
                currentBeat = 1;
                if (currentMeasure == measures.size())
                    task.cancel();
                else {
                    currentMeasure++;
                    noteIndex = 0;
                }
            } else
                currentBeat++;
        } else
            currentStep++;
    }

    /**
     * Builder class for {@link Score} object.
     */
    public static class Builder {
        private String title, artist;
        private int tempoBmp;
        private TimeSignature time;
        private final List<Measure> measures = new ArrayList<>();

        /**
         * @see Score#getTitle()
         * @param title to set
         * @return this
         */
        public Builder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * @see Score#getArtist()
         * @param artist to set
         * @return this
         */
        public Builder artist(String artist) {
            this.artist = artist;
            return this;
        }

        /**
         * @see Score#getTempo()
         * @param tempoBmp tempo in beats per minute
         * @return tempo
         */
        public Builder tempo(int tempoBmp) {
            this.tempoBmp = tempoBmp;
            return this;
        }

        /**
         * @see Score#getTime()
         * @param time signature
         * @return this
         */
        public Builder time(TimeSignature time) {
            this.time = time;
            return this;
        }

        /**
         * @see Score#getMeasures()
         * @param measures to add
         * @return this
         */
        public Builder measure(Measure... measures) {
            this.measures.addAll(Arrays.asList(measures));
            return this;
        }

        /**
         * Builds a new Score
         *
         * @return new score
         */
        public Score build() {
            return new Score(title, artist, tempoBmp, time, measures);
        }
    }
}
