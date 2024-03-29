/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.isi.kcap.wings.opmm;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class StorageHandler {
    public File zipFolder(File directory) throws Exception {
        // Create zip file
        File _tmpZip = File.createTempFile(directory.getAbsolutePath(), ".zip");
        String zipName = _tmpZip.getPath();
        Path zipPath = Paths.get(zipName);

        // Obtain path source dir
        Path sourceFolderPath = directory.toPath();

        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()));
        Files.walkFileTree(sourceFolderPath, new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                zos.putNextEntry(new ZipEntry(sourceFolderPath.relativize(file).toString()));
                Files.copy(file, zos);
                zos.closeEntry();
                return FileVisitResult.CONTINUE;
            }
        });
        zos.close();
        return _tmpZip;
    }

    public void zipFile(File sourceFile, Path zipPath) throws Exception {
        FileOutputStream fos = new FileOutputStream(zipPath.toFile());
        ZipOutputStream zos = new ZipOutputStream(fos);
        ZipEntry ze = new ZipEntry(sourceFile.getName());
        zos.putNextEntry(ze);
        FileInputStream fis = new FileInputStream(sourceFile);
        byte[] bytesRead = new byte[512];
        int bytesNum;
        while ((bytesNum = fis.read(bytesRead)) > 0) {
            zos.write(bytesRead, 0, bytesNum);
        }
        fis.close();
        zos.closeEntry();
        zos.close();
        fos.close();
    }

}
