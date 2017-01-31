package com.saga.opencms.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.http.HttpResponse;
import org.opencms.main.CmsLog;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by jgregorio on 09/09/2016.
 * Ref: http://www.baeldung.com/httpclient-post-http-request
 */
public class SgFile {
	private static final Log LOG = CmsLog.getLog(SgFile.class);

//	@Autowired
//	private ResourceLoader resourceLoader;
//
//	public InputStream findSpring(String path) {
//		Resource resource = resourceLoader.getResource("classpath:" + mailPath + messagesPath);
//		resource.getInputStream();
//	}

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