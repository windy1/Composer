package se.walkercrou.composer;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

/**
 * Main class for Composer plugin.
 */
@Plugin(id = "composer", name = "Composer", version = "1.0.0")
public class Composer {
    @Inject public Logger log;

    private final CommandSpec piano = CommandSpec.builder()
            .description(Text.of("Plays a piano note"))
            .arguments(GenericArguments.onlyOne(GenericArguments.doubleNum(Text.of("pitch"))))
            .executor(((src, args) -> playNote(src, args, SoundTypes.NOTE_PIANO)))
            .build();

    private final CommandSpec bass = CommandSpec.builder()
            .description(Text.of("Plays a bass note"))
            .arguments(GenericArguments.onlyOne(GenericArguments.doubleNum(Text.of("pitch"))))
            .executor((src, args) -> playNote(src, args, SoundTypes.NOTE_BASS))
            .build();

    private final CommandSpec pig = CommandSpec.builder()
            .description(Text.of("Plays a pig note"))
            .arguments(GenericArguments.onlyOne(GenericArguments.doubleNum(Text.of("pitch"))))
            .executor((src, args) -> playNote(src, args, SoundTypes.PIG_IDLE))
            .build();

    private final CommandSpec maryCmd = CommandSpec.builder()
            .description(Text.of("Plays \"Mary Had a Little Lamb\" by Sarah Josepha Hale"))
            .executor(this::playSong)
            .build();

    @Listener
    public void onGameStarted(GameStartedServerEvent event) {
        CommandManager cm = Sponge.getCommandManager();
        cm.register(this, piano, "piano");
        cm.register(this, bass, "bass");
        cm.register(this, pig, "pig");
        cm.register(this, maryCmd, "mary");
    }

    private CommandResult playNote(CommandSource src, CommandContext context, SoundType type) {
        log.info("Playing a note");
        Player player = (Player) src;
        double pitch = context.<Double>getOne("pitch").get();
        player.getWorld().playSound(type, player.getLocation().getPosition(), 2, pitch);
        return CommandResult.success();
    }

    private CommandResult playSong(CommandSource src, CommandContext context) {
        if (!(src instanceof Player))
            return CommandResult.builder().successCount(0).build();

        Note qb1 = Note.quarter(Pitch.B1);
        Note qa1 = Note.quarter(Pitch.A1);

        Measure m1 = new Measure(qb1, qa1, Note.quarter(Pitch.G1), qa1); // mary had a
        Measure m2 = new Measure(qb1, qb1, Note.half(Pitch.B1)); // little lamb,
        Measure m3 = new Measure(qa1, qa1, Note.half(Pitch.A1)); // little lamb,
        Measure m4 = new Measure(qb1, Note.quarter(Pitch.D2), Note.half(Pitch.D2)); // little lamb.
        // (repeat m1) mary had a
        Measure m6 = new Measure(qb1, qb1, qb1, qb1); // little lamb. Its
        Measure m7 = new Measure(qa1, qa1, qb1, qa1); // fleece was white as
        Measure m8 = new Measure(Note.whole(Pitch.G1));

        Player player = (Player) src;
        new Score.Builder()
                .title("Mary Had a Little Lamb")
                .artist("Sarah Josepha Hale")
                .tempo(60)
                .time(TimeSignature.COMMON)
                .measure(m1, m2, m3, m4, m1, m6, m7, m8)
                .build()
                .play(this, player.getWorld(), player.getLocation().getPosition());

        return CommandResult.success();
    }
}
