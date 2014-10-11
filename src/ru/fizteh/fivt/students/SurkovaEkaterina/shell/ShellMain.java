package ru.fizteh.fivt.students.SurkovaEkaterina.shell;
import java.io.IOException;
import java.util.ArrayList;

public class ShellMain {
    public static void main(final String[] args) throws IOException {

        Shell shell = new Shell();

        ArrayList<Command<?>> commands = new ArrayList<Command<?>>();

        Command command = new CommandCat();
        commands.add(command);

        command = new CommandCd();
        commands.add(command);

        command = new CommandCp();
        commands.add(command);

        command = new CommandDir();
        commands.add(command);

        command = new CommandExit();
        commands.add(command);

        command = new CommandMkdir();
        commands.add(command);

        command = new CommandMv();
        commands.add(command);

        command = new CommandPwd();
        commands.add(command);

        command = new CommandRm();
        commands.add(command);

        command = new CommandLs();
        commands.add(command);

        shell.setShellCommands(commands);
        shell.setArguments(args);
        shell.beginExecuting();
    }
}
