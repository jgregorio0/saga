/*     */ package com.fins.gt.dataaccess;
/*     */ 
/*     */ import com.fins.gt.util.LogHandler;
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ 
/*     */ public class DataAccessManager
/*     */ {
/*     */   private static IDataBaseManager currentManager;
/*  12 */   protected static int DEFAULT_MAX_CONN = 10000;
/*     */ 
/*  48 */   private static final ThreadLocal txConnection = new ThreadLocal();
/*     */ 
/*     */   public static boolean initDataSource(String url, String username, String password)
/*     */   {
/*  16 */     if (currentManager != null) {
/*  17 */       currentManager.setURL(url);
/*  18 */       currentManager.setUser(username);
/*  19 */       currentManager.setPassword(password);
/*     */     }
/*  21 */     return currentManager != null;
/*     */   }
/*     */ 
/*     */   public static boolean initConnectionPool() {
/*  25 */     return initConnectionPool(DEFAULT_MAX_CONN);
/*     */   }
/*     */ 
/*     */   public static boolean initConnectionPool(int maxConn) {
/*  29 */     if (currentManager != null) {
/*  30 */       currentManager.setMaxConnections(maxConn);
/*  31 */       return true;
/*     */     }
/*  33 */     return false;
/*     */   }
/*     */ 
/*     */   public static boolean testDataSource() {
/*  37 */     Connection conn = null;
/*     */     try {
/*  39 */       conn = getConnection();
/*  40 */       boolean i = conn != null;
/*     */       return i;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */       return false;
/*     */     } finally {
/*  44 */       closeConnection(conn);
/*  45 */     }
/*     */   }
/*     */ 
/*     */   public static Connection setTxConnection(Connection connection)
/*     */   {
/*  50 */     Connection prevConnection = (Connection)txConnection.get();
/*  51 */     txConnection.set(connection);
/*  52 */     return prevConnection;
/*     */   }
/*     */ 
/*     */   public static boolean txStart() {
/*  56 */     Connection conn = getConnection();
/*  57 */     setTxConnection(conn);
/*  58 */     if (conn != null) {
/*     */       try {
/*  60 */         conn.setAutoCommit(false);
/*  61 */         return true;
/*     */       } catch (SQLException e) {
/*  63 */         LogHandler.error(DataAccessManager.class, e);
/*     */       }
/*     */     }
/*  66 */     return false;
/*     */   }
/*     */   public static boolean txEnd() {
/*  69 */     return txEnd(true);
/*     */   }
/*     */   public static boolean txRollback() {
/*  72 */     LogHandler.info("Transaction Rollback");
/*  73 */     return txEnd(false);
/*     */   }
/*     */   public static boolean txEnd(boolean commit) {
/*  76 */     Connection conn = getConnection();
/*  77 */     setTxConnection(null);
/*  78 */     if (conn != null) {
/*     */       try {
/*  80 */         if (commit)
/*  81 */           conn.commit();
/*     */         else {
/*  83 */           conn.rollback();
/*     */         }
/*  85 */         conn.setAutoCommit(true);
/*     */         return true;
/*     */       } catch (SQLException e) {
/*  88 */         LogHandler.error(DataAccessManager.class, e);
/*     */       } finally {
/*  90 */         closeConnection(conn);
/*     */       }
/*     */     }
/*  93 */     return false;
/*     */   }
/*     */ 
/*     */   public static Connection getConnection()
/*     */   {
/*  98 */     Connection conn = (Connection)txConnection.get();
/*  99 */     if ((conn == null) && (currentManager != null)) {
/* 100 */       conn = currentManager.getConnection();
/*     */       try {
/* 102 */         conn.setAutoCommit(true); } catch (Exception localException) {
/*     */       }
/*     */     }
/* 105 */     return conn;
/*     */   }
/*     */ 
/*     */   public static boolean isAutoCommit(Connection conn)
/*     */   {
/*     */     try {
/* 111 */       return conn.getAutoCommit(); } catch (Exception localException) {
/*     */     }
/* 113 */     return true;
/*     */   }
/*     */ 
/*     */   public static void closeConnection(Connection conn) {
/* 117 */     if ((conn != null) && (isAutoCommit(conn)))
/*     */       try {
/* 119 */         if (conn != null)
/* 120 */           conn.close();
/*     */       } catch (SQLException e) {
/* 122 */         LogHandler.error(DataAccessManager.class, e);
/*     */       }
/*     */   }
/*     */ 
/*     */   public static void destroy()
/*     */   {
/* 128 */     if (currentManager != null)
/* 129 */       currentManager.dispose();
/* 130 */     currentManager = null;
/*     */   }
/*     */ 
/*     */   public static IDataBaseManager getCurrentManager() {
/* 134 */     return currentManager;
/*     */   }
/*     */ 
/*     */   public static void setCurrentManager(IDataBaseManager currentManager) {
/* 138 */     currentManager = currentManager;
/*     */   }
/*     */ }

/* Location:           F:\software\sigma_grid_with_jsp_sample_release\something missing\something missing.zip
 * Qualified Name:     com.fins.gt.dataaccess.DataAccessManager
 * JD-Core Version:    0.6.0
 */