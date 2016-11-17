/*     */ package com.fins.gt.export;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ 
/*     */ public class SimpleXlsWriter extends AbstractXlsWriter
/*     */ {
/*  11 */   private short row = 0;
/*     */   public static final int BYTE_LEN = 1;
/*     */   public static final int SHORT_LEN = 2;
/*     */   public static final int INT_LEN = 4;
/*     */   public static final int FLOAT_LEN = 4;
/*     */   public static final int LONG_LEN = 8;
/*     */   public static final int DOUBLE_LEN = 8;
/*     */ 
/*     */   public void init()
/*     */   {
/*  15 */     this.row = 0;
/*     */   }
/*     */ 
/*     */   public void start() {
/*  19 */     pack2Stream(new short[] { 2057, 8, 0, 16 });
/*     */   }
/*     */ 
/*     */   public void end() {
/*  23 */     pack2Stream(new short[] { 10 });
/*     */   }
/*     */ 
/*     */   public void addRow(Object[] record) {
/*  27 */     for (short i = 0; i < record.length; i = (short)(i + 1)) {
/*  28 */       writeCell(this.row, i, record[i]);
/*     */     }
/*  30 */     this.row = (short)(this.row + 1);
/*     */   }
/*     */ 
/*     */   public void writeNumCell(short row, short col, double value)
/*     */   {
/*  35 */     pack2Stream(new short[] { 515, 14 });
/*  36 */     pack2Stream(new short[] { row, col });
/*  37 */     pack2Stream(new short[1]);
/*  38 */     pack2Stream(value);
/*     */   }
/*     */ 
/*     */   public void writeStringCell(short row, short col, String value)
/*     */   {
/*  43 */     writeStringCell(row, col, parseString(value));
/*     */   }
/*     */ 
/*     */   public void writeStringCell(short row, short col, byte[] strBytes) {
/*  47 */     int len = strBytes.length;
/*  48 */     pack2Stream(new short[] { 516, (short)(8 + len) });
/*  49 */     pack2Stream(new short[] { row, col });
/*  50 */     pack2Stream(new short[] { 0, (short)len });
/*  51 */     pack2Stream(strBytes);
/*     */   }
/*     */ 
/*     */   public void writeCell(short row, short col, Object value) {
/*  55 */     if (isNumber(value)) {
/*  56 */       double dNum = Double.parseDouble(String.valueOf(value));
/*  57 */       writeNumCell(row, col, dNum);
/*     */     } else {
/*  59 */       writeStringCell(row, col, String.valueOf(value));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeCell(short col, Object value) {
/*  64 */     writeCell(this.row, col, String.valueOf(value));
/*     */   }
/*     */ 
/*     */   public byte[] parseString(String str)
/*     */   {
/*  70 */     if (getEncoding() != null) {
/*     */       try {
/*  72 */         return str.getBytes(getEncoding());
/*     */       } catch (UnsupportedEncodingException e) {
/*  74 */         System.out.println(e.getMessage());
/*     */       }
/*     */     }
/*  77 */     return str.getBytes();
/*     */   }
/*     */ 
/*     */   public void pack2Stream(byte[] bytes) {
/*     */     try {
/*  82 */       getOut().write(bytes);
/*  83 */       getOut().flush();
/*     */     } catch (IOException localIOException) {
/*     */     }
/*     */   }
/*     */ 
/*     */   public void pack2Stream(double doubleNum) {
/*  89 */     pack2Stream(doubleToBytes(doubleNum));
/*     */   }
/*     */ 
/*     */   public void pack2Stream(short shortNum) {
/*  93 */     pack2Stream(shortToBytes(shortNum));
/*     */   }
/*     */ 
/*     */   public void pack2Stream(short[] shorts) {
/*  97 */     for (int i = 0; i < shorts.length; i++)
/*  98 */       pack2Stream(shorts[i]);
/*     */   }
/*     */ 
/*     */   public short getRow() {
/* 102 */     return this.row;
/*     */   }
/*     */ 
/*     */   public static boolean isNumber(Object value)
/*     */   {
/* 114 */     return value instanceof Number;
/*     */   }
/*     */ 
/*     */   public static byte[] longToBytes(long lnum) {
/* 118 */     byte[] bytes = new byte[8];
/* 119 */     int startIndex = 0;
/* 120 */     for (int i = 0; i < 8; i++)
/* 121 */       bytes[(startIndex + i)] = (byte)(int)(lnum >> i * 8 & 0xFF);
/* 122 */     return bytes;
/*     */   }
/*     */   public static byte[] shortToBytes(short num) {
/* 125 */     byte[] bytes = new byte[2];
/* 126 */     int startIndex = 0;
/* 127 */     bytes[startIndex] = (byte)(num & 0xFF);
/* 128 */     bytes[(startIndex + 1)] = (byte)(num >> 8 & 0xFF);
/* 129 */     return bytes;
/*     */   }
/*     */   public static byte[] doubleToBytes(double dnum) {
/* 132 */     return longToBytes(Double.doubleToLongBits(dnum));
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws IOException
/*     */   {
/* 139 */     SimpleXlsWriter xlsw = new SimpleXlsWriter();
/* 140 */     xlsw.setOut(new FileOutputStream(new File("d:/testXLS.xls")));
/*     */ 
/* 142 */     xlsw.start();
/* 143 */     for (int i = 0; i < 200; i++) {
/* 144 */       xlsw.addRow(
/* 145 */         new Object[] { 
/* 146 */         "第 " + i + "行", 
/* 147 */         "你好啊", 
/* 148 */         "123", 
/* 149 */         new Integer(12), 
/* 150 */         new Integer(1234), 
/* 151 */         new Long(1234567890L), 
/* 152 */         new Float(12.34D), 
/* 153 */         new Double(12345.678900000001D), 
/* 154 */         "我很好" });
/*     */     }
/*     */ 
/* 158 */     xlsw.end();
/* 159 */     xlsw.close();
/*     */   }
/*     */ }

/* Location:           F:\software\sigma_grid_with_jsp_sample_release\something missing\something missing.zip
 * Qualified Name:     com.fins.gt.export.SimpleXlsWriter
 * JD-Core Version:    0.6.0
 */