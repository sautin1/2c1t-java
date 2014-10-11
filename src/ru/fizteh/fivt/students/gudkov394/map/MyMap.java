package ru.fizteh.fivt.students.gudkov394.map;


import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MyMap {
    public Boolean checkName(final String name) {
        String[] s = {"put", "get", "remove", "list", "exit"};
        for (int i = 0; i < s.length; ++i) {
            if (name.equals(s[i])) {
                return true;
            }
        }
        return false;
    }

    public void run(final String[] currentArgs, final Map ct) {
        if (currentArgs[0].equals("put")) {
            Put put = new Put(currentArgs, ct);
        } else if ("get".equals(currentArgs[0])) {
            Get get = new Get(currentArgs, ct);
        } else if ("remove".equals(currentArgs[0])) {
            Remove remove = new Remove(currentArgs, ct);
        } else if ("list".equals(currentArgs[0])) {
            ListTable listTable = new ListTable(currentArgs, ct);
        } else if ("exit".equals(currentArgs[0])) {
            Exit exit = new Exit(currentArgs, ct);
        } else {
            System.err.println("wrong command");
            System.exit(22);
        }
    }

    public void interactive() {
        Scanner sc = new Scanner(System.in);
        Map currentTable = new HashMap<String, String>();
        Init init = new Init(currentTable, System.getProperty("db.file"));
        while (true) {
            String currentString = sc.nextLine();
            currentString = currentString.trim();
                run(currentString.split("\\s+"), currentTable);
        }

    }

    public void packageMode(final String[] args) {
        Map currentTable = new HashMap<String, String>();
        Init init = new Init(currentTable, System.getProperty("db.file"));
        StringBuilder builder = new StringBuilder();
        for (String s : args) {
            builder.append(s).append(" ");
        }
        String string = new String(builder);
        string = string.replaceAll("\\s*;\\s*", ";");
        String[] commands = string.split(";|(\\s+)");
        int i = 0;
        while (i < commands.length) {
            int first = i;
            ++i;
            while (i < commands.length && !checkName(commands[i])) {
                ++i;
            }
            int size = 0;
            for (int j = 0; j < i - first; ++j) {
                if (commands[j + first].length() != 0) {
                    ++size;
                }
            }

            String[] s = new String[size];
            int tmpSize = 0;
            for (int j = 0; j < s.length; ++j) {
                if (commands[j + first].length() != 0) {
                    s[tmpSize] = commands[j + first];
                    ++tmpSize;
                }
            }
            run(s, currentTable);
        }
        System.exit(0);
    }


    public MyMap(final String[] args) {
        if (args.length == 0) {
            interactive();
        } else {
            packageMode(args);
        }
    }
}
