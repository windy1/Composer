package se.walkercrou.composer.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CorruptedFileException extends Exception {
	public CorruptedFileException(final String message) {
		super(message);
	}
}