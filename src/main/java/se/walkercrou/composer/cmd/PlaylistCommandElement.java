package se.walkercrou.composer.cmd;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;

import org.spongepowered.api.text.Text;
import se.walkercrou.composer.Composer;

import java.util.ArrayList;
import java.util.List;

public class PlaylistCommandElement extends CommandElement {
	private Composer composer = Composer.getInstance(); //probably should use inject here instead
	protected PlaylistCommandElement(@Nullable final Text key) {
		super(key);
	}

	@Nullable
	@Override
	protected Object parseValue(final @NotNull CommandSource source, final @NotNull CommandArgs args) throws ArgumentParseException {
		if(composer.getPlaylists().get(args.peek()) != null){
			return composer.getPlaylists().get(args.next());
		}
		return null;
	}

	@Override
	public List<String> complete(final @NotNull CommandSource src, final @NotNull CommandArgs args, final @NotNull CommandContext context) {
		return new ArrayList<>(composer.getPlaylists().keySet());
	}


}
