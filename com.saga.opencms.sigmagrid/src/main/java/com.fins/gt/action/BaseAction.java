/*     */ package com.fins.gt.action;
/*     */ 
/*     */ import com.fins.gt.util.LogHandler;
import com.fins.gt.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ 
/*     */ public class BaseAction
/*     */ {
/*     */   private PrintWriter out;
/*     */   protected HttpServletRequest request;
/*     */   protected HttpServletResponse response;
/*     */   protected Map parameterMap;
/*  22 */   protected boolean printed = false;
/*     */   protected String viewPath;
/*     */   protected String viewBasePath;
/*     */   protected ServletWrapper servletWrapper;
/*     */ 
/*     */   public void service()
/*     */     throws ServletException, IOException
/*     */   {
/*  30 */     throw new ServletException("Please override service() Method!");
/*     */   }
/*     */ 
/*     */   public void init(HttpServletRequest request, HttpServletResponse response) {
/*  34 */     setRequest(request);
/*  35 */     setResponse(response);
/*  36 */     setServletWrapper(new ServletWrapper(request, response));
/*     */   }
/*     */ 
/*     */   protected void println(Object text) {
/*  40 */     print(text);
/*  41 */     println();
/*     */   }
/*     */ 
/*     */   protected void println() {
/*  45 */     this.printed = true;
/*  46 */     getOut().println();
/*     */   }
/*     */   protected void print(Object text) {
/*  49 */     this.printed = true;
/*  50 */     getOut().print(String.valueOf(text));
/*     */   }
/*     */ 
/*     */   protected void flushOut() {
/*  54 */     getOut().flush();
/*     */   }
/*     */   protected void closeOut() {
/*  57 */     getOut().close();
/*     */   }
/*     */ 
/*     */   protected String getViewUrl(String viewName)
/*     */   {
/*  62 */     String queryStr = null;
/*  63 */     int aidx = viewName.indexOf('?');
/*  64 */     if (aidx > 0) {
/*  65 */       queryStr = viewName.substring(aidx + 1);
/*  66 */       viewName = viewName.substring(0, aidx);
/*     */     }
/*  68 */     if ((!StringUtils.endsWithIgnoreCase(viewName, ".jsp")) && 
/*  69 */       (!StringUtils.endsWithIgnoreCase(viewName, ".html")) && 
/*  70 */       (!StringUtils.endsWithIgnoreCase(viewName, ".htm")))
/*     */     {
/*  72 */       viewName = viewName + ".jsp";
/*     */     }
/*  74 */     if (StringUtils.isNotEmpty(queryStr)) {
/*  75 */       viewName = viewName + '?' + queryStr;
/*     */     }
/*     */ 
/*  78 */     if (viewName.indexOf("//") == 0) {
/*  79 */       return viewName.substring(1);
/*     */     }
/*  81 */     if (viewName.indexOf("/") == 0) {
/*  82 */       return getViewBasePath() + viewName;
/*     */     }
/*  84 */     String viewPath = getViewPath();
/*  85 */     if (viewName.indexOf("../") == 0) {
/*  86 */       viewName = viewName.substring(3);
/*  87 */       int idx = viewPath.lastIndexOf('/');
/*  88 */       viewPath = viewPath.substring(0, idx);
/*     */     }
/*  90 */     return getViewBasePath() + viewPath + "/" + viewName;
/*     */   }
/*     */ 
/*     */   protected void redirect(String viewName) throws IOException
/*     */   {
/*  95 */     this.response.sendRedirect(getViewUrl(viewName));
/*     */   }
/*     */ 
/*     */   protected void forward(String viewName) throws ServletException, IOException {
/*  99 */     this.request.getRequestDispatcher(getViewUrl(viewName)).forward(this.request, this.response);
/*     */   }
/*     */ 
/*     */   protected void include(String viewName) throws ServletException, IOException {
/* 103 */     this.request.getRequestDispatcher(getViewUrl(viewName)).include(this.request, this.response);
/*     */   }
/*     */ 
/*     */   public HttpServletRequest getRequest() {
/* 107 */     return this.request;
/*     */   }
/*     */   public void setRequest(HttpServletRequest request) {
/* 110 */     this.request = request;
/* 111 */     setParameterMap(request.getParameterMap());
/*     */   }
/*     */   public HttpServletResponse getResponse() {
/* 114 */     return this.response;
/*     */   }
/*     */   public void setContentType(String contextType) {
/* 117 */     this.response.setContentType(contextType);
/*     */   }
/*     */ 
/*     */   public void setResponse(HttpServletResponse response) {
/* 121 */     this.response = response;
/*     */   }
/*     */ 
/*     */   public boolean isPrinted() {
/* 125 */     return this.printed;
/*     */   }
/*     */ 
/*     */   public String getViewPath() {
/* 129 */     return this.viewPath;
/*     */   }
/*     */ 
/*     */   public void setViewPath(String viewPath) {
/* 133 */     this.viewPath = viewPath;
/*     */   }
/*     */ 
/*     */   public String getViewBasePath() {
/* 137 */     return this.viewBasePath;
/*     */   }
/*     */ 
/*     */   public void setViewBasePath(String viewBasePath) {
/* 141 */     this.viewBasePath = viewBasePath;
/*     */   }
/*     */ 
/*     */   public Map getParameterMap() {
/* 145 */     return this.parameterMap;
/*     */   }
/*     */ 
/*     */   public Map getParameterSimpleMap() {
/* 149 */     Iterator kit = this.parameterMap.keySet().iterator();
/* 150 */     Map param = new HashMap();
/* 151 */     while (kit.hasNext()) {
/* 152 */       String key = String.valueOf(kit.next());
/* 153 */       String[] v = (String[])null;
/*     */       try {
/* 155 */         v = (String[])this.parameterMap.get(key);
/*     */       } catch (Exception e) {
/* 157 */         continue;
/*     */       }
/* 159 */       if ((v != null) && (v.length == 1))
/* 160 */         param.put(key, v[0]);
/*     */       else {
/* 162 */         param.put(key, v);
/*     */       }
/*     */     }
/* 165 */     return param;
/*     */   }
/*     */ 
/*     */   public String getParameter(String name) {
/* 169 */     return this.request.getParameter(name);
/*     */   }
/*     */ 
/*     */   public void setParameterMap(Map parameterMap) {
/* 173 */     this.parameterMap = parameterMap;
/*     */   }
/*     */ 
/*     */   public void dispose() {
/* 177 */     this.out = null;
/* 178 */     this.request = null;
/* 179 */     this.response = null;
/* 180 */     this.printed = false;
/* 181 */     this.viewPath = null;
/* 182 */     this.viewBasePath = null;
/*     */   }
/*     */ 
/*     */   public ServletWrapper getServletWrapper()
/*     */   {
/* 187 */     return this.servletWrapper;
/*     */   }
/*     */ 
/*     */   public void setServletWrapper(ServletWrapper servletWrapper)
/*     */   {
/* 192 */     this.servletWrapper = servletWrapper;
/*     */   }
/*     */ 
/*     */   public PrintWriter getOut() {
/* 196 */     if (this.out == null) {
/*     */       try {
/* 198 */         setOut(this.response.getWriter());
/*     */       } catch (IOException e) {
/* 200 */         LogHandler.error(this, e);
/*     */       }
/*     */     }
/* 203 */     return this.out;
/*     */   }
/*     */ 
/*     */   public void setOut(PrintWriter out) {
/* 207 */     this.out = out;
/*     */   }
/*     */ }

/* Location:           F:\software\sigma_grid_with_jsp_sample_release\something missing\something missing.zip
 * Qualified Name:     com.fins.gt.action.BaseAction
 * JD-Core Version:    0.6.0
 */