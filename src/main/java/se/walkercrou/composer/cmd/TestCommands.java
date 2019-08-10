package se.walkercrou.composer.cmd;

import static org.spongepowered.api.effect.sound.SoundTypes.BLOCK_NOTE_BASS;
import static se.walkercrou.composer.score.Note.HALF;
import static se.walkercrou.composer.score.Note.QUARTER;
import static se.walkercrou.composer.score.Note.WHOLE;
import static se.walkercrou.composer.Pitch.A0;
import static se.walkercrou.composer.Pitch.A1;
import static se.walkercrou.composer.Pitch.B0;
import static se.walkercrou.composer.Pitch.B1;
import static se.walkercrou.composer.Pitch.D2;
import static se.walkercrou.composer.Pitch.G0;
import static se.walkercrou.composer.Pitch.G1;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import se.walkercrou.composer.Composer;
import se.walkercrou.composer.score.Measure;
import se.walkercrou.composer.score.Note;
import se.walkercrou.composer.score.Score;
import se.walkercrou.composer.score.TimeSignature;
import se.walkercrou.composer.nbs.NoteBlockStudioSong;

import java.io.File;
import java.io.IOException;

/**
 * Class for commands used for debugging.
 */
public class TestCommands {
    private final Composer plugin;
    private final CommandSpec piano = CommandSpec.builder()
            .description(Text.of("Plays a piano note"))
            .arguments(GenericArguments.onlyOne(GenericArguments.doubleNum(Text.of("pitch"))))
            .executor(((src, args) -> playNote(src, args, SoundTypes.BLOCK_NOTE_HARP)))
            .build();
    private final CommandSpec bass = CommandSpec.builder()
            .description(Text.of("Plays a bass note"))
            .arguments(GenericArguments.onlyOne(GenericArguments.doubleNum(Text.of("pitch"))))
            .executor((src, args) -> playNote(src, args, BLOCK_NOTE_BASS))
            .build();
    private final CommandSpec pig = CommandSpec.builder()
            .description(Text.of("Plays a pig note"))
            .arguments(GenericArguments.onlyOne(GenericArguments.doubleNum(Text.of("pitch"))))
            .executor((src, args) -> playNote(src, args, SoundTypes.ENTITY_PIG_AMBIENT))
            .build();
    private final CommandSpec mary = CommandSpec.builder()
            .description(Text.of("Plays \"Mary Had a Little Lamb\" by Sarah Josepha Hale"))
            .executor(this::playSong)
            .build();
    private final CommandSpec nbs = CommandSpec.builder()
            .description(Text.of("Reads the specified .nbs file and prints the result"))
            .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("file"))))
            .executor(this::readNbsFile)
            .build();

    public TestCommands(Composer plugin) {
        this.plugin = plugin;
    }

    public void register() {
        CommandManager cm = Sponge.getCommandManager();
        cm.register(plugin, piano, "piano");
        cm.register(plugin, bass, "bass");
        cm.register(plugin, pig, "pig");
        cm.register(plugin, mary, "mary");
        cm.register(plugin, nbs, "nbs");
    }

    @NonnullByDefault
    private CommandResult readNbsFile(CommandSource src, CommandContext context) throws CommandException {
        if (!(src instanceof Player))
            throw new CommandException(Text.of("Only players may run this command."));

        NoteBlockStudioSong nbs;
        try {
            nbs = NoteBlockStudioSong.read(new File(context.<String>getOne("file").get()));
        } catch (IOException e) {
            e.printStackTrace();
            throw new CommandException(Text.of("Error reading NBS file"), e);
        }

        Player player = (Player) src;
        nbs.toScore().play(plugin, player.getWorld(), player.getLocation().getPosition());

        return CommandResult.success();
    }

    @NonnullByDefault
    private CommandResult playNote(CommandSource src, CommandContext context, SoundType type) {
        Player player = (Player) src;
        double pitch = context.<Double>getOne("pitch").get();
        player.getWorld().playSound(type, player.getLocation().getPosition(), 2, pitch);
        return CommandResult.success();
    }

    @NonnullByDefault
    private CommandResult playSong(CommandSource src, CommandContext context) throws CommandException {
        if (!(src instanceof Player))
            throw new CommandException(Text.of("Only players may run this command."));

        Note qb1 = new Note(B1, QUARTER);
        Note qa1 = new Note(A1, QUARTER);

        Measure m1 = new Measure(qb1, qa1, new Note(G1, QUARTER), qa1); // mary had a
        Measure m2 = new Measure(qb1, qb1, new Note(B1, HALF)); // little lamb,
        Measure m3 = new Measure(qa1, qa1, new Note(A1, HALF)); // little lamb,
        Measure m4 = new Measure(qb1, new Note(D2, QUARTER), new Note(D2, HALF)); // little lamb.
        // (repeat m1) mary had a
        Measure m6 = new Measure(qb1, qb1, qb1, qb1); // little lamb. Its
        Measure m7 = new Measure(qa1, qa1, qb1, qa1); // fleece was white as
        Measure m8 = new Measure(new Note(G1, WHOLE));

        Measure bbass = new Measure(new Note(BLOCK_NOTE_BASS, B0, WHOLE));
        Measure abass = new Measure(new Note(BLOCK_NOTE_BASS, A0, WHOLE));

        Player player = (Player) src;
        new Score.Builder()
                .title("Mary Had a Little Lamb")
                .artist("Sarah Josepha Hale")
                .tempo(240)
                .time(TimeSignature.COMMON)
                .newLayer()
                .measure(m1, m2, m3, m4, m1, m6, m7, m8)
                .saveLayer()
                .newLayer()
                .measure(bbass, bbass, abass, bbass, bbass, bbass, abass, new Measure(new Note(G0, WHOLE)))
                .saveLayer()
                .build()
                .play(plugin, player.getWorld(), player.getLocation().getPosition());

        return CommandResult.success();
    }
}
