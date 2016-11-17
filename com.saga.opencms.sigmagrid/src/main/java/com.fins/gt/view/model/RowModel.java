/*    */ package com.fins.gt.view.model;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class RowModel extends BaseModel
/*    */ {
/*  7 */   private List columnModels = new ArrayList();
/*    */   private GridModel gridModel;
/*    */   private ColumnModel currentColModel;
/* 10 */   private int currentColNO = -1;
/*    */ 
/*    */   public String toHTML(Object record, int rowNO) {
/* 13 */     StringBuffer sbf = new StringBuffer();
/* 14 */     sbf.append("<tr>");
/* 15 */     int colNum = this.columnModels.size();
/* 16 */     for (this.currentColNO = 0; this.currentColNO < colNum; this.currentColNO += 1) {
/* 17 */       this.currentColModel = ((ColumnModel)this.columnModels.get(this.currentColNO));
/* 18 */       sbf.append(this.currentColModel.toHTML(record, rowNO, this.currentColNO));
/*    */     }
/* 20 */     sbf.append("</tr>");
/* 21 */     this.currentColModel = null;
/* 22 */     return sbf.toString();
/*    */   }
/*    */ 
/*    */   public void addColumnModel(ColumnModel columnModel) {
/* 26 */     this.columnModels.add(columnModel);
/* 27 */     columnModel.setRowModel(this);
/* 28 */     columnModel.setGridModel(getGridModel());
/*    */   }
/*    */ 
/*    */   public GridModel getGridModel()
/*    */   {
/* 33 */     return this.gridModel;
/*    */   }
/*    */ 
/*    */   public void setGridModel(GridModel gridModel) {
/* 37 */     this.gridModel = gridModel;
/*    */   }
/*    */ 
/*    */   public List getColumnModels() {
/* 41 */     return this.columnModels;
/*    */   }
/*    */ 
/*    */   public void setColumnModels(List columnModels) {
/* 45 */     this.columnModels = columnModels;
/*    */   }
/*    */ 
/*    */   public ColumnModel getCurrentColModel() {
/* 49 */     return this.currentColModel;
/*    */   }
/*    */ 
/*    */   public void setCurrentColModel(ColumnModel currentColModel) {
/* 53 */     this.currentColModel = currentColModel;
/*    */   }
/*    */ 
/*    */   public int getCurrentColNO() {
/* 57 */     return this.currentColNO;
/*    */   }
/*    */ 
/*    */   public void setCurrentColNO(int currentColNO) {
/* 61 */     this.currentColNO = currentColNO;
/*    */   }
/*    */ }

/* Location:           F:\software\sigma_grid_with_jsp_sample_release\something missing\something missing.zip
 * Qualified Name:     com.fins.gt.view.model.RowModel
 * JD-Core Version:    0.6.0
 */