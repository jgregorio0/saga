<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true"%>
<%!
	private static final long MEGABYTE = 1024L * 1024L;
	private static long totalMemory;
	private static long freeMemory;
	private static long restMemory;

	public static long bytesToMegabytes(long bytes) {
		return (bytes / MEGABYTE);
	}

	public static void test() {
		// I assume you will know how to create a object Person yourself...
		List<String> list = new ArrayList<String>();
		for (int i = 0; i <= 100000; i++) {
			list.add(new String("Hola - " + i));
		}
		// Get the Java runtime
		Runtime runtime = Runtime.getRuntime();
		// Run the garbage collector
		runtime.gc();
		// Calculate the used memory
		totalMemory = runtime.totalMemory();
		freeMemory = runtime.freeMemory();
		restMemory =  totalMemory - freeMemory;
		System.out.println("Used memory is bytes: " + restMemory);
		System.out.println("Used memory is megabytes: " + bytesToMegabytes(restMemory));
	}
%>
<div>
<%
	test();
%>
	totalMemory: <%=totalMemory%>
	freeMemory: <%=freeMemory%>
	restMemory: <%=restMemory%>
</div>