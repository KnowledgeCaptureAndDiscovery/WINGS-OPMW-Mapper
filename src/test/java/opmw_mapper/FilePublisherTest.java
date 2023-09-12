package opmw_mapper;

import java.io.File;

import org.junit.Test;
import java.io.IOException;
import java.nio.file.Files;

import edu.isi.kcap.wings.opmm.FilePublisher;
import edu.isi.kcap.wings.opmm.Publisher.FilePublished;
import junit.framework.Assert;

public class FilePublisherTest {
  String webPath = "http://localhost:8080";
  String directory = "/tmp/opmw";
  // Create a temporal dierctory
  FilePublisher filePublisher;

  public FilePublisherTest() throws IOException {
    File tempDirectory = Files.createTempDirectory("opmw").toFile();
    this.filePublisher = new FilePublisher(FilePublisher.Type.FILE_SYSTEM, tempDirectory.getAbsolutePath(),
        webPath);
  }

  @Test
  public void publishFileTest() throws IOException {
    File tempFile = Files.createTempFile("test", ".txt").toFile();
    FilePublished newFile = this.filePublisher.publishFile(tempFile);
    File file = new File(newFile.getFilePath());
    Assert.assertTrue(file.exists());
  }
}
