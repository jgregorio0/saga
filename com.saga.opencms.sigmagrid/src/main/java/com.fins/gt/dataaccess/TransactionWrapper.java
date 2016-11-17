/*    */ package com.fins.gt.dataaccess;
/*    */ 
/*    */ import com.fins.gt.util.LogHandler;
/*    */ 
/*    */ public abstract class TransactionWrapper
/*    */ {
/*  6 */   private boolean success = false;
/*    */ 
/*    */   public boolean execute()
/*    */   {
/* 12 */     this.success = true;
/* 13 */     beforeStart();
/* 14 */     DataAccessManager.txStart();
/*    */     try {
/* 16 */       transaction();
/*    */     } catch (Exception e) {
/* 18 */       this.success = false;
/* 19 */       LogHandler.error(this, e);
/* 20 */       DataAccessManager.txRollback();
/* 21 */       afterRollback(e);
/*    */     }
/* 23 */     if (this.success) {
/* 24 */       DataAccessManager.txEnd();
/* 25 */       afterEnd();
/*    */     }
/* 27 */     return this.success;
/*    */   }
/*    */   public abstract void transaction() throws Exception;
/*    */ 
/*    */   public void beforeStart() {
/*    */   }
/*    */   public void afterRollback(Exception e) {  }
/*    */ 
/*    */   public void afterEnd() {  }
/*    */ 
/* 36 */   public boolean isSuccess() { return this.success;
/*    */   }
/*    */ }

/* Location:           F:\software\sigma_grid_with_jsp_sample_release\something missing\something missing.zip
 * Qualified Name:     com.fins.gt.dataaccess.TransactionWrapper
 * JD-Core Version:    0.6.0
 */