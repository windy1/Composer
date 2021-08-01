package se.walkercrou.composer.nbs;

import com.google.common.io.ByteStreams;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;
import se.walkercrou.composer.exception.CorruptedFileException;
import se.walkercrou.composer.score.Layer;
import se.walkercrou.composer.score.Measure;
import se.walkercrou.composer.score.Note;
import se.walkercrou.composer.util.PitchUtils;
import se.walkercrou.composer.score.Score;
import se.walkercrou.composer.score.TimeSignature;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Represents song data imported from the Note Block Studio file format (.nbs).
 *
 * @see <a href="http://www.stuffbydavid.com/mcnbs">http://www.stuffbydavid.com/mcnbs</a>
 */
@Getter
public class NoteBlockStudioSong {
    // ---- Header ---
    private short zeroBytes;
    private byte version;
    private byte instruments;
    private short lengthTicks;
    private short height; // amount of layers
    private String name;
    private String author;
    private String ogAuthor;
    private String description;
    private double tempoTicksPerSecond;
    private boolean autoSave;
    private byte autoSaveDuration;
    private byte timeSignature;
    private int minutesSpent;
    private int leftClicks;
    private int rightClicks;
    private int blocksAdded;
    private int blocksRemoved;
    private String importedFileName;
    private byte loop;
    private byte maxLoop;
    private short loopStartTick;

    // ---- Note Blocks ----
    private NoteBlock[][] noteBlocks;

    // ---- Layer info ----
    private LayerInfo[] layerInfo;


    /**
     * Converts this song into a {@link Score}.
     *
     * @return score
     */
    public Score toScore() {
        Score.Builder builder = new Score.Builder()
                .title(name)
                .artist(ogAuthor)
                .tempo((int) tempoTicksPerSecond * 60)
                .time(new TimeSignature(timeSignature, 4));

        for (int i = 0; i < noteBlocks.length; i++) {
            NoteBlock[] layer = noteBlocks[i];
            Layer.Builder layerBuilder = builder.newLayer();
            Note[] currentMeasure = new Note[timeSignature];
            int beat = 1;
            for (NoteBlock note : layer) {
                if (note == null)
                    currentMeasure[beat - 1] = Note.rest(Note.QUARTER);
                else {
                    // make sure key is within two octave range
                    int key = note.getKey();
                    while (key < 33)
                        key += 12;
                    while (key > 57)
                        key -= 12;
                    key -= 33;

                    currentMeasure[beat - 1] = new Note(note.getInstrument(), PitchUtils.TWO_OCTAVES[key], Note.QUARTER,
                            layerInfo[i].volume / 100d);
                }

                if (beat == timeSignature) {
                    layerBuilder.measure(new Measure(currentMeasure));
                    currentMeasure = new Note[timeSignature];
                    beat = 1;
                } else
                    beat++;
            }
            layerBuilder.saveLayer();
        }

        return builder.build();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Reads the specified file and extracts the song data.
     *
     * @param file to read
     * @return song data
     * @throws IOException
     */
    public static NoteBlockStudioSong read(File file) throws IOException, CorruptedFileException {
        if (!file.exists())
            throw new FileNotFoundException();
        NoteBlockStudioSong result = new NoteBlockStudioSong();
        InputStream in = new FileInputStream(file);
        byte[] bytes = ByteStreams.toByteArray(in);
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        readHeader(result, buffer);
        readNoteBlocks(result, buffer);
        readLayerInfo(result, buffer);
        return result;
    }

    private static String getString(ByteBuffer in) throws IOException {
        int len = in.getInt();
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < len; i++)
            str.append((char) in.get());
        return str.toString();
    }

    private static void readHeader(NoteBlockStudioSong result, ByteBuffer buffer) throws IOException {
        result.zeroBytes = buffer.getShort();
        result.version = buffer.get();
        result.instruments = buffer.get();
        result.lengthTicks = buffer.getShort();
        result.height = buffer.getShort();
        result.name = getString(buffer);
        result.author = getString(buffer);
        result.ogAuthor = getString(buffer);
        result.description = getString(buffer);
        result.tempoTicksPerSecond = buffer.getShort() / 100d;
        result.autoSave = buffer.get() != 1;
        result.autoSaveDuration = buffer.get();
        result.timeSignature = buffer.get();
        result.minutesSpent = buffer.getInt();
        result.leftClicks = buffer.getInt();
        result.rightClicks = buffer.getInt();
        result.blocksAdded = buffer.getInt();
        result.blocksRemoved = buffer.getInt();
        result.importedFileName = getString(buffer);
        result.loop = buffer.get();
        result.maxLoop = buffer.get();
        result.loopStartTick = buffer.getShort();
    }

    private static void readNoteBlocks(NoteBlockStudioSong result, ByteBuffer buffer) throws IOException, CorruptedFileException {
        result.noteBlocks = new NoteBlock[result.height + 1][result.lengthTicks + 1];
        short tick = -1;
        short jumps;
        while (true) {
            jumps = buffer.getShort();
            if (jumps == 0)
                break;
            tick += jumps;
            short layer = -1;
            while (true) {
                jumps = buffer.getShort();
                if (jumps == 0)
                    break;
                layer += jumps;
                byte instrument = buffer.get();
                byte key = buffer.get();
                byte volume = buffer.get();
                byte panning = buffer.get();
                short pitch = buffer.getShort();
                try {
                    result.noteBlocks[layer][tick] = new NoteBlock(instrument, key,volume,panning,pitch);
                } catch (ArrayIndexOutOfBoundsException e){
                    throw new CorruptedFileException("Most likely a corrupted file..");
                }
            }
        }
    }


    private static void readLayerInfo(NoteBlockStudioSong result, ByteBuffer buffer) throws IOException {
        String name = getString(buffer);
        byte lock = buffer.get();
        byte volume = buffer.get();
        byte stereo = buffer.get();

        result.layerInfo = new LayerInfo[result.height + 1];
        for (int i = 0; i < result.height; i++)
            result.layerInfo[i] = new LayerInfo(name,lock,volume,stereo);
    }

    /**
     * Represents a single note block within the song.
     */
    @Getter
    public static class NoteBlock {
        private final byte instrument;
        private final byte key;
        private final byte volume;
        private final byte panning;
        private final short pitch;

        private NoteBlock(byte instrument, byte key, byte volume, byte panning, short pitch) {
            this.instrument = instrument;
            this.key = key;
            this.volume = volume;
            this.panning = panning;
            this.pitch = pitch;
        }

        /**
         * Returns the {@link SoundType} instrument for this note.
         *
         * @return instrument
         */
        public SoundType getInstrument() {
            switch (instrument) {
                default:
                case 0:
                    return SoundTypes.BLOCK_NOTE_HARP;
                case 1:
                    return SoundTypes.BLOCK_NOTE_BASS;
                case 2:
                    return SoundTypes.BLOCK_NOTE_BASEDRUM;
                case 3:
                    return SoundTypes.BLOCK_NOTE_SNARE;
                case 4:
                    return SoundTypes.BLOCK_NOTE_PLING;
                case 5:
                    return  SoundTypes.BLOCK_NOTE_GUITAR;
                case 6:
                    return SoundTypes.BLOCK_NOTE_FLUTE;
                case 7:
                    return SoundTypes.BLOCK_NOTE_BELL;
                case 8:
                    return SoundTypes.BLOCK_NOTE_CHIME;
                case 9:
                    return SoundTypes.BLOCK_NOTE_XYLOPHONE;
            }
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

    /**
     * Represents some meta data relating to a layer of the song.
     */
    @Getter
    public static class LayerInfo {
        private final String name;
        private final byte lock;
        private final byte volume;
        private final byte stereo;

        private LayerInfo(String name, byte lock, byte volume, byte stereo) {
            this.name = name;
            this.lock = lock;
            this.volume = volume;
            this.stereo = stereo;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }
}
