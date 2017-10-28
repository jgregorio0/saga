<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@page buffer="none" session="false"%>

<%--<%=System.getProperty("file.encoding")%>--%>
<%
  String[] encodings = new String[]{"ISO-8859-5",
          "KOI8-R",
          "WINDOWS-1251",
          "MACCYRILLIC",
          "IBM866",
          "IBM855",
          "ISO-8859-7",
          "WINDOWS-1253",
          "ISO-8859-8",
          "WINDOWS-1255",
          "ISO-2022-JP",
          "Shift_JIS",
          "EUC-JP",
          "ISO-2022-KR",
          "EUC-KR",
          "UTF-8",
          "UTF-16BE",
          "UTF-16LE",
          "UTF-32BE",
          "UTF-32LE",
          "WINDOWS-1252"};

  String esp = "España";
  Map<String, String> espEncs = new HashMap<String, String>();
  for (int i = 0; i < encodings.length; i++) {
    String encoding = encodings[i];
    String espEnc = new String(esp.getBytes(), encoding);
    espEncs.put(encoding, espEnc);
  }

%>

<div>
  <c:forEach var="entry" items="<%=espEncs.entrySet()%>">
    <p>${entry.key}: ${entry.value}</p>
  </c:forEach>
</div>