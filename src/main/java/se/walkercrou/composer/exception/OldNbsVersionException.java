package se.walkercrou.composer.exception;

/**
 * @author sarhatabaot
 */
public class OldNbsVersionException extends Exception{
    public OldNbsVersionException(final String fileName) {
        super(String.format("File %s is using the classic (version 0) of NBS.", fileName));
    }
}
