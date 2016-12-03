<%@ page buffer="none" session="false" trimDirectiveWhitespaces="true" %>
<%--pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>--%>

<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.opencms.main.CmsLog" %>
<%@ page import="java.io.*" %>
<%@ page import="java.nio.ByteBuffer" %>
<%@ page import="java.nio.charset.CharacterCodingException" %>
<%@ page import="java.nio.charset.Charset" %>
<%@ page import="java.nio.charset.CharsetDecoder" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>

<%!
	final Log LOG = CmsLog.getLog(this.getClass());

	/**
	 *
	 * @author Georgios Migdos
	 */
	public class CharsetDetector {

		public Charset detectCharset(File f, String[] charsets) {

			Charset charset = null;

			for (String charsetName : charsets) {
				charset = detectCharset(f, Charset.forName(charsetName));
				if (charset != null) {
					break;
				}
			}

			return charset;
		}

		private Charset detectCharset(File f, Charset charset) {
			try {
				BufferedInputStream input = new BufferedInputStream(new FileInputStream(f));

				CharsetDecoder decoder = charset.newDecoder();
				decoder.reset();

				byte[] buffer = new byte[512];
				boolean identified = false;
				while ((input.read(buffer) != -1) && (!identified)) {
					identified = identify(buffer, decoder);
				}

				input.close();

				if (identified) {
					return charset;
				} else {
					return null;
				}

			} catch (Exception e) {
				return null;
			}
		}

		private boolean identify(byte[] bytes, CharsetDecoder decoder) {
			try {
				decoder.decode(ByteBuffer.wrap(bytes));
			} catch (CharacterCodingException e) {
				return false;
			}
			return true;
		}
	}
%>



<%
	//    try {
//        String contextPath = OpenCms.getSystemInfo().getContextPath();
//        String filepath = contextPath + "lib/SagaSuiteSearch-1.0.jar";
//        LOG.debug("reading jar: " + filepath);
//
//        File f = new File(filepath);
//        LOG.debug("file found: " + f);
//
//
//    } catch (Exception e) {
//        LOG.error("ERROR checking encoding", e);
//    }



//    String[] charsetsToBeTested = {"UTF-8", "windows-1253", "ISO-8859-7"};
//
//    CharsetDetector cd = new CharsetDetector();
//    Charset charset = cd.detectCharset(f, charsetsToBeTested);
//
//    if (charset != null) {
//        try {
//            InputStreamReader reader = new InputStreamReader(new FileInputStream(f), charset);
//            int c = 0;
//            while ((c = reader.read()) != -1) {
//                System.out.print((char)c);
//            }
//            reader.close();
//        } catch (FileNotFoundException fnfe) {
//            fnfe.printStackTrace();
//        }catch(IOException ioe){
//            ioe.printStackTrace();
//        }
//
//    }else{
//        System.out.println("Unrecognized charset.");
//    }
%>

<div>
	<%
		String oldEncoding = request.getCharacterEncoding();
	%>
	<p>request encoding: <%=oldEncoding%></p>
	<p>param.firstname: <%=request.getParameter("firstname")%></p>
	<%
		request.setCharacterEncoding("UTF-8");
	%>
	<p>param.firstname (UTF-8): <%=request.getParameter("firstname")%></p>
	<%
		request.setCharacterEncoding(oldEncoding);
	%>
	<form method="get" accept-charset="UTF-8" >
		First name:<br>
		<input type="text" name="firstname" value="<c:out value='${param.firstname}'/>"><br>
		<input type="submit" value="Submit">
	</form>
</div>