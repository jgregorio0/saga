<%@ page import="org.opencms.main.OpenCms" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>
<div>
    <h1>Nodo</h1>
    <p>Nombre del nodo: <%= OpenCms.getSystemInfo().getServerName() %></p>

    <h1>##### Heap utilization statistics [MB] #####</h1>
    <%

        int mb = 1024 * 1024;

        //Getting the runtime reference from system
        Runtime runtime = Runtime.getRuntime();

        //Print used memory
        String usedMem = "Used Memory:"
                + (runtime.totalMemory() - runtime.freeMemory()) / mb;

        //Print free memory
        String freeMem = "Free Memory:"
                + runtime.freeMemory() / mb;

        //Print total available memory
        String totalMem = "Total Memory:" + runtime.totalMemory() / mb;

        //Print Maximum available memory
        String maxMem = "Max Memory:" + runtime.maxMemory() / mb;
    %>
    <p><%=usedMem%>
    </p>

    <p><%=freeMem%>
    </p>

    <p><%=totalMem%>
    </p>

    <p><%=maxMem%>
    </p>
</div>