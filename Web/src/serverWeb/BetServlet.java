package serverWeb;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BetServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		
		
		String user =((String)request.getSession().getAttribute("user"));
		
		int gameNumber = Integer.parseInt(request.getParameter("gameNumber"));
		
		String bet = request.getParameter("bet");
		int credits = Integer.parseInt(request.getParameter("credits"));
		
		WebServer.getMainServer().clientMakeBet(user, gameNumber, bet, credits);
	}

}
