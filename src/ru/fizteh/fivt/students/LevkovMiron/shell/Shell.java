package ru.fizteh.fivt.students.LevkovMiron.shell;

import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Scanner;
/**
 * Created by Мирон on 19.09.2014 ${PACKAGE_NAME}.
 */
class Shell {

    private String curDir;

    Shell() {
        curDir = System.getProperty("user.dir");
        curDir = replaceSlash(curDir);
    }

    String replaceSlash(String s) {
        char[] charArray = s.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if (charArray[i] == '\\') {
                charArray[i] = '/';
            }
        }
        return new String(charArray);
    }

    String root() {
        String s = File.listRoots()[0].toString();
        return s.substring(0, s.length() - 1);
    }

    String absPath(String path) {
        while (path.charAt(0) == '/') {
            path = path.substring(1);
        }
        if (!path.contains(":")) {
            path = curDir + "/" + path;
        }
        return path;
    }

    void cd(final String path) throws NoSuchFileException {
        if (".".equals(path)) {
            return;
        }
        String previousDir = curDir;
        if ("..".equals(path)) {
            String[] dirs = curDir.split("/");
            curDir = root();
            for (int i = 1; i < dirs.length - 1; i++) {
                curDir += "/" + dirs[i];
            }
            return;
        }
        curDir = absPath(path);
        curDir = replaceSlash(curDir);
        File f = new File(curDir);
        if (!f.exists()) {
            curDir = previousDir;
            throw new NoSuchFileException("cd: " + path + ": No such file or directory");
        }
    }

    void mkdir(final String name) {
        File curDirFile = new File(curDir + "/" + name);
        curDirFile.mkdir();
    }

    void pwd() {
        System.out.println(curDir);
    }

    void ls() {
        String[] list = new File(curDir).list();
        for (String file : list) {
            System.out.println(file);
        }
    }

    void exit() {
        System.exit(1);
    }

    void cat(final String name) throws IOException {
        File file = new File(absPath(name));
        if (file.isDirectory()) {
            throw new NoSuchFileException("cat: " + name + ": is a directory");
        }
        if (!file.exists()) {
            throw new NoSuchFileException("cat: " + name + ": No such file or directory");
        }
        try (FileInputStream stream = new FileInputStream(file);
             Scanner scanner = new Scanner(stream)) {
            while (scanner.hasNext()) {
                System.out.println(scanner.nextLine());
            }
        } catch (IOException e) {
            throw new IOException("cat: " + name + ": IOException");
        }
    }

    void mv(String sourcePath, String destinationPath) throws IOException {
        destinationPath = absPath(destinationPath);
        sourcePath = absPath(sourcePath);

        try {
            File file = new File(sourcePath);
            File destinationFile = new File(destinationPath);
            String fileName = file.getName();

            Path source = Paths.get(sourcePath);

            Path destination = Paths.get(destinationPath + "/" + fileName);
            if (!destinationFile.exists() && destinationFile.getParent().equals(file.getParent())) {
                destination = Paths.get(destinationPath);
            }
            Files.move(source, destination);
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    mv(f.getAbsolutePath(), destinationPath + "/" + fileName);
                }
            }
        } catch (NoSuchFileException e) {
            throw new IOException("mv: " + sourcePath + " " + destinationPath + ": No such file or directory");
        } catch (IOException e) {
            throw new IOException("mv: " + sourcePath + " " + destinationPath + ": IOException");
        }
    }

    void rm(String path) throws IOException {
        path = absPath(path);
        File file = new File(path);
        try {
            if (file.isDirectory()) {
                throw new NoSuchFileException("rm: " + file.getName() + ": is a directory");
            }
            Files.delete(Paths.get(path));
        } catch (NoSuchFileException e) {
            throw new IOException("rm: " + path + ": No such file or directory");
        } catch (IOException e) {
            throw new IOException("rm: " + path + ": IOException");
        }
    }

    void rmR(String path) throws IOException {
        path = absPath(path);
        File file = new File(path);
        try {
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    rmR(path + "/" + f.getName());
                }
            }
            Files.delete(Paths.get(path));
        } catch (NoSuchFileException e) {
            throw new IOException("rm [-r]: " + path + ": No such file or directory");
        } catch (IOException e) {
            throw new IOException("rm [-r]: " + path + ": IOException");
        }
    }

    void cp(String sourcePath, String destinationPath) throws IOException {
        destinationPath = absPath(destinationPath);
        sourcePath = absPath(sourcePath);
        try {
            File file = new File(sourcePath);
            Path source = Paths.get(sourcePath);
            if (file.isDirectory()) {
                throw new IllegalArgumentException(("cp: " + sourcePath + ": is a directory (not copied)"));
            }
            String fileName = file.getName();
            File destinationFile = new File(destinationPath);
            if (!destinationFile.exists()) {
                destinationFile.mkdir();
            }
            Path destination = Paths.get(destinationPath + "/" + fileName);
            Files.copy(source, destination);
        } catch (NoSuchFileException e) {
            throw new IOException("cp: " + sourcePath + " " + destinationPath + ": No such file or directory");
        } catch (IOException e) {
            throw new IOException("cp: " + sourcePath + " " + destinationPath + ": IOException");
        }
    }

    void cpR(String sourcePath, String destinationPath) throws IOException {
        destinationPath = absPath(destinationPath);
        sourcePath = absPath(sourcePath);
        try {
            File file = new File(sourcePath);
            File destinationFile = new File(destinationPath);
            String fileName = file.getName();

            Path source = Paths.get(sourcePath);

            Path destination = Paths.get(destinationPath + "/" + fileName);
            if (!destinationFile.exists() && destinationFile.getParent().equals(file.getParent())) {
                destination = Paths.get(destinationPath);
            }
            if (!destinationFile.exists()) {
                destinationFile.mkdir();
            }
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    cpR(f.getAbsolutePath(), destinationPath + "/" + fileName);
                }
            } else {
                Files.copy(source, destination);
            }
        } catch (NoSuchFileException e) {
            throw new IOException("cp [-r]:" + sourcePath + " " + destinationPath + ": No such file or directory");
        } catch (IOException e) {
            throw new IOException("cp [-r]: " + sourcePath + " " + destinationPath + ": IOException");
        }
    }

    void runCommand(String inString, final PrintStream printStream) {
        HashMap<String, Integer> cmdMap = new HashMap<>();
        cmdMap.put("cd", 2);
        cmdMap.put("mkdir", 2);
        cmdMap.put("pwd", 1);
        cmdMap.put("ls", 1);
        cmdMap.put("exit", 1);
        cmdMap.put("cat", 2);
        cmdMap.put("mv", 3);
        boolean flag = printStream.equals(System.err);
        try {
            inString = inString.trim();
            String[] command = inString.split(" ");
            if (cmdMap.containsKey(command[0]) && cmdMap.get(command[0]) != command.length) {
                throw new ArrayIndexOutOfBoundsException();
            }
            if (command[0].equals("cd") && command.length == 2) {
                cd(command[1]);
            } else if (command[0].equals("mkdir")) {
                mkdir(command[1]);
            } else if (command[0].equals("pwd")) {
                pwd();
            } else if (command[0].equals("ls")) {
                ls();
            } else if (command[0].equals("exit")) {
                exit();
            } else if (command[0].equals("cat")) {
                cat(command[1]);
            } else if (command[0].equals("rm")) {
                    if (command[1].equals("-r")) {
                        if (command.length != 3) {
                            throw new ArrayIndexOutOfBoundsException();
                        }
                        rmR(command[2]);
                    } else {
                        if (command.length != 2) {
                            throw new ArrayIndexOutOfBoundsException();
                        }
                        rm(command[1]);
                    }
            } else if (command[0].equals("cp")) {
                    if (command[1].equals("-r")) {
                        if (command.length != 4) {
                            throw new ArrayIndexOutOfBoundsException();
                        }
                        cpR(command[2], command[3]);
                    } else {
                        if (command.length != 3) {
                            throw new ArrayIndexOutOfBoundsException();
                        }
                        cp(command[1], command[2]);
                    }
            } else if (command[0].equals("mv")) {
                    mv(command[1], command[2]);
            } else {
                printStream.println(command[0] + ": Unknown command");
                if (flag) {
                    System.exit(-3);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            printStream.println("Wrong command format " + "\"" + inString + "\": to few arguments\n");
            if (flag) {
                System.exit(-4);
            }
        } catch (NoSuchFileException e) {
            printStream.println(e.getMessage());
            if (flag) {
                System.exit(-1);
            }
        } catch (IOException e) {
            printStream.println(e.getMessage());
            if (flag) {
                System.exit(-2);
            }
        }
    }
}
