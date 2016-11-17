/*    */ package com.fins.gt.export;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public abstract class AbstractXlsWriter
/*    */ {
/*    */   private OutputStream out;
/*  9 */   private String encoding = null;
/*    */ 
/*    */   public abstract void init();
/*    */ 
/*    */   public abstract void start();
/*    */ 
/*    */   public abstract void end();
/*    */ 
/*    */   public abstract void addRow(Object[] paramArrayOfObject);
/*    */ 
/*    */   public void close() { try { getOut().close();
/*    */     } catch (IOException localIOException)
/*    */     {
/*    */     } }
/*    */ 
/*    */   public OutputStream getOut() {
/* 26 */     return this.out;
/*    */   }
/*    */   public void setOut(OutputStream out) {
/* 29 */     this.out = out;
/*    */   }
/*    */   public String getEncoding() {
/* 32 */     return this.encoding;
/*    */   }
/*    */   public void setEncoding(String encoding) {
/* 35 */     this.encoding = encoding;
/*    */   }
/*    */ }

/* Location:           F:\software\sigma_grid_with_jsp_sample_release\something missing\something missing.zip
 * Qualified Name:     com.fins.gt.export.AbstractXlsWriter
 * JD-Core Version:    0.6.0
 */