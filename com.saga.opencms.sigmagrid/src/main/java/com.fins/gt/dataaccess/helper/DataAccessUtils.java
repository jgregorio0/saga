/*     */ package com.fins.gt.dataaccess.helper;
/*     */ 
/*     */ import com.fins.gt.util.CacheUtils;
/*     */ import com.fins.gt.util.LogHandler;
/*     */ import com.fins.gt.util.StringUtils;
/*     */ import java.sql.Connection;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.ResultSetMetaData;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ public class DataAccessUtils
/*     */ {
/*  24 */   private static Pattern pattern = Pattern.compile("#\\{([^_](\\w|:)*[^_\\}])\\}*");
/*  25 */   private static Map sqlSnippetsCache = Collections.synchronizedMap(new HashMap());
/*     */ 
/*     */   public static String[] getColumnName(ResultSet resultSet) throws SQLException
/*     */   {
/*  29 */     ResultSetMetaData metaData = resultSet.getMetaData();
/*  30 */     int cols = metaData.getColumnCount();
/*  31 */     String[] columnName = new String[cols];
/*  32 */     for (int i = 0; i < columnName.length; i++) {
/*  33 */       columnName[i] = metaData.getColumnName(i + 1).toLowerCase();
/*     */     }
/*  35 */     return columnName;
/*     */   }
/*     */ 
/*     */   public static int[] getColumnType(ResultSet resultSet) throws SQLException {
/*  39 */     ResultSetMetaData metaData = resultSet.getMetaData();
/*  40 */     int cols = metaData.getColumnCount();
/*  41 */     int[] columnType = new int[cols];
/*  42 */     for (int i = 0; i < columnType.length; i++) {
/*  43 */       columnType[i] = metaData.getColumnType(i + 1);
/*     */     }
/*  45 */     return columnType;
/*     */   }
/*     */ 
/*     */   public static void buildRecord(ResultSet resultSet, String[] columnName, Map map) throws SQLException
/*     */   {
/*  50 */     for (int i = 0; i < columnName.length; i++)
/*  51 */       map.put(columnName[i], resultSet.getString(columnName[i]));
/*     */   }
/*     */ 
/*     */   public static String parseEasySql(String easySql)
/*     */   {
/*  56 */     String newSql = StringUtils.trim(easySql);
/*  57 */     newSql = newSql.replaceAll("!=", "<>");
/*  58 */     return newSql;
/*     */   }
/*     */ 
/*     */   public static String escRex(String in) {
/*  62 */     return in.replaceAll("\\{", "\\\\{").replaceAll("\\}", "\\\\}");
/*     */   }
/*     */ 
/*     */   public static List createSqlSnippets(String inSql)
/*     */   {
/*  67 */     if ((inSql == null) || (inSql.trim().length() < 1)) {
/*  68 */       return null;
/*     */     }
/*  70 */     List sqlSnippetList = (List)CacheUtils.getFromCache(sqlSnippetsCache, inSql);
/*  71 */     if (sqlSnippetList != null) {
/*  72 */       return sqlSnippetList;
/*     */     }
/*  74 */     sqlSnippetList = new ArrayList();
/*  75 */     CacheUtils.addToCache(sqlSnippetsCache, inSql, sqlSnippetList);
/*     */ 
/*  77 */     Pattern pattern = Pattern.compile("#\\{IF:([^\\}]+)\\}*");
/*  78 */     Matcher matcher = pattern.matcher(inSql);
/*  79 */     while (matcher.find()) {
/*  80 */       SqlSnippet sqlSnippet = new SqlSnippet();
/*  81 */       sqlSnippet.start = matcher.start(0);
/*  82 */       sqlSnippet.end = (inSql.indexOf("#{/IF}", sqlSnippet.start) + "#{/IF}".length());
/*  83 */       sqlSnippet.condition = matcher.group(1).trim();
/*  84 */       sqlSnippet.snippet = inSql.substring(sqlSnippet.start, sqlSnippet.end).trim();
/*  85 */       sqlSnippet.content = sqlSnippet.snippet.substring(matcher.group(0).length(), sqlSnippet.snippet.length() - "#{/IF}".length()).trim();
/*  86 */       sqlSnippet.init();
/*  87 */       sqlSnippetList.add(sqlSnippet);
/*     */     }
/*  89 */     return sqlSnippetList;
/*     */   }
/*     */ 
/*     */   public static PreparedStatement createPreparedStatement(Connection conn, String inSql, Map paramMap) throws SQLException {
/*  93 */     return createPreparedStatement(conn, inSql, new Map[] { paramMap }, false);
/*     */   }
/*     */   public static PreparedStatement createPreparedStatement(Connection conn, String inSql, Map[] paramMaps) throws SQLException {
/*  96 */     return createPreparedStatement(conn, inSql, paramMaps, true);
/*     */   }
/*     */ 
/*     */   public static PreparedStatement createPreparedStatement(Connection conn, String inSql, Map[] paramMaps, boolean batch) throws SQLException {
/* 100 */     PreparedStatement pstmt = null;
/*     */     try {
/* 102 */       for (int i = 0; i < paramMaps.length; i++) {
/* 103 */         Object[] sqlInfo = getDBSqlInfo(inSql, paramMaps[i]);
/* 104 */         String nsql = (String)sqlInfo[0];
/* 105 */         List paraList = (List)sqlInfo[1];
/* 106 */         if (pstmt == null) {
/* 107 */           pstmt = conn.prepareStatement(nsql);
/*     */         }
/* 109 */         for (int j = 0; j < paraList.size(); j++) {
/* 110 */           SqlParameter sqlParameter = (SqlParameter)paraList.get(j);
/* 111 */           pstmt.setString(sqlParameter.getIndex(), sqlParameter.getValueAsString());
/*     */         }
/* 113 */         if (batch)
/* 114 */           pstmt.addBatch();
/*     */       }
/*     */     }
/*     */     catch (SQLException e) {
/* 118 */       LogHandler.error(DataAccessUtils.class, e);
/* 119 */       if (pstmt != null) pstmt.close();
/* 120 */       throw e;
/*     */     }
/* 122 */     return pstmt;
/*     */   }
/*     */ 
/*     */   public static Object[] getDBSqlInfo(String inSql, Map paramMap)
/*     */   {
/* 130 */     List sqlSnippetList = createSqlSnippets(inSql);
/* 131 */     List paraList = new ArrayList();
/*     */ 
/* 133 */     StringBuffer sql = new StringBuffer();
/* 134 */     int start = 0;
/* 135 */     int last = 0;
/* 136 */     if (paramMap == null) paramMap = new HashMap();
/* 137 */     int i = 0;
/* 138 */     for (i = 0; i < sqlSnippetList.size(); i++) {
/* 139 */       SqlSnippet sqlSnippet = (SqlSnippet)sqlSnippetList.get(i);
/* 140 */       start = last;
/* 141 */       last = sqlSnippet.start;
/* 142 */       String osql = inSql.substring(start, last);
/* 143 */       sql.append(osql);
/* 144 */       if (sqlSnippet.check(paramMap)) {
/* 145 */         sql.append(sqlSnippet.content);
/*     */       }
/* 147 */       last = sqlSnippet.end;
/*     */     }
/* 149 */     if ((last > 0) && (last < inSql.length())) {
/* 150 */       sql.append(inSql.substring(last));
/*     */     }
/* 152 */     if (i == 0) {
/* 153 */       sql.append(inSql);
/*     */     }
/* 155 */     String outSql = sql.toString();
/* 156 */     Matcher matcher = pattern.matcher(outSql);
/*     */ 
/* 158 */     int index = 0;
/* 159 */     while (matcher.find()) {
/* 160 */       index++;
/* 161 */       String pPara = matcher.group(0);
/* 162 */       String pName = matcher.group(1);
/* 163 */       String pType = "in";
/* 164 */       if (pName.startsWith("OUT:")) {
/* 165 */         pName = pName.substring(4);
/* 166 */         pType = "out";
/*     */       }
/*     */ 
/* 169 */       String pValue = String.valueOf(paramMap.get(pName));
/* 170 */       SqlParameter sqlPara = new SqlParameter(pName, pValue, pType);
/*     */ 
/* 172 */       sqlPara.setIndex(index);
/* 173 */       paraList.add(sqlPara);
/* 174 */       outSql = StringUtils.replace(outSql, pPara, "?");
/*     */     }
/* 176 */     LogHandler.debug(outSql + "  \t  " + paraList);
/* 177 */     return new Object[] { outSql, paraList };
/*     */   }
/*     */ 
/*     */   public static boolean isEquals(long a, long b)
/*     */   {
/* 183 */     return a == b;
/*     */   }
/*     */ 
/*     */   public static boolean isNotEquals(long a, long b) {
/* 187 */     return a != b;
/*     */   }
/*     */ 
/*     */   public static boolean isLessThen(long a, long b) {
/* 191 */     return a < b;
/*     */   }
/*     */ 
/*     */   public static boolean isGreatThen(long a, long b) {
/* 195 */     return a > b;
/*     */   }
/*     */ 
/*     */   public static boolean isLessThenE(long a, long b) {
/* 199 */     return a <= b;
/*     */   }
/*     */ 
/*     */   public static boolean isGreatThenE(long a, long b) {
/* 203 */     return a >= b;
/*     */   }
/*     */ 
/*     */   public static boolean isEmpty(String a) {
/* 207 */     return (a == null) || (a.length() < 1);
/*     */   }
/*     */   public static boolean isNotEmpty(String a) {
/* 210 */     return !isEmpty(a);
/*     */   }
/*     */ 
/*     */   public static boolean isEquals(String a, String b) {
/* 214 */     if (b == null) {
/* 215 */       return a == null;
/*     */     }
/* 217 */     return b.equals(a);
/*     */   }
/*     */ 
/*     */   public static boolean isNotEquals(String a, String b) {
/* 221 */     if (b == null) {
/* 222 */       return a != null;
/*     */     }
/* 224 */     return !b.equals(a);
/*     */   }
/*     */ 
/*     */   public static boolean isLessThen(String a, String b) {
/* 228 */     return (a != null) && (a.compareTo(b) < 0);
/*     */   }
/*     */ 
/*     */   public static boolean isGreatThen(String a, String b) {
/* 232 */     return (a != null) && (a.compareTo(b) > 0);
/*     */   }
/*     */ 
/*     */   public static boolean isLessThenE(String a, String b) {
/* 236 */     return (a != null) && ((a.compareTo(b) < 0) || (a.equals(b)));
/*     */   }
/*     */ 
/*     */   public static boolean isGreatThenE(String a, String b) {
/* 240 */     return (a != null) && ((a.compareTo(b) > 0) || (a.equals(b)));
/*     */   }
/*     */ }

/* Location:           F:\software\sigma_grid_with_jsp_sample_release\something missing\something missing.zip
 * Qualified Name:     com.fins.gt.dataaccess.helper.DataAccessUtils
 * JD-Core Version:    0.6.0
 */