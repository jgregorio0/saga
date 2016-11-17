/*    */ package demo.grid.action;
/*    */ 
/*    */

import com.fins.gt.action.BaseAction;
import com.fins.gt.server.GridServerHandler;
import demo.grid.dao.DepartmentDAO;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */

/*    */
/*    */ public class DepartmentAction extends BaseAction
/*    */ {
/*    */   public void service()
/*    */     throws ServletException, IOException
/*    */   {
/* 22 */     getDepartmentList();
/*    */   }
/*    */ 
/*    */   public void getDepartmentList()
/*    */     throws ServletException, IOException
/*    */   {
/* 28 */     GridServerHandler gridServerHandler = new GridServerHandler(this.request, this.response);
/*    */ 
/* 30 */     DepartmentDAO dao = new DepartmentDAO();
/*    */ 
/* 38 */     Map param = getParameterSimpleMap();
/*    */ 
/* 40 */     List list = dao.getDepartments(param);
/*    */ 
/* 43 */     int totalRowNum = list.size();
/* 44 */     gridServerHandler.setTotalRowNum(totalRowNum);
/*    */ 
/* 46 */     gridServerHandler.setData(list);
/*    */ 
/* 48 */     print(gridServerHandler.getLoadResponseText());
/*    */   }
/*    */ }

/* Location:           F:\software\sigma_grid_with_jsp_sample_release\something missing\something missing.zip
 * Qualified Name:     demo.grid.action.DepartmentAction
 * JD-Core Version:    0.6.0
 */