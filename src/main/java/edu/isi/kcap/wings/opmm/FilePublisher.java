package edu.isi.kcap.wings.opmm;

import java.io.File;
import java.io.IOException;

import edu.isi.kcap.wings.opmm.Publisher.FilePublished;
import edu.isi.kcap.wings.opmm.Publisher.FileSystem;

public class FilePublisher {

    public static enum Type {
        SSH,
        HTTP,
        FILE_SYSTEM,
    }

    FilePublisher.Type type;
    FileSystem fileSystem;

    public FilePublisher(Type type, String fileSystemDirectory, String fileSystemWebPath) {
        this.type = type;
        switch (this.type) {
            case FILE_SYSTEM:
                if (fileSystemDirectory == null || fileSystemWebPath == null) {
                    throw new IllegalArgumentException("Directory and webDomain must be specified for FILE_SYSTEM");
                }
                this.fileSystem = new FileSystem(fileSystemDirectory, fileSystemWebPath);
                break;
            default:
                break;
        }
    }

    public FilePublished publishFile(File file) throws IOException {
        switch (this.type) {
            case FILE_SYSTEM:
                return this.fileSystem.publishFile(file);
            default:
                return null;
        }
    }

    public FilePublished publishFile(String filePath) throws IOException {
        String filePathClear = filePath.replaceAll("\\s", "");
        File file = new File(filePathClear);
        if (!file.exists()) {
            System.err.println("File " + filePath + " does not exist");
            // throw new IOException("File " + filePath + " does not exist");
        }
        switch (this.type) {
            case FILE_SYSTEM:
                return this.fileSystem.publishFile(file);
            default:
                return null;
        }
    }
}
