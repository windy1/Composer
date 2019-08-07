package se.walkercrou.composer.score;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.effect.Viewer;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a "layer" or "track" within a {@link Score}.
 */
public class Layer {
    private final TimeSignature time;
    private final List<Measure> measures;

    private Layer(TimeSignature time, List<Measure> measures) {
        this.time = time;
        this.measures = measures;
    }

    /**
     * Returns the measures in this Layer.
     *
     * @return measures in layer
     */
    public List<Measure> getMeasures() {
        return measures;
    }

    private int currentBeat = 1;
    private int currentMeasure = 1;
    private int hold = 1;
    private int noteIndex = 0;
    private boolean finished = false;

    protected boolean onStep(Viewer viewer, Vector3d pos, int currentStep, int stepsPerBeat) {
        if (finished)
            return true; // no more measures to play

        if (--hold <= 0) {
            // play next note
            Measure measure = measures.get(currentMeasure - 1);
            Note note = measure.getNotes()[noteIndex++];
            pos = pos == null && viewer instanceof Player ? ((Player) viewer).getLocation().getPosition() : pos;
            note.play(viewer, pos);
            hold = (int) (note.getBeatsForTime(time) * stepsPerBeat);
        }

        if (currentStep == stepsPerBeat) {
            if (currentBeat == time.getBeatsPerMeasure()) {
                currentBeat = 1;
                if (currentMeasure == measures.size())
                    finished = true;
                else {
                    currentMeasure++;
                    noteIndex = 0;
                }
            } else
                currentBeat++;
        }

        return false;
    }

    /**
     * Builder class for a {@link Layer}. You can receive an instance of this object through
     * {@link Score.Builder#newLayer()}.
     */
    public static class Builder {
        private final Score.Builder parent;
        private final TimeSignature time;
        private final List<Measure> measures = new ArrayList<>();

        protected Builder(Score.Builder parent, TimeSignature time) {
            this.parent = parent;
            this.time = time;
        }

        /**
         * Adds a single or multiple measures to the layer.
         *
         * @param measures to add
         * @return this builder
         */
        public Builder measure(Measure... measures) {
            this.measures.addAll(Arrays.asList(measures));
            return this;
        }

        /**
         * Saves the layer to the {@link Score} and returns the original Score builder.
         *
         * @return score builder
         */
        public Score.Builder saveLayer() {
            parent.layers.add(new Layer(time, measures));
            return parent;
        }
    }
}
