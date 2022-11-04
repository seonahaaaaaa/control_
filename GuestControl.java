package mvc.guest.control;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.guest.command.Command;
import mvc.guest.command.CommandDelete;
import mvc.guest.command.CommandException;
import mvc.guest.command.CommandInput;
import mvc.guest.command.CommandList;
import mvc.guest.command.CommandNull;

/**
 * Servlet implementation class GuestControl
 */
public class GuestControl extends HttpServlet {
				// 서블릿 페이지 먼저 실행 => 뷰단이 실행시 null 값으로 빈 데이터가 안들어간 상태임.
	
	
	private HashMap commandMap;
	private String	jspDir = "/05_mvc_class/2_mvcGuest/";
	private String  error = "error.jsp";
	

    public GuestControl() {
        super();       
		initCommand();
	}

	private void initCommand(){
		commandMap = new HashMap();	//(키 & 값)

		commandMap.put("main-page",	new CommandNull("main.jsp") );
			// db로 이동하지 않는 함수 => main-page의 값이 들어오면 main.jsp 페이지 이동만 함
		commandMap.put("list-page",	new CommandList("listMessage.jsp") );
			// [목록보기] 클릭시 list-page 명령어가 넘어온 후 listMessage.jsp 페이지 이동
		commandMap.put("input-form",	new CommandNull("insertMessage.jsp") );
			// 방명록 남기기 버튼 클릭시 input-form 명령어 실행 후 insertMessage.jsp 페이지 이동
		commandMap.put("iput-do",	new CommandInput("saveMessage.jsp") );
			// input-do의 값이 들어오면 saveMessage.jsp 페이지 이동
		commandMap.put("delete-form",	new CommandNull("deleteMessage.jsp") );
			// 삭제 되었는지 확인 문구 띄워 출력만 하기 위해 CommandNull()
		commandMap.put("delete-confirm",	new CommandDelete("deleteConfirm.jsp") );
			// form에서 받아온 값 delete-confirm 명령어 들어올 시 데이터 삭제됨.
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}
	

	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");

		String nextPage = "";
		String cmdKey	= request.getParameter("cmd");	// list-page 값 들어옴
		if( cmdKey == null ){	// 값 들어온 상태
			cmdKey = "main-page";	// list-page 값 들어오면 해당 값을
		}

		
		Command cmd = null;

		try{
			
			if( commandMap.containsKey( cmdKey ) ){
				cmd = (Command)commandMap.get( cmdKey);
			}else{
				throw new CommandException("지정할 명령어가 존재하지 않음");
			}

			nextPage = cmd.execute( request, response  );

		}catch( CommandException e ){
			// request 에 데이터 저장
			request.setAttribute("javax.servlet.jsp.jspException", e );
			nextPage = error;
			System.out.println("오류 : " + e.getMessage() );
		}

		// 해당 페이지에서 작업힌 내용을 이어받아 호출 된 페이지에서 처리하는 전이 방식
		RequestDispatcher reqDp = getServletContext().getRequestDispatcher( jspDir + nextPage );
						//  jspDir 담은 경로를 요청하며. 
		reqDp.forward( request, response );
			//  request, response 에 저장 된 데이터를 공유 => request : 저장소 & response : 출력도구
		
	}

}
