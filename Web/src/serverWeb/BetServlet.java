package serverWeb;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import server.ClientOperations;

public class BetServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		HttpSession session = request.getSession();

		String user =((String)session.getAttribute("user"));
		
		int gameNumber = Integer.parseInt(request.getParameter("gameNumber"));
		
		String bet = request.getParameter("bet");
		int credits = Integer.parseInt(request.getParameter("credits"));

		String cenas = ((ClientOperations)session.getAttribute("server")).clientMakeBet(user, gameNumber, bet, credits);
		
		((ClientOperations)session.getAttribute("server")).clientSendMsgUser(user, user, cenas);
	}

}
