/* By:
 * 		Ivo Daniel Venhuizen Correia, no 2008110814
 * 		João Pedro Gaioso Barbosa, no 2008111830
 * 
 * Distributed Systems, October 2010 
 */

package serverWeb;


import java.io.IOException;
import java.net.Socket;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Hashtable;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import server.ClientOperations;

import clientRMI.Client;
import clientRMI.ServerOperations;


public class WebServer extends HttpServlet{
	private int clientsOn = 0;
	private static final long serialVersionUID = 1L;

	private static Registry registry;
	private static ClientOperations mainServer;
	private static ServerOperations webClient;
	private static ServerOperations multiplexer = null;
	private static Hashtable <String, ServerOperations> clientsHash;

	@Override
	public void init() throws ServletException
	{
		try
		{
			//registry = LocateRegistry.getRegistry(Constants.FIRST_RMI_SERVER_PORT);
			registry = LocateRegistry.getRegistry(12000);
			mainServer = (ClientOperations) registry.lookup("BetAndUinServer");
			webClient = new Client(registry, mainServer);
			clientsHash = new Hashtable<String, ServerOperations>();
			if (multiplexer == null)
				multiplexer = new Client(registry, mainServer);
			mainServer.addWebMultiplexer(multiplexer);
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
		String type = request.getParameter("type");
		String username = request.getParameter("username");
		String password= request.getParameter("password");
		String email= request.getParameter("email");
		String msg = null;
		
		if(type != null && type.equals("reset")){
			mainServer.clientResetCredits(request.getParameter("username"));
		}
		else if(type != null && type.equals("login") ){
			response.setContentType("text/html");
			
			if (username == null || password == null)
			{
				msg = "name or password parameter not found";
			}
			else
			{
				String value = mainServer.clientLogin(username, password, webClient, true);
				
				msg = value;

			}

			RequestDispatcher dispatcher;
			
			if (msg.equals("log successful")){
				
				clientsOn++;
				System.out.println("We have " + clientsOn + " clients on.");
				
				clientsHash.put(username, webClient);
				HttpSession session = request.getSession(true);

				//User userData = new User(username);
			    session.setAttribute("user", username);
			    session.setAttribute("status", msg);
			    session.setAttribute("server", mainServer);
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
		else if(type != null && type.equals("register")){
			response.setContentType("text/html");
			
			if (username == null || password == null || email == null)
			{
				msg = "name or password parameter not found";
			}
			else
			{
				String value = mainServer.clientRegister(username, password, email, webClient, true);
				
				msg = value;

			}

			RequestDispatcher dispatcher;
			
			if (msg.equals("log successful")){
				
				HttpSession session = request.getSession(true);

				//User userData = new User(username);
			    session.setAttribute("user", username);
			    session.setAttribute("status", msg);
			    session.setAttribute("server", mainServer);
				dispatcher = request.getRequestDispatcher("/Pages/Bet.html");
				dispatcher.forward(request, response);
				
			}
			else{
				HttpSession session = request.getSession(true);
				
				if (msg.equals("log taken")){
					session.setAttribute("status","\nSorry, but this user already exists. Please, choose a different username.\n");
			    }
			    else if (msg.equals("username all")){
			    	session.setAttribute("status","\nSorry, but the username 'all' is invalid. Please, choose a different username.\n");
			    }
				else{
					session.setAttribute("status","\nInsert a username, password and a valid e-mail address, so you can register on BetAndUin!\n");
				}

				dispatcher = request.getRequestDispatcher("/Pages/Register/Register.jsp");
				dispatcher.forward(request, response);
			}
		}
		
		
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		doGet(request, response);
	}
	
	public static void multiplexer(String msg) throws RemoteException {
		if (clientsHash != null){
		
			for (Entry<String, ServerOperations> entry: clientsHash.entrySet()){
				System.out.println("Print for: " + entry.getKey());
				entry.getValue().printUserMessage(msg, entry.getKey());
			}
		}
		
	}
}
