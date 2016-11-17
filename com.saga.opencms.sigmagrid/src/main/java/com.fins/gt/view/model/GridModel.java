/*    */ package com.fins.gt.view.model;
/*    */ 
/*    */ import java.util.List;
/*    */ 
/*    */ public class GridModel extends BaseModel
/*    */ {
/*    */   private RowModel rowModel;
/*  7 */   private int currentRowNO = -1;
/*    */ 
/*    */   public RowModel getRowModel() {
/* 10 */     return this.rowModel;
/*    */   }
/*    */ 
/*    */   public void setRowModel(RowModel rowModel) {
/* 14 */     this.rowModel = rowModel;
/* 15 */     this.rowModel.setGridModel(this);
/*    */   }
/*    */ 
/*    */   public int getCurrentRowNO() {
/* 19 */     return this.currentRowNO;
/*    */   }
/*    */ 
/*    */   public void setCurrentRowNO(int currentRowNO) {
/* 23 */     this.currentRowNO = currentRowNO;
/*    */   }
/*    */ 
/*    */   public String toHTML(List dataList) {
/* 27 */     StringBuffer sbf = new StringBuffer();
/* 28 */     sbf.append("<table>");
/* 29 */     int rowNum = dataList.size();
/* 30 */     for (this.currentRowNO = 0; this.currentRowNO < rowNum; this.currentRowNO += 1) {
/* 31 */       Object record = dataList.get(this.currentRowNO);
/* 32 */       sbf.append(this.rowModel.toHTML(record, this.currentRowNO));
/*    */     }
/* 34 */     sbf.append("</table>");
/* 35 */     this.currentRowNO = -1;
/* 36 */     return sbf.toString();
/*    */   }
/*    */ }

/* Location:           F:\software\sigma_grid_with_jsp_sample_release\something missing\something missing.zip
 * Qualified Name:     com.fins.gt.view.model.GridModel
 * JD-Core Version:    0.6.0
 */