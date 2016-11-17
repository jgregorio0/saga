/*    */ package com.fins.gt.dataaccess.helper;
/*    */ 
/*    */ public class SqlParameter
/*    */ {
/*    */   private String name;
/*    */   private Object value;
/*    */   private String type;
/*    */   private int index;
/*    */ 
/*    */   public SqlParameter(String name, Object value)
/*    */   {
/* 10 */     this.name = name;
/* 11 */     this.value = value;
/* 12 */     this.type = "in";
/*    */   }
/*    */   public SqlParameter(String name, Object value, String type) {
/* 15 */     this(name, value);
/* 16 */     this.type = type;
/*    */   }
/*    */   public String getName() {
/* 19 */     return this.name;
/*    */   }
/*    */   public void setName(String name) {
/* 22 */     this.name = name;
/*    */   }
/*    */   public String getType() {
/* 25 */     return this.type;
/*    */   }
/*    */   public void setType(String type) {
/* 28 */     this.type = type;
/*    */   }
/*    */   public Object getValue() {
/* 31 */     return this.value;
/*    */   }
/*    */   public void setValue(Object value) {
/* 34 */     this.value = value;
/*    */   }
/*    */   public String getValueAsString() {
/* 37 */     return String.valueOf(this.value);
/*    */   }
/*    */   public int getIndex() {
/* 40 */     return this.index;
/*    */   }
/*    */   public void setIndex(int index) {
/* 43 */     this.index = index;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 47 */     return this.name + " = " + this.value;
/*    */   }
/*    */ }

/* Location:           F:\software\sigma_grid_with_jsp_sample_release\something missing\something missing.zip
 * Qualified Name:     com.fins.gt.dataaccess.helper.SqlParameter
 * JD-Core Version:    0.6.0
 */