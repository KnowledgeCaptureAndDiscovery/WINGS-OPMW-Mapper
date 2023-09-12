package edu.isi.kcap.wings.opmm.Publisher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

public class FileSystem {
  private String directoryPath;
  private String webDomain;
  private File directory;

  public FileSystem(String directoryPath, String webDomain) {
    this.directoryPath = directoryPath;
    this.webDomain = webDomain;
    createDirectory();
  }

  private void createDirectory() {
    this.directory = new File(this.directoryPath);
    if (!directory.exists()) {
      directory.mkdirs();
    }
  }

  public FilePublished publishFile(File file) throws IOException {
    File newFile = new File(directoryPath + "/" + file.getName());
    FileUtils.copyFile(file, newFile);
    String relativePath = getRelativePath(newFile, directory);
    String fileUrl = getPublicUrl(relativePath);
    if (newFile.exists()) {
      FilePublished filePublished = new FilePublished(newFile.getAbsolutePath(), fileUrl);
      return filePublished;
    }
    throw new IOException("File " + file.getAbsolutePath() + " could not be published");
  }

  public static String getRelativePath(File file, File base) {
    Path path1 = Paths.get(base.getAbsolutePath());
    Path path2 = Paths.get(file.getAbsolutePath());
    String relativePath = path1.relativize(path2).toString();
    return relativePath;
  }

  public String getPublicUrl(String relativePath) {
    return this.webDomain + "/" + relativePath;
  }

}
