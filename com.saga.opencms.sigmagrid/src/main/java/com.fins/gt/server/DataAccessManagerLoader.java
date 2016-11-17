/*    */ package com.fins.gt.server;
/*    */ 
/*    */ import com.fins.gt.dataaccess.DataAccessManager;
/*    */ import com.fins.gt.dataaccess.IDataBaseManager;
/*    */ import com.fins.gt.util.LogHandler;
/*    */ import com.fins.gt.util.StringUtils;
/*    */ import javax.servlet.ServletContext;
/*    */ 
/*    */ public class DataAccessManagerLoader
/*    */ {
/*    */   public static void loadDataAccessManager(ServletContext servletContext)
/*    */   {
/* 15 */     String manager = getParameter(servletContext, "db.manager", "com.fins.gt.dataaccess.H2DBManager");
/* 16 */     if (StringUtils.isNotEmpty(manager)) {
/*    */       try {
/* 18 */         DataAccessManager.setCurrentManager((IDataBaseManager)Class.forName(manager).newInstance());
/*    */       } catch (Exception e) {
/* 20 */         LogHandler.warn(" invaild db.manager [" + manager + "]");
/*    */       }
/*    */     }
/* 23 */     String user = getParameter(servletContext, "db.user", "sa");
/* 24 */     String password = getParameter(servletContext, "db.password", "");
/* 25 */     String url = getParameter(servletContext, "db.url", "");
/*    */ 
/* 27 */     String maxconn = getParameter(servletContext, "db.maxconn", "none");
/*    */ 
/* 29 */     if (!DataAccessManager.initDataSource(url, user, password)) {
/* 30 */       LogHandler.error(" invaild [" + url + "," + user + "," + password + "]");
/* 31 */       return;
/*    */     }
/*    */     try {
/* 34 */       int maxconnI = Integer.parseInt(maxconn);
/* 35 */       DataAccessManager.initConnectionPool(maxconnI);
/*    */     } catch (NumberFormatException e) {
/* 37 */       LogHandler.warn(" invaild db.maxconn [" + maxconn + "]");
/* 38 */       DataAccessManager.initConnectionPool();
/*    */     }
/*    */ 
/* 41 */     if (!DataAccessManager.testDataSource()) {
/* 42 */       LogHandler.error(" invaild [" + url + "," + user + "," + password + "]");
/* 43 */       return;
/*    */     }
/* 45 */     LogHandler.info(" ======= server started ======= ");
/*    */   }
/*    */ 
/*    */   private static String getParameter(ServletContext servletContext, String key, String defaultValue) {
/* 49 */     String value = servletContext.getInitParameter(key);
/* 50 */     return value == null ? defaultValue : value;
/*    */   }
/*    */ 
/*    */   public static void destroyDataAccessManager(ServletContext servletContext)
/*    */   {
/*    */     try {
/* 56 */       DataAccessManager.destroy();
/*    */     } catch (Exception e) {
/* 58 */       LogHandler.error(DataAccessManagerLoader.class, e);
/*    */     }
/* 60 */     LogHandler.info(" ======= server stoped ======= ");
/*    */   }
/*    */ }

/* Location:           F:\software\sigma_grid_with_jsp_sample_release\something missing\something missing.zip
 * Qualified Name:     com.fins.gt.server.DataAccessManagerLoader
 * JD-Core Version:    0.6.0
 */