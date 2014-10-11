package ru.fizteh.fivt.students.SurkovaEkaterina.shell;
import java.io.IOException;

public class CommandCd extends ACommand<FilesOperations> {
    public CommandCd() {
        super("cd", "cd <directory name>");
    }

    public final void executeCommand(final String parameters,
                                     final FilesOperations operations)
            throws IOException {
        if (CommandsParser.getParametersNumber(parameters) > 1) {
            throw new IllegalArgumentException("cd: Too many arguments!");
        }
        if (CommandsParser.getParametersNumber(parameters) > 0) {
            operations.setCurrentDirectory(parameters);
        }
    }
}
