<%@ page import="java.io.File" %>
<%@ page import="org.opencms.main.OpenCms" %>
<%@ page import="java.util.Arrays" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<ul>
	<%

		try {
			String file = OpenCms.getSystemInfo().getAbsoluteRfsPathRelativeToWebInf("logs/");

			File f = new File(file);
			String [] fileNames = f.list();
			Arrays.sort(fileNames);
			for (int i = 0; i < fileNames.length; i++) {
				out.println("<li>" + fileNames[i] + "</li>");
			}
		} catch (Exception e) {
			out.println("ERROR " + e);
		}
	%>
</ul>
