/*    */ package demo.grid.vo;
/*    */ 
/*    */ public class StudentVO
/*    */ {
/*    */   private Integer no;
/*    */   private String name;
/*    */   private String gender;
/*    */   private String birthday;
/*    */   private Integer department;
/*    */   private String memo;
/*    */   private SubStudentVO subvo;
/*    */ 
/*    */   public SubStudentVO getSubvo()
/*    */   {
/* 16 */     return this.subvo;
/*    */   }
/*    */   public void setSubvo(SubStudentVO subvo) {
/* 19 */     this.subvo = subvo;
/*    */   }
/*    */   public Integer getNo() {
/* 22 */     return this.no;
/*    */   }
/*    */   public void setNo(Integer no) {
/* 25 */     this.no = no;
/*    */   }
/*    */   public String getName() {
/* 28 */     return this.name;
/*    */   }
/*    */   public void setName(String name) {
/* 31 */     this.name = name;
/*    */   }
/*    */   public String getGender() {
/* 34 */     return this.gender;
/*    */   }
/*    */   public void setGender(String gender) {
/* 37 */     this.gender = gender;
/*    */   }
/*    */   public String getBirthday() {
/* 40 */     return this.birthday;
/*    */   }
/*    */   public void setBirthday(String birthday) {
/* 43 */     this.birthday = birthday;
/*    */   }
/*    */   public Integer getDepartment() {
/* 46 */     return this.department;
/*    */   }
/*    */   public void setDepartment(Integer department) {
/* 49 */     this.department = department;
/*    */   }
/*    */   public String getMemo() {
/* 52 */     return this.memo;
/*    */   }
/*    */   public void setMemo(String memo) {
/* 55 */     this.memo = memo;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 59 */     return super.toString() + " : " + this.no + "," + this.name + "," + this.gender + "," + this.birthday + "," + this.department + "," + this.memo;
/*    */   }
/*    */ }

/* Location:           F:\software\sigma_grid_with_jsp_sample_release\something missing\something missing.zip
 * Qualified Name:     demo.grid.vo.StudentVO
 * JD-Core Version:    0.6.0
 */