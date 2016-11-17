/*    */ package com.fins.gt.dataaccess;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ 
/*    */ public class RuntimeSQLException extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public RuntimeSQLException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public RuntimeSQLException(SQLException cause)
/*    */   {
/* 12 */     super(cause);
/*    */   }
/*    */   public RuntimeSQLException(String message) {
/* 15 */     super(message);
/*    */   }
/*    */   public RuntimeSQLException(String message, SQLException cause) {
/* 18 */     super(message, cause);
/*    */   }
/*    */ }

/* Location:           F:\software\sigma_grid_with_jsp_sample_release\something missing\something missing.zip
 * Qualified Name:     com.fins.gt.dataaccess.RuntimeSQLException
 * JD-Core Version:    0.6.0
 */