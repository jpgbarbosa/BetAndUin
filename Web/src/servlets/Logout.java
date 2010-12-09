/* By:
 * 		Ivo Daniel Venhuizen Correia, no 2008110814
 * 		João Pedro Gaioso Barbosa, no 2008111830
 * 
 * Distributed Systems, October 2010 
 */

package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import server.ClientOperations;


public class Logout extends HttpServlet{

	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException
	{
		
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		HttpSession session = request.getSession(true);
		
		try{
			System.out.println("We have " + (String)session.getAttribute("user"));
			((ClientOperations)session.getAttribute("server")).clientLeave((String)session.getAttribute("user"));
			session.invalidate();
			
			response.sendRedirect("/BetAndUinWeb/Pages/Login.jsp");
		} catch(Exception e){
			//dispatcher = request.getRequestDispatcher("/Pages/cenas.html");
			//dispatcher.forward(request, response);
		}
		
		
		
		
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		doGet(request, response);
	}
}
