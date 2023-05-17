package edu.isi.kcap.wings.opmm;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.List;

import org.openprovenance.prov.interop.InteropFramework;
import org.openprovenance.prov.model.Bundle;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.Entity;
import org.openprovenance.prov.model.ProvUtilities;
import org.openprovenance.prov.model.Statement;
import org.openprovenance.prov.model.StatementOrBundle;

public class ProvNUtils {

  /**
   * Convert from ttl/xml to ProvN format
   *
   * @param source
   * @param target
   */
  public static Document convertToProvN(Path source, Path target) {
    File file = source.toFile();
    InteropFramework intF = new InteropFramework();
    Document document = intF.readDocumentFromFile(file.getAbsolutePath());
    intF.writeDocument(target.toString(), document);
    return document;
  }

  /**
   * Convert from ttl/xml to ProvN format and return it as string
   *
   * @param source
   * @param target
   */
  public static String convertToProvN(Path source) {
    File file = source.toFile();
    InteropFramework intF = new InteropFramework();
    Document document = intF.readDocumentFromFile(file.getAbsolutePath());
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    intF.writeDocument(stream, intF.getTypeForFormat("provn"), document);
    return new String(stream.toByteArray());
  }

  /**
   * Get the entity with the given local name from the Document.
   *
   * @param Document  A document
   * @param localname The local name of the entity
   * @return The entity with the given local name
   * @throws RuntimeException if the entity is not found
   */
  public static Entity getEntityByLocalName(Document document, String localname) throws RuntimeException {
    ProvUtilities u = new ProvUtilities();
    List<Entity> entities = u.getEntity(document);
    for (Entity entity : entities) {
      String entityLocalName = entity.getId().getLocalPart();
      if (entityLocalName.equals(localname)) {
        return entity;
      }
    }
    throw new RuntimeException("Record not found: " + localname);
  }

}
