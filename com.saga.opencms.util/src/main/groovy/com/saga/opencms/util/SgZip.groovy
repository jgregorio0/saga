package com.saga.opencms.util

import org.apache.commons.io.FilenameUtils

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
     * @throws IOException
     */
    public static String zip(String source) throws IOException {
        File fileToZip = new File(source);
        String destName = FilenameUtils.removeExtension(fileToZip.getName()) + ".zip";
        zip(source, fileToZip.getParent() + "/" + destName, false);
        return fileToZip.getParent();
    }

    /**
     * Compress file or directry to zip
     *
     * @param source  path to File or directory to zip
     * @param zipPath new zip file path
     * @throws IOException
     */
    public static String zip(String source, String zipPath)
            throws IOException, IllegalArgumentException {
        return zip(source, zipPath, false);
    }

    /**
     * Compress file or directory to zip
     * @param source
     * @param zipPath
     * @param deleteIfExists
     * @return
     * @throws IOException
     * @throws IllegalArgumentException
     */
    public static String zip(String source, String zipPath, boolean deleteIfExists)
            throws IOException, IllegalArgumentException {

        if (deleteIfExists) {
            ensureNotExist(zipPath);
        }
        exceptionIfExist(zipPath);
        ensureBaseDir(new File(zipPath).getParent());

        FileOutputStream fos = new FileOutputStream(zipPath);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fileToZip = new File(source);
        zip(fileToZip, zipOut);
        zipOut.close();
        fos.close();
        return zipPath;
    }

    /**
     * Zip file into out
     *
     * @param fileToZip
     * @param zipOut
     * @throws IOException
     */
    public static void zip(File fileToZip, ZipOutputStream zipOut) throws IOException {
        zip(fileToZip, fileToZip.getName(), zipOut);
    }

    /**
     * Zip files recursively including folders
     *
     * @param fileToZip
     * @param zipOut
     * @throws IOException
     */
    public static void zip(File fileToZip, String zipEntryName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            // avoid hidden files
            return;
        } else if (fileToZip.isDirectory()) {
            // zip all files contained into directory
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zip(childFile, zipEntryName + "/" + childFile.getName(), zipOut);
            }
        } else {
            // zip single file
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(zipEntryName);
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
     * Zip multiples files or directories
     *
     * @param srcPaths
     * @param zipPath
     * @throws IOException
     */
    public static String zip(List<String> srcPaths, String zipPath) throws IOException {
        FileOutputStream fos = new FileOutputStream(zipPath);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        for (String srcPath : srcPaths) {
            File fileToZip = new File(srcPath);
            zip(fileToZip, zipOut);
        }
        zipOut.close();
        fos.close();
        return zipPath;
    }

    /**
     * Unzip file to folder with the same name
     *
     * @param sourcePath path to zip file
     * @param deleteIfExist
     * @throws IOException
     */
    public static String unzipInNewFolder(String sourcePath, boolean deleteIfExist) throws IOException {
        // Destination directory base on source path
        String destDir = generateUnzipDirPath(sourcePath);

        // Delete if exist
        if (deleteIfExist) {
            ensureNotExist(destDir);
        }

        // unzip
        unzip(sourcePath, destDir);
        return destDir;
    }
    /**
     * Unzip file to folder with the same name
     *
     * @param sourcePath path to zip file
     * @throws IOException
     */
    public static String unzipInParentFolder(String sourcePath) throws IOException {
        File file = new File(sourcePath);
        unzip(sourcePath, file.getParent());
        return file.getParent();
    }


    /**
     * Unzip file
     *
     * @param sourcePath path to zip file
     * @param unzipDir   path to directory (keep zip files)
     * @throws IOException
     */
    public static String unzip(String sourcePath, String unzipDir)
            throws IOException, IllegalArgumentException {
        // Ensure directory exists
        String baseDir = ensureBaseDir(unzipDir);

        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(sourcePath));
        ZipEntry zipEntry = zis.getNextEntry();

        while (zipEntry != null) {
            String fileName = zipEntry.getName();
            File newFile = new File(baseDir + fileName);
            exceptionIfExist(newFile);

            // Ensure file
            ensureFile(newFile);

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
        return unzipDir;
    }

    /**
     * Ensure base directory ends with / and create directory and parent directories if not exist.
     * @param unzipDir
     * @return
     */
    public static String ensureBaseDir(String unzipDir) {
        String baseDir = unzipDir;
        if (!baseDir.endsWith("/")) {
            baseDir += "/";
        }
        ensureDirectory(baseDir);
        return baseDir;
    }

    /**
     * Create parent directories and file if not exist
     * @param newFile
     * @throws IOException
     */
    public static void ensureFile(File newFile) throws IOException {
        // Ensure parent folder
        ensureDirectory(newFile.getParent());

        // Create file if not exists
        if (!newFile.exists()) {
            newFile.createNewFile();
        }
    }

    /**
     * Create directory and parent directories if not exist.
     * @param baseDir
     */
    public static void ensureDirectory(String baseDir) {
        new File(baseDir).mkdirs();
    }

    /**
     * Ensure detination path do not exist already.
     * @param zipPath
     * @throws IllegalArgumentException
     */
    private static void exceptionIfExist(String zipPath)
            throws IllegalArgumentException {
        exceptionIfExist(new File(zipPath));
    }

    /**
     * Ensure detination path do not exist already.
     * @param file
     * @throws IllegalArgumentException
     */
    private static void exceptionIfExist(File file)
            throws IllegalArgumentException {
        if (file.exists()) {
            throw new IllegalArgumentException("Destination file path already exists: " + file.getAbsolutePath());
        }
    }

    /**
     * Generate unzip directory path using source path
     * @param sourcePath
     * @return
     */
    public static String generateUnzipDirPath(String sourcePath) {
        File file = new File(sourcePath);
        return FilenameUtils.removeExtension(file.getPath());
    }


    /**
     * Ensure detination directory does not exist. Delete if exists.
     * @param destDir
     */
    public static void ensureNotExist(String destDir) throws IOException {
        File destFile = new File(destDir);
        if (destFile.exists()) {
            delete(destFile);
        }
    }

    /**
     * Delete files recursively
     * @param file
     * @throws IOException
     */
    public static void delete(File file) throws IOException {
        if (file.isDirectory()) {
            for (File child : file.listFiles())
                delete(child);
        }
        if (!file.delete())
            throw new FileNotFoundException("Failed to delete file: " + file);
    }
}
