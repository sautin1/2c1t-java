package ru.fizteh.fivt.students.vadim_mazaev.shell.commands;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

public final class MkdirCmd {
    private MkdirCmd() {
        //not called
    }
    public static void run(final String[] cmdWithArgs) throws Exception {
        if (cmdWithArgs.length == 1) {
            throw new Exception(getName() + ": missing operand");
        } else if (cmdWithArgs.length > 2) {
            throw new Exception(getName()
                    + ": too much arguments");
        }
        try {
            if (cmdWithArgs[1].isEmpty()) {
                throw new Exception(getName() + ": cannot create '"
                    + "': no such file or directory");
            }
            File makedDir = Paths
                .get(System.getProperty("user.dir"), cmdWithArgs[1]).toFile();
            if (!makedDir.mkdir()) {
                throw new Exception(getName()
                        + ": cannot create directory '"
                        + cmdWithArgs[1] + "': File exists");
            }
        } catch (InvalidPathException e) {
            throw new Exception(getName()
                    + ": cannot create directory '"
                    + cmdWithArgs[1] + "': illegal character in name");
        } catch (SecurityException e) {
            throw new Exception(getName()
                    + ": cannot create directory '"
                    + cmdWithArgs[1] + "': access denied");
        }
    }
    public static String getName() {
        return "mkdir";
    }
}
