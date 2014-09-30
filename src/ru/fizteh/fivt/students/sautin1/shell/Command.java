package ru.fizteh.fivt.students.sautin1.shell;

import java.io.IOException;

/**
 * Created by sautin1 on 9/30/14.
 */
public interface Command {
    public void execute(String... args) throws RuntimeException, IOException;
    public String toString();
}