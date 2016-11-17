/*     */ package com.fins.gt.dataaccess;
/*     */ 
/*     */ import com.fins.gt.dataaccess.helper.DataAccessUtils;
/*     */ import com.fins.gt.util.LogHandler;
/*     */ import com.fins.gt.util.StringUtils;
/*     */ import java.sql.Connection;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class BaseDAO
/*     */ {
/*     */   public List executeQuery(String sql)
/*     */     throws RuntimeSQLException
/*     */   {
/*  20 */     return executeQuery(sql, null);
/*     */   }
/*     */ 
/*     */   public List executeQuery(String sql, Map paramMap) throws RuntimeSQLException {
/*  24 */     Connection conn = null;
/*  25 */     PreparedStatement pstmt = null;
/*  26 */     ResultSet rest = null;
/*  27 */     List recordList = null;
/*     */     try
/*     */     {
/*  30 */       conn = getConnection();
/*  31 */       pstmt = DataAccessUtils.createPreparedStatement(conn, sql, paramMap);
/*  32 */       if (pstmt != null) {
/*  33 */         rest = pstmt.executeQuery();
/*  34 */         String[] columnName = DataAccessUtils.getColumnName(rest);
/*  35 */         recordList = new ArrayList();
/*  36 */         Map record = null;
/*  37 */         while (rest.next()) {
/*  38 */           record = new HashMap();
/*  39 */           DataAccessUtils.buildRecord(rest, columnName, record);
/*  40 */           recordList.add(record);
/*     */         }
/*     */       }
/*     */     } catch (SQLException e) {
/*  44 */       recordList = null;
/*  45 */       LogHandler.error(this, e);
/*  46 */       throw new RuntimeSQLException(e);
/*     */     } finally {
/*  48 */       close(rest, pstmt, conn);
/*     */     }
/*     */ 
/*  51 */     return recordList;
/*     */   }
/*     */ 
/*     */   public int executeUpdate(String sql) throws RuntimeSQLException {
/*  55 */     return executeUpdate(sql, null);
/*     */   }
/*     */ 
/*     */   public int[] executeBatchUpdate(String sql, List paramMapList) throws RuntimeSQLException {
/*  59 */     Map[] paramMaps = new Map[paramMapList.size()];
/*  60 */     return executeBatchUpdate(sql, (Map[])paramMapList.toArray(paramMaps));
/*     */   }
/*     */ 
/*     */   public int[] executeBatchUpdate(String sql, Map[] paramMaps) throws RuntimeSQLException {
/*  64 */     int[] opresults = (int[])null;
/*     */ 
/*  66 */     Connection conn = null;
/*  67 */     PreparedStatement pstmt = null;
/*     */     try {
/*  69 */       conn = getConnection();
/*  70 */       pstmt = DataAccessUtils.createPreparedStatement(conn, sql, paramMaps);
/*  71 */       if (pstmt != null)
/*  72 */         opresults = pstmt.executeBatch();
/*     */     } catch (SQLException e) {
/*  74 */       opresults = (int[])null;
/*  75 */       LogHandler.error(this, e);
/*  76 */       throw new RuntimeSQLException(e);
/*     */     } finally {
/*  78 */       close(pstmt, conn);
/*     */     }
/*  80 */     return opresults;
/*     */   }
/*     */ 
/*     */   public int executeUpdate(String sql, Map paramMap) throws RuntimeSQLException {
/*  84 */     int opresult = -1;
/*     */ 
/*  86 */     Connection conn = null;
/*  87 */     PreparedStatement pstmt = null;
/*     */     try {
/*  89 */       conn = getConnection();
/*  90 */       pstmt = DataAccessUtils.createPreparedStatement(conn, sql, paramMap);
/*  91 */       if (pstmt != null)
/*  92 */         opresult = pstmt.executeUpdate();
/*     */     } catch (SQLException e) {
/*  94 */       opresult = -1;
/*  95 */       LogHandler.error(this, e);
/*  96 */       throw new RuntimeSQLException(e);
/*     */     } finally {
/*  98 */       close(pstmt, conn);
/*     */     }
/*     */ 
/* 101 */     return opresult;
/*     */   }
/*     */ 
/*     */   public Integer countTable(String tableName) throws RuntimeSQLException {
/* 105 */     return countTable(tableName, null);
/*     */   }
/*     */ 
/*     */   public Integer countTable(String tableName, String whereSql) throws RuntimeSQLException {
/* 109 */     whereSql = StringUtils.isNotEmpty(whereSql) ? " where " + whereSql : "";
/* 110 */     String sql = "select count(*) as totalrownum from " + tableName + whereSql;
/* 111 */     return countQuery(sql, "totalrownum");
/*     */   }
/*     */ 
/*     */   public Integer countQuery(String sql, String fieldName) {
/* 115 */     return countQuery(sql, fieldName, null);
/*     */   }
/*     */ 
/*     */   public Integer countQuery(String sql, String fieldName, Map paramMap) {
/* 119 */     List list = executeQuery(sql);
/* 120 */     if ((list != null) && (list.size() > 0)) {
/* 121 */       String t = String.valueOf(((Map)list.get(0)).get(fieldName.toLowerCase()));
/*     */       try {
/* 123 */         return new Integer(t);
/*     */       } catch (Exception e) {
/* 125 */         LogHandler.error(this, e);
/*     */       }
/*     */     }
/* 128 */     return new Integer(0);
/*     */   }
/*     */ 
/*     */   public static Connection getConnection()
/*     */   {
/* 134 */     return DataAccessManager.getConnection();
/*     */   }
/*     */ 
/*     */   public void close(Connection conn) {
/* 138 */     DataAccessManager.closeConnection(conn);
/*     */   }
/*     */ 
/*     */   public void close(ResultSet rest, Statement pstmt, Connection conn) {
/*     */     try {
/* 143 */       close(rest);
/*     */     } catch (SQLException e) {
/* 145 */       LogHandler.error(this, e);
/*     */     }
/* 147 */     close(pstmt, conn);
/*     */   }
/*     */ 
/*     */   public void close(Statement pstmt, Connection conn) {
/*     */     try {
/* 152 */       close(pstmt);
/*     */     } catch (SQLException e) {
/* 154 */       LogHandler.error(this, e);
/*     */     }
/* 156 */     close(conn);
/*     */   }
/*     */ 
/*     */   public void close(ResultSet rest) throws SQLException {
/* 160 */     if (rest != null)
/* 161 */       rest.close();
/*     */   }
/*     */ 
/*     */   public void close(Statement pstmt) throws SQLException {
/* 165 */     if (pstmt != null)
/* 166 */       pstmt.close();
/*     */   }
/*     */ }

/* Location:           F:\software\sigma_grid_with_jsp_sample_release\something missing\something missing.zip
 * Qualified Name:     com.fins.gt.dataaccess.BaseDAO
 * JD-Core Version:    0.6.0
 */