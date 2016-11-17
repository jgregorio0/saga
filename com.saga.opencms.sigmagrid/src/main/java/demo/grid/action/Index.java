package demo.grid.action;

import com.fins.gt.action.BaseAction;

import javax.servlet.ServletException;
import java.io.IOException;

public class Index extends BaseAction {

	public void service() throws ServletException, IOException {
		forward("/main");
	}
}
