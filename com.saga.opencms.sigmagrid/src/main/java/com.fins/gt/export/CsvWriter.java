/*     */ package com.fins.gt.export;
/*     */ 
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.Writer;
/*     */ import java.nio.charset.Charset;
/*     */ 
/*     */ public class CsvWriter
/*     */ {
/*  35 */   private PrintWriter outputStream = null;
/*     */ 
/*  37 */   private String fileName = null;
/*     */ 
/*  39 */   private boolean firstColumn = true;
/*     */ 
/*  41 */   private boolean useCustomRecordDelimiter = false;
/*     */ 
/*  43 */   private Charset charset = null;
/*     */ 
/*  46 */   private UserSettings userSettings = new UserSettings();
/*     */ 
/*  48 */   private boolean initialized = false;
/*     */ 
/*  50 */   private boolean closed = false;
/*     */   public static final int ESCAPE_MODE_DOUBLED = 1;
/*     */   public static final int ESCAPE_MODE_BACKSLASH = 2;
/*     */ 
/*     */   public CsvWriter(String fileName, char delimiter, Charset charset)
/*     */   {
/*  77 */     if (fileName == null) {
/*  78 */       throw new IllegalArgumentException("Parameter fileName can not be null.");
/*     */     }
/*     */ 
/*  81 */     if (charset == null) {
/*  82 */       throw new IllegalArgumentException("Parameter charset can not be null.");
/*     */     }
/*     */ 
/*  85 */     this.fileName = fileName;
/*  86 */     this.userSettings.Delimiter = delimiter;
/*  87 */     this.charset = charset;
/*     */   }
/*     */ 
/*     */   public CsvWriter(String fileName)
/*     */   {
/*  99 */     this(fileName, ',', Charset.forName("ISO-8859-1"));
/*     */   }
/*     */ 
/*     */   public CsvWriter(Writer outputStream, char delimiter)
/*     */   {
/* 112 */     if (outputStream == null) {
/* 113 */       throw new IllegalArgumentException("Parameter outputStream can not be null.");
/*     */     }
/*     */ 
/* 116 */     this.outputStream = new PrintWriter(outputStream);
/* 117 */     this.userSettings.Delimiter = delimiter;
/* 118 */     this.initialized = true;
/*     */   }
/*     */ 
/*     */   public CsvWriter(OutputStream outputStream, char delimiter, Charset charset)
/*     */   {
/* 134 */     this(new OutputStreamWriter(outputStream, charset), delimiter);
/*     */   }
/*     */ 
/*     */   public char getDelimiter()
/*     */   {
/* 143 */     return this.userSettings.Delimiter;
/*     */   }
/*     */ 
/*     */   public void setDelimiter(char delimiter)
/*     */   {
/* 153 */     this.userSettings.Delimiter = delimiter;
/*     */   }
/*     */ 
/*     */   public char getRecordDelimiter() {
/* 157 */     return this.userSettings.RecordDelimiter;
/*     */   }
/*     */ 
/*     */   public void setRecordDelimiter(char recordDelimiter)
/*     */   {
/* 169 */     this.useCustomRecordDelimiter = true;
/* 170 */     this.userSettings.RecordDelimiter = recordDelimiter;
/*     */   }
/*     */ 
/*     */   public char getTextQualifier()
/*     */   {
/* 179 */     return this.userSettings.TextQualifier;
/*     */   }
/*     */ 
/*     */   public void setTextQualifier(char textQualifier)
/*     */   {
/* 189 */     this.userSettings.TextQualifier = textQualifier;
/*     */   }
/*     */ 
/*     */   public boolean getUseTextQualifier()
/*     */   {
/* 198 */     return this.userSettings.UseTextQualifier;
/*     */   }
/*     */ 
/*     */   public void setUseTextQualifier(boolean useTextQualifier)
/*     */   {
/* 208 */     this.userSettings.UseTextQualifier = useTextQualifier;
/*     */   }
/*     */ 
/*     */   public int getEscapeMode() {
/* 212 */     return this.userSettings.EscapeMode;
/*     */   }
/*     */ 
/*     */   public void setEscapeMode(int escapeMode) {
/* 216 */     this.userSettings.EscapeMode = escapeMode;
/*     */   }
/*     */ 
/*     */   public void setComment(char comment) {
/* 220 */     this.userSettings.Comment = comment;
/*     */   }
/*     */ 
/*     */   public char getComment() {
/* 224 */     return this.userSettings.Comment;
/*     */   }
/*     */ 
/*     */   public boolean getForceQualifier()
/*     */   {
/* 234 */     return this.userSettings.ForceQualifier;
/*     */   }
/*     */ 
/*     */   public void setForceQualifier(boolean forceQualifier)
/*     */   {
/* 246 */     this.userSettings.ForceQualifier = forceQualifier;
/*     */   }
/*     */ 
/*     */   public void write(String content, boolean preserveSpaces)
/*     */     throws IOException
/*     */   {
/* 265 */     checkClosed();
/*     */ 
/* 267 */     checkInit();
/*     */ 
/* 269 */     if (content == null) {
/* 270 */       content = "";
/*     */     }
/*     */ 
/* 273 */     if (!this.firstColumn) {
/* 274 */       this.outputStream.write(this.userSettings.Delimiter);
/*     */     }
/*     */ 
/* 277 */     boolean textQualify = this.userSettings.ForceQualifier;
/*     */ 
/* 279 */     if ((!preserveSpaces) && (content.length() > 0)) {
/* 280 */       content = content.trim();
/*     */     }
/*     */ 
/* 283 */     if ((!textQualify) && 
/* 284 */       (this.userSettings.UseTextQualifier) && (
/* 285 */       (content.indexOf(this.userSettings.TextQualifier) > -1) || 
/* 286 */       (content.indexOf(this.userSettings.Delimiter) > -1) || 
/* 287 */       ((!this.useCustomRecordDelimiter) && (
/* 288 */       (content.indexOf('\n') > -1) || 
/* 289 */       (content.indexOf('\r') > -1))) || 
/* 290 */       ((this.useCustomRecordDelimiter) && 
/* 291 */       (content.indexOf(this.userSettings.RecordDelimiter) > -1)) || 
/* 292 */       ((this.firstColumn) && (content.length() > 0) && (
/* 293 */       content.charAt(0) == this.userSettings.Comment)) || (
/* 296 */       (this.firstColumn) && (content.length() == 0)))) {
/* 297 */       textQualify = true;
/*     */     }
/*     */ 
/* 300 */     if ((this.userSettings.UseTextQualifier) && (!textQualify) && 
/* 301 */       (content.length() > 0) && (preserveSpaces)) {
/* 302 */       char firstLetter = content.charAt(0);
/*     */ 
/* 304 */       if ((firstLetter == ' ') || (firstLetter == '\t')) {
/* 305 */         textQualify = true;
/*     */       }
/*     */ 
/* 308 */       if ((!textQualify) && (content.length() > 1)) {
/* 309 */         char lastLetter = content.charAt(content.length() - 1);
/*     */ 
/* 311 */         if ((lastLetter == ' ') || (lastLetter == '\t')) {
/* 312 */           textQualify = true;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 317 */     if (textQualify) {
/* 318 */       this.outputStream.write(this.userSettings.TextQualifier);
/*     */ 
/* 320 */       if (this.userSettings.EscapeMode == 2) {
/* 321 */         content = replace(content, "\\", "\\\\");
/*     */ 
/* 323 */         content = replace(content, "" + this.userSettings.TextQualifier, "\\" + 
/* 324 */           this.userSettings.TextQualifier);
/*     */       } else {
/* 326 */         content = replace(content, "" + this.userSettings.TextQualifier, 
/* 327 */           "" + this.userSettings.TextQualifier + 
/* 328 */           this.userSettings.TextQualifier);
/*     */       }
/* 330 */     } else if (this.userSettings.EscapeMode == 2) {
/* 331 */       content = replace(content, "\\", "\\\\");
/*     */ 
/* 333 */       content = replace(content, "" + this.userSettings.Delimiter, "\\" + 
/* 334 */         this.userSettings.Delimiter);
/*     */ 
/* 336 */       if (this.useCustomRecordDelimiter) {
/* 337 */         content = replace(content, "" + this.userSettings.RecordDelimiter, 
/* 338 */           "\\" + this.userSettings.RecordDelimiter);
/*     */       } else {
/* 340 */         content = replace(content, "\r", "\\\r");
/*     */ 
/* 342 */         content = replace(content, "\n", "\\\n");
/*     */       }
/*     */ 
/* 346 */       if ((this.firstColumn) && (content.length() > 0) && 
/* 347 */         (content.charAt(0) == this.userSettings.Comment)) {
/* 348 */         if (content.length() > 1)
/* 349 */           content = "\\" + this.userSettings.Comment + 
/* 350 */             content.substring(1);
/*     */         else {
/* 352 */           content = "\\" + this.userSettings.Comment;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 357 */     this.outputStream.write(content);
/*     */ 
/* 359 */     if (textQualify) {
/* 360 */       this.outputStream.write(this.userSettings.TextQualifier);
/*     */     }
/*     */ 
/* 363 */     this.firstColumn = false;
/*     */   }
/*     */ 
/*     */   public void write(String content)
/*     */     throws IOException
/*     */   {
/* 377 */     write(content, false);
/*     */   }
/*     */ 
/*     */   public void writeComment(String commentText) throws IOException {
/* 381 */     checkClosed();
/*     */ 
/* 383 */     checkInit();
/*     */ 
/* 385 */     this.outputStream.write(this.userSettings.Comment);
/*     */ 
/* 387 */     this.outputStream.write(commentText);
/*     */ 
/* 389 */     if (this.useCustomRecordDelimiter)
/* 390 */       this.outputStream.write(this.userSettings.RecordDelimiter);
/*     */     else {
/* 392 */       this.outputStream.println();
/*     */     }
/*     */ 
/* 395 */     this.firstColumn = true;
/*     */   }
/*     */ 
/*     */   public void writeRecord(String[] values, boolean preserveSpaces)
/*     */     throws IOException
/*     */   {
/* 414 */     if ((values != null) && (values.length > 0)) {
/* 415 */       for (int i = 0; i < values.length; i++) {
/* 416 */         write(values[i], preserveSpaces);
/*     */       }
/*     */ 
/* 419 */       endRecord();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void writeRecord(String[] values)
/*     */     throws IOException
/*     */   {
/* 434 */     writeRecord(values, false);
/*     */   }
/*     */ 
/*     */   public void endRecord()
/*     */     throws IOException
/*     */   {
/* 445 */     checkClosed();
/*     */ 
/* 447 */     checkInit();
/*     */ 
/* 449 */     if (this.useCustomRecordDelimiter)
/* 450 */       this.outputStream.write(this.userSettings.RecordDelimiter);
/*     */     else {
/* 452 */       this.outputStream.println();
/*     */     }
/*     */ 
/* 455 */     this.firstColumn = true;
/*     */   }
/*     */ 
/*     */   private void checkInit()
/*     */     throws IOException
/*     */   {
/* 462 */     if (!this.initialized) {
/* 463 */       if (this.fileName != null) {
/* 464 */         this.outputStream = 
/* 465 */           new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.fileName), this.charset));
/*     */       }
/*     */ 
/* 468 */       this.initialized = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void flush()
/*     */   {
/* 477 */     this.outputStream.flush();
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/* 484 */     if (!this.closed) {
/* 485 */       close(true);
/*     */ 
/* 487 */       this.closed = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void close(boolean closing)
/*     */   {
/* 495 */     if (!this.closed) {
/* 496 */       if (closing) {
/* 497 */         this.charset = null;
/*     */       }
/*     */       try
/*     */       {
/* 501 */         if (this.initialized) {
/* 502 */           this.outputStream.close();
/*     */         }
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/*     */       }
/* 508 */       this.outputStream = null;
/*     */ 
/* 510 */       this.closed = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void checkClosed()
/*     */     throws IOException
/*     */   {
/* 518 */     if (this.closed)
/* 519 */       throw new IOException(
/* 520 */         "This instance of the CsvWriter class has already been closed.");
/*     */   }
/*     */ 
/*     */   protected void finalize()
/*     */   {
/* 528 */     close(false);
/*     */   }
/*     */ 
/*     */   public static String replace(String original, String pattern, String replace)
/*     */   {
/* 580 */     int len = pattern.length();
/* 581 */     int found = original.indexOf(pattern);
/*     */ 
/* 583 */     if (found > -1) {
/* 584 */       StringBuffer sb = new StringBuffer();
/* 585 */       int start = 0;
/*     */ 
/* 587 */       while (found != -1) {
/* 588 */         sb.append(original.substring(start, found));
/* 589 */         sb.append(replace);
/* 590 */         start = found + len;
/* 591 */         found = original.indexOf(pattern, start);
/*     */       }
/*     */ 
/* 594 */       sb.append(original.substring(start));
/*     */ 
/* 596 */       return sb.toString();
/*     */     }
/* 598 */     return original;
/*     */   }
/*     */ 
/*     */   public static String conver(String content)
/*     */     throws IOException
/*     */   {
/* 606 */     if (content == null) {
/* 607 */       content = "";
/*     */     }
/*     */ 
/* 610 */     content = replace(content, "\"", "\"\"");
/*     */ 
/* 613 */     content = replace(content, "\r\n", "\r");
/*     */ 
/* 616 */     content = '"' + content + '"';
/* 617 */     return content;
/*     */   }
/*     */ 
/*     */   private class Letters
/*     */   {
/*     */     public static final char LF = '\n';
/*     */     public static final char CR = '\r';
/*     */     public static final char QUOTE = '"';
/*     */     public static final char COMMA = ',';
/*     */     public static final char SPACE = ' ';
/*     */     public static final char TAB = '\t';
/*     */     public static final char POUND = '#';
/*     */     public static final char BACKSLASH = '\\';
/*     */     public static final char NULL = '\000';
/*     */ 
/*     */     private Letters()
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   private class UserSettings
/*     */   {
/*     */     public char TextQualifier;
/*     */     public boolean UseTextQualifier;
/*     */     public char Delimiter;
/*     */     public char RecordDelimiter;
/*     */     public char Comment;
/*     */     public int EscapeMode;
/*     */     public boolean ForceQualifier;
/*     */ 
/*     */     public UserSettings()
/*     */     {
/* 569 */       this.TextQualifier = '"';
/* 570 */       this.UseTextQualifier = true;
/* 571 */       this.Delimiter = ',';
/* 572 */       this.RecordDelimiter = '\000';
/* 573 */       this.Comment = '#';
/* 574 */       this.EscapeMode = 1;
/* 575 */       this.ForceQualifier = false;
/*     */     }
/*     */   }
/*     */ }

/* Location:           F:\software\sigma_grid_with_jsp_sample_release\something missing\something missing.zip
 * Qualified Name:     com.fins.gt.export.CsvWriter
 * JD-Core Version:    0.6.0
 */