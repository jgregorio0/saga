package com.saga.opencms.util

import org.apache.commons.io.IOUtils
import org.opencms.file.CmsFile
import org.opencms.file.CmsObject

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

class SgServer {

    /**
     * Search content on server
     * @param host Where search
     * @param path Resource to search
     * @return
     */
    public static byte[] searchOnServer(String host, String path) {
            URL url = new URL(host + path);
            URLConnection connection = url.openConnection();
            InputStream is = connection.getInputStream();
            byte[] fileBytes = IOUtils.toByteArray(is);
            return fileBytes;
    }

    /**
     * Search image on server
     * @param path
     * @param host
     * @return
     */
    def searchImageOnServer(String host, String path) {
        URL imageURL = new URL(host + path);
        BufferedImage bufferedImage = ImageIO.read(imageURL);
        return bufferedImage
    }

    /**
     * Read image content
     * @param cmso
     * @param path
     * @return
     */
    public static BufferedImage readImageFromCms(CmsObject cmso, String path) {
        CmsFile imageFile = cmso.readFile(path);
        byte[] bytes = imageFile.getContents();
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        BufferedImage image = null;
        image = ImageIO.read(bis);
        return image
    }

    /**
     * Validate image content
     * @param path
     * @return
     */
    static boolean isImageContent(CmsObject cmso, String path) {
        BufferedImage image = readImageFromCms(cmso, path);
        return image != null
    }

    /**
     * Repair image content
     */
    static def repairImage(CmsObject cmso, String path, BufferedImage bufferedImage) {
        String format = obtainFormat(path)
        byte[] imageInByte = bufImage2Bytes(bufferedImage, format);

        CmsFile imageFile = cmso.readFile(path);
        imageFile.setContents(imageInByte)

        cmso.lockResource(path)
        imageFile.setContents(imageInByte)
        cmso.writeFile(imageFile)
        cmso.unlockResource(path)
        return this;
    }

    /**
     * Obtain image format from name
     * @param path
     * @return
     */
    static String obtainFormat(String path) {
        int dot = path.lastIndexOf(".")
        if (dot < 1 || ((dot + 1) > path.length())) {
            throw new IllegalArgumentException("ERROR: obtaining format from $path")
        }
        String formatOrigin = path.substring(dot + 1).toLowerCase();
        String[] formats = ImageIO.getWriterFileSuffixes()
        boolean encontrado = false;
        String format = null
        formats.each {
            if (it.equals(formatOrigin)) {
                format = it
            }
        }

        return format
    }

    /**
     * Transform buffered images to bytes
     * @param bufferedImage
     * @param format
     * @return
     */
    byte[] bufImage2Bytes(BufferedImage bufferedImage, format) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, format, baos );
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();

        return imageInByte
    }
}