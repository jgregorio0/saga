/*    */ package demo.grid.dao;
/*    */ 
/*    */

import com.fins.gt.dataaccess.BaseDAO;

import java.util.List;
import java.util.Map;

/*    */
/*    */

/*    */
/*    */ public class DepartmentDAO extends BaseDAO
/*    */ {
/*    */   public List getDepartments(Map param)
/*    */   {
/* 11 */     List list = executeQuery("SELECT * FROM department_info WHERE 1=1  #{IF:no!=EMPTY} and no = #{no} #{/IF} #{IF:name!=EMPTY} and name like #{name} #{/IF}", 
/* 14 */       param);
/* 15 */     return list;
/*    */   }
/*    */ }

/* Location:           F:\software\sigma_grid_with_jsp_sample_release\something missing\something missing.zip
 * Qualified Name:     demo.grid.dao.DepartmentDAO
 * JD-Core Version:    0.6.0
 */