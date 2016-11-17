package demo.grid.action;

import com.fins.gt.action.BaseAction;
import com.fins.gt.server.GridServerHandler;
import demo.grid.dao.StudentsDAO;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 */
public class StudentAction extends BaseAction {
	
	
	/**
	 * @throws javax.servlet.ServletException
	 * @throws java.io.IOException
	 */
	public void getStudentsListByDep() throws ServletException, IOException {

		GridServerHandler gridServerHandler=new GridServerHandler(request,response);
	
		StudentsDAO dao=new StudentsDAO();
		Map param = getParameterSimpleMap() ;
		List list = dao.getStudentsListByDep(param);

		int totalRowNum = list.size();
		gridServerHandler.setTotalRowNum(totalRowNum);
		gridServerHandler.setData(list);

		print(gridServerHandler.getLoadResponseText());
	}
	
	/**
	 */
	public void getList() throws ServletException, IOException {
		StudentsDAO dao=new StudentsDAO();
		List list=null;
		
		GridServerHandler gridServerHandler=new GridServerHandler(request,response);
		
		int totalRowNum=gridServerHandler.getTotalRowNum();
		if (totalRowNum<1){
			totalRowNum=dao.countAllStudents();
			gridServerHandler.setTotalRowNum(totalRowNum);
		}
		
		list=dao.getStudentsByPage(gridServerHandler.getStartRowNum(),gridServerHandler.getPageSize());
		
		gridServerHandler.setData(list);
//		gridServerHandler.setException("your exception message");
		
		print(gridServerHandler.getLoadResponseText());
		
	}
	
	
	/**
	 */
	public void getAllList() throws ServletException, IOException {
		StudentsDAO dao=new StudentsDAO();
		List list=null;
		GridServerHandler gridServerHandler=new GridServerHandler(request,response);

		list=dao.getAllStudents();
		
		gridServerHandler.setData(list);

		print(gridServerHandler.getLoadResponseText());

	}
	
	
	
	/**
	 * 增删改对应的action 方法
	 */
	public void doSave() throws ServletException, IOException {
		StudentsDAO dao=new StudentsDAO();
		boolean success=true;
		GridServerHandler gridServerHandler=new GridServerHandler(request,response);
		
		List insertedRecords = gridServerHandler.getInsertedRecords();
		List updatedList = gridServerHandler.getUpdatedRecords();
		List deletedRecords = gridServerHandler.getDeletedRecords();

		success = dao.saveStudents(insertedRecords , updatedList,  deletedRecords );

		
		gridServerHandler.setSuccess(success);
		
//		gridServerHandler.setSuccess(false);
//		gridServerHandler.setException("... exception info ...");

		print(gridServerHandler.getSaveResponseText());
	}

}
