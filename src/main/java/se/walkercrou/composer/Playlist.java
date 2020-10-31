package se.walkercrou.composer;


import se.walkercrou.composer.nbs.NoteBlockStudioSong;

import java.util.List;

//pluginfolder/playlists/foldername/songs..
public class Playlist {
	private final List<NoteBlockStudioSong> tracks;

	public Playlist(final List<NoteBlockStudioSong> tracks) {
		this.tracks = tracks;
	}


	public List<NoteBlockStudioSong> getTracks() {
		return tracks;
	}
}
