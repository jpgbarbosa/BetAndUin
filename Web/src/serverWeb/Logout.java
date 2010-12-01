/* By:
 * 		Ivo Daniel Venhuizen Correia, no 2008110814
 * 		João Pedro Gaioso Barbosa, no 2008111830
 * 
 * Distributed Systems, October 2010 
 */

package serverWeb;

import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import users.User;
import server.ClientOperations;

import clientRMI.Client;
import clientRMI.ServerOperations;


public class Logout extends HttpServlet{

	private static final long serialVersionUID = 1L;

	private static Registry registry;
	private static ClientOperations mainServer;
	private static ServerOperations webClient;

	@Override
	public void init() throws ServletException
	{
		
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		RequestDispatcher dispatcher;
		HttpSession session = request.getSession(true);
		
		try{
			WebServer.getMainServer().clientLeave((String)session.getAttribute("user"));
			session.invalidate();
		} catch(Exception e){
			//TODO: Complete this.
		}
		
		
		//dispatcher = request.getRequestDispatcher("/Pages/Login.jsp");
		//dispatcher.forward(request, response);
		
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		doGet(request, response);
	}
}
