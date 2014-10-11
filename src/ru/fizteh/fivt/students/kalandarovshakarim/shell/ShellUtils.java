/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.shell;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.NoSuchFileException;
import java.nio.file.attribute.BasicFileAttributes;

/**
 *
 * @author Shakarim
 */
public class ShellUtils {

    private Path workingDirectory;

    public ShellUtils() {
        this.workingDirectory
                = new File(System.getProperty("user.home")).toPath();
    }

    public ShellUtils(Path workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    private Path getPath(String fileName) {
        Path path = Paths.get(fileName);
        if (workingDirectory != null && !path.isAbsolute()) {
            path = workingDirectory.resolve(path);
        }
        path = path.normalize();
        return path;
    }

    public String getCwd() {
        return workingDirectory.toString();
    }

    public String[] listFiles() {
        return workingDirectory.toFile().list();
    }

    public void mkDir(String dirName) throws IOException {
        Path dir = getPath(dirName);
        try {
            Files.createDirectory(dir);
        } catch (FileAlreadyExistsException e) {
            String exMsg = "cannot create directory '%s' already exists";
            throw new IOException(String.format(exMsg, dirName));
        }
    }

    public void chDir(String pathToDir) throws FileNotFoundException,
            IOException {
        Path path = getPath(pathToDir);
        File dirToGo = path.toFile();

        if (dirToGo.isDirectory()) {
            workingDirectory = path;
        } else if (dirToGo.exists()) {
            String exMsg = "'%s' is not Directory";
            throw new IOException(String.format(exMsg, pathToDir));
        } else {
            throw new FileNotFoundException(pathToDir);

        }
    }

    public void cat(String fileName) throws IOException {

        File file = getPath(fileName).toFile();

        if (file.isDirectory()) {
            String exMsg = "'%s' is Directory";
            throw new IOException(String.format(exMsg, fileName));
        }

        Files.copy(file.toPath(), System.out);
    }

    private void rmDir(File dir) {
        File[] dirList = dir.listFiles();
        for (File file : dirList) {
            if (file.isDirectory()) {
                rmDir(file);
            }
            file.delete();
        }
        dir.delete();
    }

    public void rm(String fileName, boolean recursive)
            throws FileNotFoundException, IOException {

        File file = getPath(fileName).toFile();

        if (!file.exists()) {
            throw new FileNotFoundException(fileName);
        }

        if (file.isDirectory() && !recursive) {
            String exMsg = "'%s' is Directory";
            throw new IOException(String.format(exMsg, fileName));
        }

        if (file.isFile()) {
            file.delete();
        } else {
            rmDir(file);
        }

    }

    public void cp(String source, String destination, boolean recursive)
            throws NoSuchFileException, IOException {
        File srcFile = getPath(source).toFile();
        File destFile = getPath(destination).toFile();

        if (!srcFile.exists()) {
            throw new FileNotFoundException(source);
        }

        if (srcFile.isDirectory() && !recursive) {
            String exMsg = "'%s' is Directory";
            throw new IOException(String.format(exMsg, source));
        }

        if (srcFile.equals(destFile)) {
            String exMsg = "'%s' and '%s' are the same";
            throw new IOException(String.format(exMsg, source, destination));
        }
        /*
         if (destFile.isDirectory() && destFile.exists()) {
         destFile = new File(destFile, srcFile.getName());
         }
         /*
         if (!destFile.exists()) {
         if (srcFile.isDirectory() && !destFile.mkdir()) {
         throw new FileNotFoundException(destination);
         }
         if (srcFile.isFile() && !destFile.createNewFile()) {
         throw new FileNotFoundException(destination);
         }
         }*/
        if (srcFile.isFile()) {
            if (destFile.isDirectory()) {
                destFile = new File(destFile, source);
            }
            Files.copy(srcFile.toPath(), destFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        } else {
            Files.walkFileTree(srcFile.toPath(), new FileVisitorImpl(srcFile, destFile));
        }
    }

    public void mv(String source, String destionation)
            throws FileNotFoundException, IOException {
        this.cp(source, destionation, true);
        this.rm(source, true);
    }

    private static class FileVisitorImpl implements FileVisitor<Path> {

        private final File srcFile;
        private final File destFile;

        public FileVisitorImpl(File srcFile, File destFile) {
            if (destFile.isDirectory()) {
                this.srcFile = srcFile.getParentFile();
            } else {
                this.srcFile = srcFile;
            }
            this.destFile = destFile;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            Path relative = srcFile.toPath().relativize(dir);
            Path destinationDir = destFile.toPath().resolve(relative);
            if (!destinationDir.equals(destFile.toPath()) || !Files.exists(destFile.toPath())) {
                Files.createDirectory(destinationDir);
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Path relative = srcFile.toPath().relativize(file);
            Path destinationFile = destFile.toPath().resolve(relative);
            if (Files.exists(destinationFile)) {
                throw new IOException(String.format("'%s' File already exists", destinationFile.toString()));
            }
            Files.copy(file, destinationFile);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }
    }
}
