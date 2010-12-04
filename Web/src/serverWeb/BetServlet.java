package serverWeb;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.comet.CometEvent;
import org.apache.catalina.comet.CometProcessor;

import server.ClientOperations;

public class BetServlet extends HttpServlet implements CometProcessor{

	private static final long serialVersionUID = 1L;
	
	// The clients Map is used to associate a specific user id with a particular
	// HttpServletResponse object. This way if, later on, we want to send 
	// something to the client's socket, we can retrieve the HttpServletResponse.
	private static Map<String, HttpServletResponse> clients = new Hashtable<String, HttpServletResponse>();
	
	// Method called when a client is registers with the CometProcessor
	private static void addClient(String nickName, HttpServletResponse clientResponseObject) {
		BetServlet.clients.put(nickName, clientResponseObject);
		// TODO 1: Write your code here.
	}

	
	// Method called after an Exception is thrown when the server tries to write to a client's socket.
	private static void removeClient(String nickName, HttpServletRequest request) {
		if (BetServlet.clients.remove(nickName) != null) {
			// TODO 2: Write your code here
		}
	}

	
	// Main method that handles all the assynchronous calls to the servlet.
	// Receives a CometEvent object, that might have three types of EventType:
	// - BEGIN (when the connection starts. It is used to initialize variables and register the callback
	// - READ (means that there is data sent by the client available to be processed.
	// - END (happens when the connection is terminated, to clean variables and so on.
	// - ERROR (Happens when some IOException is thrown when writing/reading the connection.
	
	public void event(CometEvent event) throws IOException, ServletException {
		
		// request and response exactly like in Servlets
		HttpServletRequest request = event.getHttpServletRequest();
		HttpServletResponse response = event.getHttpServletResponse();
		HttpSession session = request.getSession();
		
		// Parse the something from "?type=something" in the URL.
		String reqType = request.getParameter("type");

		// Initialize the SESSION and Cache headers.
		String sessionId = session.getId();
		String user = (String) session.getAttribute("user");

		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-control", "no-cache");
		// Disabling the cache, means that the browser will _always_ call this code.
		
		// Since the "event" method is called for every kind of event, we have to decide what to do
		// based on the Event type. There for we check for all 4 kinds of events: BEGIN, READ, END and ERROR
		if (event.getEventType() == CometEvent.EventType.BEGIN) {
			// A connection is initiliazed
			
			if (reqType != null) {
				if (reqType.equalsIgnoreCase("register")) {
					// Register will add the client HttpServletResponse to the callback array and start a streamed response.
					
					// This header is sent to keep the connection open, in order to send future updates.
					response.setHeader("Content-type", "application/octet-stream");
					// Here is where the important Comet magic happens.
					
					// Let's save the HttpServletResponse with the nickName key.
					//  That response object will act as a callback to the client.
					addClient(user, response);
					
				} else if (reqType.equalsIgnoreCase("exit")) {
					// if the client wants to quit, we do it.					
					removeClient(sessionId, request);
					/*The other part of the removing process (informing the betting server
					 * that this client is moving away) is already being done by the other
					 * comet processor, namely the ChatServlet.
					 */
					
				} else if (reqType.equalsIgnoreCase("bet")){

					int gameNumber = Integer.parseInt(request.getParameter("gameNumber"));
					
					String bet = request.getParameter("bet");
					int credits = Integer.parseInt(request.getParameter("credits"));

					String answer = ((ClientOperations)session.getAttribute("server")).clientMakeBet(user, gameNumber, bet, credits);
					
					((ClientOperations)session.getAttribute("server")).clientSendMsgUser(user, user, answer);
				}
		
			}
		} else if (event.getEventType() == CometEvent.EventType.ERROR) {
			// In case of any error, we terminate the connection.
			// The connection remains in cache anyway, and it's later removed
			// when an Exception at write-time is raised.
			event.close();
		} else if (event.getEventType() == CometEvent.EventType.END) {
			// When the clients wants to finish, we do it the same way as above.
			event.close();
		}
	}

	public static void sendMessage(String message, String destination) {
		// This method sends a message to a specific user
		synchronized (BetServlet.clients) {
			try {
				HttpServletResponse resp = BetServlet.clients.get(destination);
				resp.getWriter().println(message + "<br/>");
				resp.getWriter().flush();
			} catch (Exception ex) {
				// Trouble using the response object's writer so we remove
				// the user and response object from the hashtable
				removeClient(destination,null);
			}
		}
	}

}
