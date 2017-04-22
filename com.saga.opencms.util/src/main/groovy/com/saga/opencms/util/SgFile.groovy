package com.saga.opencms.util

import org.apache.commons.io.IOUtils
import org.apache.commons.logging.Log
import org.opencms.main.CmsLog

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
}