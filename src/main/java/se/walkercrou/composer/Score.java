package se.walkercrou.composer;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.Viewer;
import org.spongepowered.api.scheduler.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Represents a musical score to be played in game.
 */
public class Score {
    private final String title, artist;
    private final int tempoBmp;
    private final TimeSignature time;
    private final List<Layer> layers;
    private Runnable onFinish;

    private Score(String title, String artist, int tempoBmp, TimeSignature time, List<Layer> layers) {
        this.title = title;
        this.artist = artist;
        this.tempoBmp = tempoBmp;
        this.time = time;
        this.layers = layers;
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
     * Returns the {@link Layer}s in this Score.
     *
     * @return layers in score
     */
    public List<Layer> getLayers() {
        return layers;
    }

    public Score onFinish(Runnable onFinish) {
        this.onFinish = onFinish;
        return this;
    }

    /**
     * Plays this score for the specified {@link Viewer} at the specified {@link Vector3d} position.
     *
     * @param context plugin
     * @param viewer to play for
     * @param pos position to play at
     */
    public void play(Composer context, Viewer viewer, Vector3d pos) {
        // get the shortest note in score, these will be used to determine how often we "step"
        // for example, if the shortest note is a sixteenth note in common time, we need to step 4 times per beat
        Note shortestNote = null;
        for (Layer layer : layers) {
            for (Measure measure : layer.getMeasures()) {
                for (Note note : measure.getNotes()) {
                    int type = note.getType();
                    if (shortestNote == null || type > shortestNote.getType())
                        shortestNote = note;
                }
            }
        }

        if (shortestNote == null)
            return;

        // i.e. in common time: a sixteenth note would be a 1/4 beat long
        double millisPerBeat = 60 / (double) tempoBmp * 1000;
        double millisPerStep = millisPerBeat * shortestNote.getBeatsForTime(time);
        long delay = (long) millisPerStep;

        stepsPerBeat = shortestNote.getType() / time.getSingleBeatNote();

        context.log.info("Now playing: \"" + title + "\" by " + artist);
        context.log.info("Millis per beat : " + millisPerBeat);
        context.log.info("Millis per step : " + millisPerStep);
        context.log.info("Steps per beat : " + stepsPerBeat);
        context.log.info("Layers: " + layers.size());

        task = Sponge.getScheduler().createTaskBuilder().async()
                .execute(() -> nextStep(viewer, pos))
                .name("\"" + title + "\" by " + artist)
                .delay(delay, TimeUnit.MILLISECONDS)
                .interval(delay, TimeUnit.MILLISECONDS)
                .submit(context);
    }

    /**
     * Pauses the song.
     */
    public void pause() {
        if (task != null)
            task.cancel();
    }

    /**
     * Stops the song.
     */
    public void finish() {
        pause();
        if (onFinish != null)
            onFinish.run();
    }

    private Task task;
    private int stepsPerBeat;
    private int currentStep = 1;

    private void nextStep(Viewer viewer, Vector3d pos) {
        boolean finished = true;
        for (Layer layer : layers)
            finished &= layer.onStep(viewer, pos, currentStep, stepsPerBeat);
        if (currentStep == stepsPerBeat)
            currentStep = 1;
        else
            currentStep++;
        if (finished)
            finish();
    }

    /**
     * Builder class for {@link Score} object.
     */
    public static class Builder {
        private String title, artist;
        private int tempoBmp;
        private TimeSignature time;
        protected final List<Layer> layers = new ArrayList<>();

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
         * Creates a new {@link Layer.Builder}.
         *
         * @return new layer builder
         */
        public Layer.Builder newLayer() {
            return new Layer.Builder(this, time);
        }

        /**
         * Builds a new Score
         *
         * @return new score
         */
        public Score build() {
            return new Score(title, artist, tempoBmp, time, layers);
        }
    }
}
