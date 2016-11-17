/*    */ package com.fins.gt.view.model;
/*    */ 
/*    */ public class ColumnModel extends BaseModel
/*    */ {
/*    */   private GridModel gridModel;
/*    */   private RowModel rowModel;
/*    */ 
/*    */   public String getCellHTML()
/*    */   {
/*  8 */     return "";
/*    */   }
/*    */   public String toHTML(Object record, int rowNO, int colNO) {
/* 11 */     StringBuffer sbf = new StringBuffer();
/* 12 */     sbf.append("<td>");
/* 13 */     sbf.append("<div>");
/* 14 */     sbf.append(getCellHTML());
/* 15 */     sbf.append("</div>");
/* 16 */     sbf.append("</td>");
/* 17 */     return sbf.toString();
/*    */   }
/*    */ 
/*    */   public GridModel getGridModel() {
/* 21 */     return this.gridModel;
/*    */   }
/*    */   public void setGridModel(GridModel gridModel) {
/* 24 */     this.gridModel = gridModel;
/*    */   }
/*    */   public RowModel getRowModel() {
/* 27 */     return this.rowModel;
/*    */   }
/*    */   public void setRowModel(RowModel rowModel) {
/* 30 */     this.rowModel = rowModel;
/*    */   }
/*    */ }

/* Location:           F:\software\sigma_grid_with_jsp_sample_release\something missing\something missing.zip
 * Qualified Name:     com.fins.gt.view.model.ColumnModel
 * JD-Core Version:    0.6.0
 */