package ru.fizteh.fivt.students.SurkovaEkaterina.shell;

public class CommandExit extends ACommand<FilesOperations> {

    public CommandExit() {
        super("exit", "exit");
    }

    public final void executeCommand(final String parameters,
                                     final FilesOperations operations) {
        System.exit(0);
    }

}
