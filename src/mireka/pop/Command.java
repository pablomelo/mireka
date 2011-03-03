package mireka.pop;

import java.io.IOException;

/**
 * A command is responsible for the parsing and execution of a POP3 command
 * received from the client.
 */
public interface Command {
    public void execute(CommandParser commandParser) throws IOException,
            Pop3Exception;

}
