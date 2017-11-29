package com.saga.opencms.util

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * Created by jgregorio on 29/11/2017.
 */
class SgZip {
    /**
     * Compress file or directry to zip
     *
     * @param source path to File or directory to zip
     * @param zipPath new zip file path
     * @throws IOException
     */
    public static void zip(String source, String zipPath) throws IOException {
        FileOutputStream fos = new FileOutputStream(zipPath);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fileToZip = new File(source);
        zip(fileToZip, fileToZip.getName(), zipOut);
        zipOut.close();
        fos.close();
    }

    /**
     * Add zip files recursively including folders
     *
     * @param fileToZip
     * @param fileName
     * @param zipOut
     * @throws IOException
     */
    public static void zip(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            // avoid hidden files
            return;
        } else if (fileToZip.isDirectory()) {
            // zip all files contained into directory
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zip(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
        } else {
            // zip single file
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
        }

    }

    /**
     * Unzip file
     *
     * @param sourcePath path to zip file
     * @param destDirectory path to directory (keep zip files)
     * @throws IOException
     */
    public static void unzip(String sourcePath, String destDirectory) throws IOException {
        // Ensure directory exists
        String baseDir = destDirectory;
        if (!baseDir.endsWith("/")) {
            baseDir += "/";
        }
        new File(baseDir).mkdirs();

        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(sourcePath));
        ZipEntry zipEntry = zis.getNextEntry();

        while (zipEntry != null) {
            String fileName = zipEntry.getName();
            File newFile = new File(baseDir + fileName);
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }
}
