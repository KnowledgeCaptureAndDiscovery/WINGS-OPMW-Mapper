package edu.isi.kcap.wings.opmm.Publisher;

/**
 * A class that represents a file that has been published.
 * It contains the path to the file and the URL where it can be accessed.
 *
 */

public class FilePublished {
  public FilePublished(String path, String url) {
    this.filePath = path;
    this.fileUrl = url;
  }

  String filePath;
  String fileUrl;

  public String getFilePath() {
    return this.filePath;
  }

  public String getFileUrl() {
    return this.fileUrl;
  }
}
