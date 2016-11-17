/*     */ package com.fins.gt.action;
/*     */ 
/*     */ import java.util.Map;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import javax.servlet.http.HttpSession;
/*     */ 
/*     */ public class ServletWrapper
/*     */ {
/*     */   private HttpServletRequest request;
/*     */   private HttpServletResponse response;
/*     */ 
/*     */   public ServletWrapper(HttpServletRequest request, HttpServletResponse response)
/*     */   {
/*  15 */     this.request = request;
/*  16 */     this.response = response;
/*     */   }
/*     */ 
/*     */   public Map getParameterMap()
/*     */   {
/*  21 */     return this.request.getParameterMap();
/*     */   }
/*     */ 
/*     */   public String getParameter(String key)
/*     */   {
/*  26 */     return this.request.getParameter(key);
/*     */   }
/*     */ 
/*     */   public String getParameter(String key, String ifNull) {
/*  30 */     String obj = getParameter(key);
/*  31 */     return obj == null ? ifNull : obj;
/*     */   }
/*     */ 
/*     */   public String[] getParameterValues(String key) {
/*  35 */     return this.request.getParameterValues(key);
/*     */   }
/*     */ 
/*     */   public Integer getIntegerParameter(String key) {
/*     */     try {
/*  40 */       return Integer.valueOf(getParameter(key)); } catch (Exception e) {
/*     */     }
/*  42 */     return null;
/*     */   }
/*     */ 
/*     */   public Integer getIntegerParameter(String key, int ifNull)
/*     */   {
/*  47 */     Integer obj = getIntegerParameter(key);
/*  48 */     return obj == null ? new Integer(ifNull) : obj;
/*     */   }
/*     */ 
/*     */   public Integer[] getIntegerParameterValues(String key) {
/*  52 */     String[] objs = getParameterValues(key);
/*  53 */     if (objs == null) return null;
/*  54 */     Integer[] dObjs = new Integer[objs.length];
/*     */ 
/*  56 */     for (int i = 0; i < objs.length; i++) {
/*     */       try {
/*  58 */         dObjs[i] = Integer.valueOf(objs[i]);
/*     */       } catch (Exception e) {
/*  60 */         dObjs[i] = null;
/*     */       }
/*     */     }
/*  63 */     return dObjs;
/*     */   }
/*     */ 
/*     */   public Double getDoubleParameter(String key) {
/*     */     try {
/*  68 */       return Double.valueOf(getParameter(key)); } catch (Exception e) {
/*     */     }
/*  70 */     return null;
/*     */   }
/*     */ 
/*     */   public Double getDoubleParameter(String key, double ifNull)
/*     */   {
/*  75 */     Double obj = getDoubleParameter(key);
/*  76 */     return obj == null ? new Double(ifNull) : obj;
/*     */   }
/*     */ 
/*     */   public Double[] getDoubleParameterValues(String key) {
/*  80 */     String[] objs = getParameterValues(key);
/*  81 */     if (objs == null) return null;
/*  82 */     Double[] dObjs = new Double[objs.length];
/*     */ 
/*  84 */     for (int i = 0; i < objs.length; i++) {
/*     */       try {
/*  86 */         dObjs[i] = Double.valueOf(objs[i]);
/*     */       } catch (Exception e) {
/*  88 */         dObjs[i] = null;
/*     */       }
/*     */     }
/*  91 */     return dObjs;
/*     */   }
/*     */ 
/*     */   public Boolean getBooleanParameter(String key)
/*     */   {
/*     */     try {
/*  97 */       return Boolean.valueOf(getParameter(key)); } catch (Exception e) {
/*     */     }
/*  99 */     return null;
/*     */   }
/*     */ 
/*     */   public Boolean getBooleanParameter(String key, boolean ifNull)
/*     */   {
/* 104 */     Boolean obj = getBooleanParameter(key);
/* 105 */     return obj == null ? new Boolean(ifNull) : obj;
/*     */   }
/*     */ 
/*     */   public Boolean[] getBooleanParameterValues(String key) {
/* 109 */     String[] objs = getParameterValues(key);
/* 110 */     if (objs == null) return null;
/* 111 */     Boolean[] dObjs = new Boolean[objs.length];
/*     */ 
/* 113 */     for (int i = 0; i < objs.length; i++) {
/*     */       try {
/* 115 */         dObjs[i] = Boolean.valueOf(objs[i]);
/*     */       } catch (Exception e) {
/* 117 */         dObjs[i] = null;
/*     */       }
/*     */     }
/* 120 */     return dObjs;
/*     */   }
/*     */ 
/*     */   public Object getAttribute(String key) {
/* 124 */     return this.request.getAttribute(key);
/*     */   }
/*     */ 
/*     */   public void setAttribute(String key, Object value) {
/* 128 */     this.request.setAttribute(key, value);
/*     */   }
/*     */ 
/*     */   public HttpServletRequest getRequest() {
/* 132 */     return this.request;
/*     */   }
/*     */ 
/*     */   public void setRequest(HttpServletRequest request) {
/* 136 */     this.request = request;
/*     */   }
/*     */ 
/*     */   public HttpServletResponse getResponse() {
/* 140 */     return this.response;
/*     */   }
/*     */ 
/*     */   public void setResponse(HttpServletResponse response) {
/* 144 */     this.response = response;
/*     */   }
/*     */ 
/*     */   public HttpSession getSession() {
/* 148 */     return this.request.getSession();
/*     */   }
/*     */   public HttpSession getSession(boolean create) {
/* 151 */     return this.request.getSession(create);
/*     */   }
/*     */ }

/* Location:           F:\software\sigma_grid_with_jsp_sample_release\something missing\something missing.zip
 * Qualified Name:     com.fins.gt.action.ServletWrapper
 * JD-Core Version:    0.6.0
 */