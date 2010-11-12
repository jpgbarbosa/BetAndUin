/* By:
 * 		Ivo Daniel Venhuizen Correia, no 2008110814
 * 		João Pedro Gaioso Barbosa, no 2008111830
 * 
 * Distributed Systems, October 2010 
 */

package serverWeb;

import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import server.ClientOperations;

import clientRMI.ServerOperations;


public class WebServer extends HttpServlet implements ServerOperations{

	private static final long serialVersionUID = 1L;
	
	private final String HTML_START = "<html><head></head><body>";
	private final String HTML_END = "</body></html>";

	private Registry registry;
	private ClientOperations mainServer;

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
		String name = request.getParameter("name");
		String message = request.getParameter("msg");
		PrintWriter out = null;

		response.setContentType("text/html");
		out = response.getWriter();

		String msg = HTML_START;
		if (name == null)
		{
			msg += "name parameter not found";
		}
		else
		{
			String value = mainServer.clientShowMenu();
			
			if (value == null)
			{
				msg += "User " + name + " not found in database";
			}
			else
			{
				msg += "Age of " + name + " is " + value;
			}
		}

		msg += HTML_END;
		out.write(msg);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		doGet(request, response);
	}
	
	@Override
	public void printUserMessage(String userName, String msg) throws java.rmi.RemoteException{
		System.out.println(msg + "\n");
		System.out.print(" >>> ");
	}
	    
    @Override
    public boolean testUser() throws java.rmi.RemoteException{
    	return true;
    }
}
