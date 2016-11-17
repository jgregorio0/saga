/*     */ package com.fins.gt.dataaccess.helper;
/*     */ 
/*     */ import java.util.Map;
/*     */ 
/*     */ public class SqlSnippet
/*     */ {
/*     */   public String snippet;
/*     */   public String condition;
/*     */   public String content;
/*     */   public int start;
/*     */   public int end;
/*     */   public String type;
/*     */   public String condition_before;
/*     */   public String condition_c;
/*     */   public String condition_after;
/*     */   public Object value;
/*  23 */   public boolean need = false;
/*     */ 
/*     */   public String toString()
/*     */   {
/*  29 */     StringBuffer bufs = new StringBuffer();
/*  30 */     bufs.append("===== ").append(getClass().getName()).append(" =====\n");
/*  31 */     bufs.append("snippet : ").append(this.snippet).append("\n");
/*  32 */     bufs.append("condition : ").append(this.condition).append("\n");
/*  33 */     bufs.append("condition_ex : ").append(this.condition_before).append(this.condition_c).append(this.condition_after).append("\n");
/*  34 */     bufs.append("content : ").append(this.content).append("\n");
/*  35 */     bufs.append("start : ").append(this.start).append("\n");
/*  36 */     bufs.append("end : ").append(this.end).append("\n");
/*  37 */     return bufs.toString();
/*     */   }
/*     */ 
/*     */   public void init() {
/*  41 */     if (this.condition.indexOf("!=") > 0)
/*  42 */       this.condition_c = "!=";
/*  43 */     else if (this.condition.indexOf("<=") > 0)
/*  44 */       this.condition_c = "<=";
/*  45 */     else if (this.condition.indexOf(">=") > 0)
/*  46 */       this.condition_c = ">=";
/*  47 */     else if (this.condition.indexOf("=") > 0)
/*  48 */       this.condition_c = "=";
/*  49 */     else if (this.condition.indexOf("<") > 0)
/*  50 */       this.condition_c = "<";
/*  51 */     else if (this.condition.indexOf(">") > 0) {
/*  52 */       this.condition_c = ">";
/*     */     }
/*     */ 
/*  55 */     String[] ba = this.condition.split(this.condition_c);
/*  56 */     this.condition_before = ba[0];
/*  57 */     this.condition_after = ba[1];
/*     */   }
/*     */ 
/*     */   public boolean check(Map map)
/*     */   {
/*  62 */     String type = "string";
/*  63 */     this.value = map.get(this.condition_before);
/*     */ 
/*  65 */     String beforeS = this.value == null ? null : String.valueOf(this.value);
/*  66 */     String afterS = this.condition_after;
/*     */ 
/*  69 */     long beforeL = -1L;
/*  70 */     long afterL = -1L;
/*     */ 
/*  72 */     if ((this.condition_after.indexOf("'") < 0) && (this.condition_after.indexOf("\"") < 0)) {
/*     */       try {
/*  74 */         afterL = new Long(this.condition_after).longValue();
/*  75 */         type = "number";
/*     */       } catch (Exception e) {
/*  77 */         type = "string";
/*     */       }
/*     */     }
/*  80 */     if ("number".equalsIgnoreCase(type)) {
/*     */       try {
/*  82 */         beforeL = new Long(beforeS).longValue();
/*  83 */         type = "number";
/*     */       } catch (Exception e) {
/*  85 */         type = "string";
/*     */       }
/*     */     }
/*     */ 
/*  89 */     boolean need = false;
/*  90 */     this.type = type;
/*     */ 
/*  93 */     if ("number".equalsIgnoreCase(type)) {
/*  94 */       if (this.condition.indexOf("!=") > 0)
/*  95 */         need = DataAccessUtils.isNotEquals(beforeL, afterL);
/*  96 */       else if (this.condition.indexOf("<=") > 0)
/*  97 */         need = DataAccessUtils.isLessThenE(beforeL, afterL);
/*  98 */       else if (this.condition.indexOf(">=") > 0)
/*  99 */         need = DataAccessUtils.isGreatThenE(beforeL, afterL);
/* 100 */       else if (this.condition.indexOf("=") > 0)
/* 101 */         need = DataAccessUtils.isEquals(beforeL, afterL);
/* 102 */       else if (this.condition.indexOf("<") > 0)
/* 103 */         need = DataAccessUtils.isLessThen(beforeL, afterL);
/* 104 */       else if (this.condition.indexOf(">") > 0) {
/* 105 */         need = DataAccessUtils.isGreatThen(beforeL, afterL);
/*     */       }
/*     */     }
/* 108 */     else if (this.condition.indexOf("!=") > 0) {
/* 109 */       if (afterS.equalsIgnoreCase("EMPTY")) {
/* 110 */         need = DataAccessUtils.isNotEmpty(beforeS);
/*     */       } else {
/* 112 */         if (afterS.equalsIgnoreCase("NULL")) {
/* 113 */           afterS = null;
/*     */         }
/* 115 */         need = DataAccessUtils.isNotEquals(beforeS, afterS);
/*     */       }
/* 117 */     } else if (this.condition.indexOf("<=") > 0)
/* 118 */       need = DataAccessUtils.isLessThenE(beforeS, afterS);
/* 119 */     else if (this.condition.indexOf(">=") > 0)
/* 120 */       need = DataAccessUtils.isGreatThenE(beforeS, afterS);
/* 121 */     else if (this.condition.indexOf("=") > 0) {
/* 122 */       if (afterS.equalsIgnoreCase("EMPTY")) {
/* 123 */         need = DataAccessUtils.isEmpty(beforeS);
/*     */       } else {
/* 125 */         if (afterS.equalsIgnoreCase("NULL")) {
/* 126 */           afterS = null;
/*     */         }
/* 128 */         need = DataAccessUtils.isEquals(beforeS, afterS);
/*     */       }
/* 130 */     } else if (this.condition.indexOf("<") > 0)
/* 131 */       need = DataAccessUtils.isLessThen(beforeS, afterS);
/* 132 */     else if (this.condition.indexOf(">") > 0) {
/* 133 */       need = DataAccessUtils.isGreatThen(beforeS, afterS);
/*     */     }
/*     */ 
/* 136 */     this.need = need;
/* 137 */     return need;
/*     */   }
/*     */ }

/* Location:           F:\software\sigma_grid_with_jsp_sample_release\something missing\something missing.zip
 * Qualified Name:     com.fins.gt.dataaccess.helper.SqlSnippet
 * JD-Core Version:    0.6.0
 */