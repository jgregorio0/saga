package com.saga.opencms.util

import org.apache.commons.fileupload.FileItem
import org.apache.commons.io.FilenameUtils


class SgExt {

    static Map fileExtensions(){
        return URLConnection.getFileNameMap();
    }

    static boolean isFileType(FileItem file, String contentType){
        boolean is = false;

        String cntType = file.getContentType();
        if (cntType != null && cntType.startsWith(contentType)) {
            is = true;
        }

        return is;
    }

    static boolean isFileExt(String filename, List<String> exts) {
        return FilenameUtils.isExtension(filename, exts);
    }

    static String getExtension(String filename) {
        return FilenameUtils.getExtension();
    }
}