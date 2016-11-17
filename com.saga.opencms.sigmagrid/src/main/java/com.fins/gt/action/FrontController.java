/*     */ package com.fins.gt.action;
/*     */ 
/*     */ import com.fins.gt.server.DataAccessManagerLoader;
/*     */ import com.fins.gt.util.LogHandler;
/*     */ import com.fins.gt.util.StringUtils;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.servlet.Filter;
/*     */ import javax.servlet.FilterChain;
/*     */ import javax.servlet.FilterConfig;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletResponse;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ 
/*     */ public class FrontController
/*     */   implements Filter
/*     */ {
/*     */   static final long serialVersionUID = 1L;
/*  27 */   public String webRootRealPath = null;
/*  28 */   public ServletContext servletContext = null;
/*  29 */   protected FilterConfig filterConfig = null;
/*     */ 
/*  31 */   public static String DEFAULT_METHODNAME = "service";
/*     */ 
/*  33 */   protected Map methodCache = Collections.synchronizedMap(new HashMap());
/*     */ 
/*  35 */   protected Class[] types1 = { 
/*  36 */     HttpServletRequest.class, 
/*  37 */     HttpServletResponse.class };
/*     */ 
/*  39 */   protected Class[] types2 = new Class[0];
/*     */ 
/*  41 */   private static Map classCache = Collections.synchronizedMap(new HashMap());
/*     */   private String actionBasePath;
/*     */   private String viewBasePath;
/*     */   private String dispatchMethod;
/*     */ 
/*     */   public void init(FilterConfig filterConfig)
/*     */     throws ServletException
/*     */   {
/*  48 */     this.filterConfig = filterConfig;
/*  49 */     this.servletContext = filterConfig.getServletContext();
/*  50 */     this.webRootRealPath = this.servletContext.getRealPath("/");
/*     */ 
/*  52 */     this.actionBasePath = filterConfig.getInitParameter("actionBasePath");
/*  53 */     if (this.actionBasePath == null) {
/*  54 */       this.actionBasePath = "action";
/*     */     }
/*     */ 
/*  57 */     this.viewBasePath = filterConfig.getInitParameter("viewBasePath");
/*  58 */     if (this.viewBasePath == null) {
/*  59 */       this.viewBasePath = "/view";
/*     */     }
/*  61 */     this.dispatchMethod = filterConfig.getInitParameter("dispatchMethod");
/*  62 */     if (this.dispatchMethod == null) {
/*  63 */       this.dispatchMethod = "doMethod";
/*     */     }
/*     */ 
/*  66 */     DataAccessManagerLoader.loadDataAccessManager(this.servletContext);
/*     */   }
/*     */ 
/*     */   public BaseAction loadAction(HttpServletRequest request, HttpServletResponse response) {
/*  70 */     String servletPath = request.getServletPath();
/*  71 */     Class klazz = (Class)classCache.get(servletPath);
/*  72 */     String actionName = null;
/*     */     try {
/*  74 */       int si = servletPath.lastIndexOf("/");
/*  75 */       int di = servletPath.lastIndexOf(".");
/*  76 */       servletPath = servletPath.substring(si + 1, di);
/*  77 */       String viewPath = servletPath.replaceAll("\\.", "/");
/*  78 */       if (klazz == null) {
/*  79 */         di = servletPath.lastIndexOf(".") + 1;
/*  80 */         actionName = servletPath.substring(0, di) + servletPath.substring(di, di + 1).toUpperCase() + servletPath.substring(di + 1);
/*  81 */         klazz = Class.forName(this.actionBasePath + '.' + actionName);
/*  82 */         classCache.put(servletPath, klazz);
/*     */       }
/*     */ 
/*  85 */       BaseAction action = (BaseAction)klazz.newInstance();
/*  86 */       action.init(request, response);
/*  87 */       action.setViewBasePath(this.viewBasePath);
/*  88 */       action.setViewPath("/" + viewPath);
/*  89 */       return action;
/*     */     }
/*     */     catch (Exception e) {
/*  92 */       LogHandler.warn(actionName + " Action not found");
/*  93 */     }return null;
/*     */   }
/*     */ 
/*     */   private String getMethodName(HttpServletRequest request)
/*     */   {
/*  98 */     return request.getParameter(this.dispatchMethod);
/*     */   }
/*     */ 
/*     */   protected Method getMethod(Class clazz, String name, Class[] pTypes) {
/*     */     try {
/* 103 */       return clazz.getMethod(name, pTypes); } catch (NoSuchMethodException e) {
/*     */     }
/* 105 */     return null;
/*     */   }
/*     */ 
/*     */   protected Method getMethod(Class clazz, String name)
/*     */   {
/* 110 */     String key = clazz.getName() + "/" + name;
/* 111 */     Method method = (Method)this.methodCache.get(key);
/* 112 */     if (method == null) {
/* 113 */       Exception mE = null;
/*     */       try {
/* 115 */         method = clazz.getMethod(name, this.types1);
/*     */       } catch (Exception e1) {
/* 117 */         mE = e1;
/*     */         try {
/* 119 */           method = clazz.getMethod(name, this.types2);
/*     */         } catch (Exception e2) {
/* 121 */           mE = e2;
/* 122 */           method = null;
/*     */         }
/*     */       }
/*     */ 
/* 126 */       if (method == null)
/* 127 */         LogHandler.warn(this, new NoSuchMethodException(mE.getMessage()));
/*     */       else {
/* 129 */         this.methodCache.put(key, method);
/*     */       }
/*     */     }
/* 132 */     return method;
/*     */   }
/*     */   public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
/* 135 */     HttpServletRequest request = (HttpServletRequest)servletRequest;
/* 136 */     HttpServletResponse response = (HttpServletResponse)servletResponse;
/*     */ 
/* 145 */     BaseAction action = loadAction(request, response);
/* 146 */     String name = getMethodName(request);
/*     */ 
/* 148 */     if (StringUtils.isEmpty(name)) {
/* 149 */       name = DEFAULT_METHODNAME;
/*     */     }
/* 151 */     if (action != null) {
/* 152 */       Object[] args = { request, response };
/* 153 */       Method method = getMethod(action.getClass(), name);
/*     */       try {
/* 155 */         if (method.getParameterTypes().length == 0)
/* 156 */           method.invoke(action, null);
/*     */         else
/* 158 */           method.invoke(action, args);
/*     */       }
/*     */       catch (Exception e) {
/* 161 */         LogHandler.warn(e);
/* 162 */         throw new ServletException(e);
/*     */       } finally {
/* 164 */         action.dispose();
/*     */       }
/* 166 */       return;
/*     */     }
/* 168 */     response.sendError(404);
/* 169 */     response.setContentType("text/html; charset=UTF-8");
/* 170 */     PrintWriter out = response.getWriter();
/* 171 */     out.println("<html><body>Action not found!</body></html>");
/* 172 */     out.flush();
/* 173 */     out.close();
/*     */   }
/*     */ 
/*     */   public String getWebRootRealPath()
/*     */   {
/* 178 */     return this.webRootRealPath;
/*     */   }
/*     */ 
/*     */   public void destroy() {
/* 182 */     this.webRootRealPath = null;
/* 183 */     this.servletContext = null;
/* 184 */     this.filterConfig = null;
/*     */ 
/* 186 */     this.methodCache = null;
/*     */ 
/* 188 */     this.types1 = null;
/* 189 */     this.types2 = null;
/*     */ 
/* 191 */     classCache = null;
/*     */ 
/* 193 */     this.actionBasePath = null;
/* 194 */     this.viewBasePath = null;
/* 195 */     this.dispatchMethod = null;
/* 196 */     DataAccessManagerLoader.destroyDataAccessManager(this.servletContext);
/*     */   }
/*     */ }

/* Location:           F:\software\sigma_grid_with_jsp_sample_release\something missing\something missing.zip
 * Qualified Name:     com.fins.gt.action.FrontController
 * JD-Core Version:    0.6.0
 */