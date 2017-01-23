<%@ page import="java.io.File" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>

<%

	try {
		String file = application.getRealPath("/WEB-INF/lib");
		File f = new File(file);
		String [] fileNames = f.list();
		File [] fileObjects= f.listFiles();
		for (int i = 0; i < fileObjects.length; i++) {
//        if(!fileObjects[i].isDirectory()){
			String fname = file+fileNames[i];
			out.println(fileNames[i]);
//        }
		}
	} catch (Exception e) {
		out.println("ERROR " + e);
	}
%>