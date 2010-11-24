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
import clientRMI.RMIClient;
import clientRMI.ServerOperations;


public class WebServer extends HttpServlet{

	private static final long serialVersionUID = 1L;

	private Registry registry;
	private ClientOperations mainServer;
	private ServerOperations webClient;

	@Override
	public void init() throws ServletException
	{
		try
		{
			//registry = LocateRegistry.getRegistry(Constants.FIRST_RMI_SERVER_PORT);
			registry = LocateRegistry.getRegistry(12000);
			mainServer = (ClientOperations) registry.lookup("BetAndUinServer");
		}catch (AccessException e)
		{
			throw new ServletException(e);
			
		}
		catch (RemoteException e)
		{
			throw new ServletException(e);
		}
		catch (NotBoundException e)
		{
			throw new ServletException(e);
		}
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		String username = request.getParameter("username");
		String password= request.getParameter("password");
		String msg = null;

		response.setContentType("text/html");
		
		if (username == null)
		{
			msg = "name parameter not found";
		}
		else
		{
			webClient = new Client(registry, mainServer, username);
			String value = mainServer.clientLogin(username, password, webClient);
			
			msg = value;

		}

		RequestDispatcher dispatcher;
		
		if (msg.equals("log successful")){
			
			HttpSession session = request.getSession(true);

			//User userData = new User(username);
		    session.setAttribute("user", webClient);
		    session.setAttribute("status", msg);
			dispatcher = request.getRequestDispatcher("/Pages/Bet.html");
			dispatcher.forward(request, response);
			
		}
		else{
			HttpSession session = request.getSession(true);
			
			if (msg.equals("log error")){
				session.setAttribute("status","\nUsername or password incorrect. Please try again...\n");
		    }
		    else if (msg.equals("log repeated")){
		    	session.setAttribute("status","\nSorry, but this user is already logged in...\n");
		    }
		    else if (msg.equals("log taken")){
		    	session.setAttribute("status","\nSorry, but this username isn't available, choose another.\n");
		    }
		    else if (msg.equals("username all")){
		    	session.setAttribute("status","\nSorry, but the keyword 'all' is reserved, pick another name.\n");
		    }
		    else if(msg.equals("user not registed")){
		    	session.setAttribute("status","Sorry, but you aren't registed yet.");
		    }
			else{
				session.setAttribute("status","\nInsert your username and password. Register if you don't have an account yet!\n");
			}

			dispatcher = request.getRequestDispatcher("/Pages/Login.jsp");
			dispatcher.forward(request, response);
		}
		
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		doGet(request, response);
	}
}
