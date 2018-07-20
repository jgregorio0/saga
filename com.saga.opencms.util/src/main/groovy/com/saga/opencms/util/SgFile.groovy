package com.saga.opencms.util

import org.apache.commons.io.IOUtils
import org.apache.commons.logging.Log
import org.opencms.file.CmsFile
import org.opencms.file.CmsObject
import org.opencms.main.CmsLog
import org.opencms.xml.content.CmsXmlContent
import org.opencms.xml.content.CmsXmlContentFactory

import javax.servlet.ServletOutputStream
import javax.servlet.http.HttpServletResponse
import java.nio.file.Files
import java.nio.file.Paths

public class SgFile {
	private static final Log LOG = CmsLog.getLog(SgFile.class);

	public InputStream find (Class clazz, String path) throws URISyntaxException, IOException {
		return Files.newInputStream(Paths.get(clazz.getClassLoader().getResource(path).toURI()));
	}

	public void upload(OutputStream out, byte[] bytes) throws IOException {
		IOUtils.write(bytes, out);
		out.close();
	}

	public void upload(File file, byte[] bytes) throws IOException {
		BufferedOutputStream out = new BufferedOutputStream(
				new FileOutputStream(file));
		upload(out, bytes);
	}

	public void upload(String path, byte[] bytes) throws IOException {
		File file = new File(path);
		upload(file, bytes);
	}

	public void upload(String folder, String filename, byte[] bytes) throws IOException {
		makeDir(folder);
		File file = new File(folder + File.separator + filename);
		upload(file, bytes);
	}

	public boolean makeDir(String path) throws IOException {
		File dir = new File(path);
		return dir.mkdirs();
	}

	public String catalinaHomePath(){
		return System.getProperty("catalina.home");
	}

	public void downloadFile(
			HttpServletResponse response, String contentType,
			String contentDisposition, InputStream inputStream)
			throws IOException {

		final ServletOutputStream outputStream = response.getOutputStream();
		response.setContentType(contentType);
//                response.setHeader("Content-Disposition", "attachment;filename=\"" + e.getName().replaceAll("\\s+", " ").replaceAll("\\s+", "_") + "\"");
		response.setHeader("Content-Disposition", contentDisposition);
		IOUtils.copy(inputStream, outputStream);
		outputStream.flush();
		outputStream.close();
	}

	public void readBuffered(String filepath, String encoding){
		def lines = [];

		FileInputStream inputStream = null;
		Scanner sc = null;
		try {
			inputStream = new FileInputStream(filepath);
			sc = new Scanner(inputStream, encoding);
			int lineCounter = 0;
			def headers = [];
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				// TODO process data
				lines.add(line);
				lineCounter++;
			}
			// note that Scanner suppresses exceptions
			if (sc.ioException() != null) {
				LOG.error("Loading data from csv " + filepath, sc.ioException());
			}
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (sc != null) {
				sc.close();
			}
		}
	}

	public class SgFileLarge {
        private final Log LOG = CmsLog.getLog(SgFileLarge.class);

        File file;
        BufferedWriter bw;
        boolean isBufferClose;

        public void initTmpBufferedFile(String preffix, String suffix, String dir, String encoding) throws Exception {
            if (file != null || bw != null) {
                file = null;
                closeBuffer();
            }
            file = File.createTempFile(preffix, suffix, new File(dir));
            OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream(file, true), encoding);
            bw = new BufferedWriter(ow);
            isBufferClose = false;
        }

        public void initBufferedFile(String absFilePath, String encoding) throws Exception {
            if (file != null || bw != null) {
                file = null;
                closeBuffer();
            }
            file = new File(absFilePath);
            file.createNewFile();
            OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream(file, true), encoding);
            bw = new BufferedWriter(ow);
            isBufferClose = false;
        }

        public void appendBuffer(String textToAppend) throws IOException {
            bw.write(textToAppend);
        }

        public void closeBuffer() {
            try {
                bw.close();
                isBufferClose = true;
            } catch (IOException e) {
                LOG.error("SgFileLarge.closeBuffer", e);
            }
        }

        public boolean rmBufferedFile() {
            return file.delete();
        }

        public String getFileAbsolutePath() {
            return file.getAbsolutePath();
        }

        public String getFileName() {
            return file.getName();
        }

        public void readBuffered(String encoding){
            FileInputStream inputStream = null;
            Scanner sc = null;
            try {
                inputStream = new FileInputStream(file);
                sc = new Scanner(inputStream, encoding);
                int counter = 0;
                while (sc.hasNextLine()) {
                    counter++;
                    String line = sc.nextLine();
                    doEachLine(line, counter);
                }
                // note that Scanner suppresses exceptions
                if (sc.ioException() != null) {
                    sc.ioException().printStackTrace();
                }
            } catch (FileNotFoundException e) {
                LOG.error("SgFileLarge.readBuffered", e)
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        LOG.error("SgFileLarge.readBuffered", e)
                    }
                }
                if (sc != null) {
                    sc.close();
                }
            }
        }

        protected void doEachLine(String line, int counter){
            // TODO overwrite
        }
	}
}