package se.walkercrou.composer.nbs;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Represents song data imported from the Note Block Studio file format (.nbs).
 *
 * @see <a href="http://www.stuffbydavid.com/mcnbs">http://www.stuffbydavid.com/mcnbs</a>
 */
public class NoteBlockStudioSong {
    // ---- Header ---
    public short lengthTicks;
    public short height; // amount of layers
    public String name;
    public String author, ogAuthor;
    public String description;
    public double tempoTicksPerSecond;
    public boolean autoSave;
    public byte autoSaveDuration;
    public byte timeSignature;
    public int minutesSpent;
    public int leftClicks, rightClicks;
    public int blocksAdded, blocksRemoved;
    public String importedFileName;

    // ---- Note Blocks ----
    public NoteBlock[][] noteBlocks;

    // ---- Layer info ----
    public LayerInfo[] layerInfo;

    private NoteBlockStudioSong() {
    }

    /**
     * Reads the specified file and extracts the song data.
     *
     * @param file to read
     * @return song data
     * @throws IOException
     */
    public static NoteBlockStudioSong read(File file) throws IOException {
        if (!file.exists())
            throw new FileNotFoundException();
        NoteBlockStudioSong result = new NoteBlockStudioSong();
        InputStream in = new FileInputStream(file);
        byte[] bytes = IOUtils.toByteArray(in);
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        readHeader(result, buffer);
        readNoteBlocks(result, buffer);
        readLayerInfo(result, buffer);
        return result;
    }

    private static String getString(ByteBuffer in) throws IOException {
        int len = in.getInt();
        String str = "";
        for (int i = 0; i < len; i++)
            str += (char) in.get();
        return str;
    }

    private static void readHeader(NoteBlockStudioSong result, ByteBuffer buffer) throws IOException {
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
    }

    private static void readNoteBlocks(NoteBlockStudioSong result, ByteBuffer buffer) throws IOException {
        System.out.println(result);
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
                result.noteBlocks[layer][tick] = new NoteBlock(instrument, key);
            }
        }
    }

    private static void readLayerInfo(NoteBlockStudioSong result, ByteBuffer buffer) throws IOException {
        result.layerInfo = new LayerInfo[result.height];
        for (int i = 0; i < result.height; i++)
            result.layerInfo[i] = new LayerInfo(getString(buffer), buffer.get());
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Represents a single note block within the song.
     */
    public static class NoteBlock {
        public final byte instrument;
        public final byte key;

        private NoteBlock(byte instrument, byte key) {
            this.instrument = instrument;
            this.key = key;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

    /**
     * Represents some meta data relating to a layer of the song.
     */
    public static class LayerInfo {
        public final String name;
        public final byte volume;

        private LayerInfo(String name, byte volume) {
            this.name = name;
            this.volume = volume;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }
}
