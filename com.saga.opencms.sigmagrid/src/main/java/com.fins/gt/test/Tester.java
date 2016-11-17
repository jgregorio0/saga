/*    */ package com.fins.gt.test;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.util.Collections;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class Tester
/*    */ {
/*    */   public int value;
/* 12 */   public static Map classCache = Collections.synchronizedMap(new HashMap());
/*    */ 
/*    */   public Tester()
/*    */   {
/* 10 */     this.value = 0;
/*    */   }
/*    */ 
/*    */   public static Object loadAction(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
/* 14 */     Class clazz = (Class)classCache.get(className);
/* 15 */     if (clazz == null) {
/* 16 */       clazz = Class.forName(className);
/* 17 */       classCache.put(className, clazz);
/*    */     }
/* 19 */     return clazz.newInstance();
/*    */   }
/*    */ 
/*    */   public static void main(String[] args)
/*    */     throws InstantiationException, IllegalAccessException, ClassNotFoundException
/*    */   {
/* 27 */     String className = "Tester";
/*    */ 
/* 29 */     long b1 = System.currentTimeMillis();
/* 30 */     for (int i = 0; i < 10000; i++) {
/* 31 */       loadAction(className);
/*    */     }
/*    */ 
/* 34 */     long e1 = System.currentTimeMillis();
/* 35 */     System.out.println("Class.forName " + (e1 - b1));
/*    */ 
/* 37 */     System.gc();
/*    */ 
/* 39 */     long b2 = System.currentTimeMillis();
/* 40 */     for (int i = 0; i < 10000; i++) {
/* 41 */       new Tester();
/*    */     }
/* 43 */     long e2 = System.currentTimeMillis();
/* 44 */     System.out.println("new Tester() " + (e2 - b2));
/*    */ 
/* 46 */     System.out.println(e1 - b1 - (e2 - b2));
/*    */   }
/*    */ }

/* Location:           F:\software\sigma_grid_with_jsp_sample_release\something missing\something missing.zip
 * Qualified Name:     com.fins.gt.test.Tester
 * JD-Core Version:    0.6.0
 */