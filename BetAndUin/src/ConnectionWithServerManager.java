import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/* Message types and their definitions:
 * I_WILL_BE_PRIMARY_SERVER:
 *     -> This is the first message sent when the server is up, informing
 *        the other server that it desires to be the primary server.
 *        Then, it waits for a response from the other server, indicating
 *        whether it is already the primary server or not.
 *        
 * I_M_ALREADY_PRIMARY_SERVER:
 *     -> Upon receiving a I_WILL_BE_PRIMARY_SERVER message, if it is
 *        already primary server, it will send this message.
 *        
 * OK:
 *     -> Upon receiving a I_WILL_BE_PRIMARY_SERVER message, if it isn't
 *        already the primary server, it will send this message. The use
 *        of this message is unlikely.
 *        
 * KEEP_ALIVE:
 *     -> Message used by the main server to inform the secondary server
 *        that it is functioning.
 */

public class ConnectionWithServerManager extends Thread{
	/*Set to true if you want the program to display debugging messages.*/
	Boolean debugging = false;

	/* Connection variables. */
	int serverPort;
	int partnerPort;
	
	public ConnectionWithServerManager(int sPort, int pPort){
		serverPort = sPort;
		partnerPort = pPort;
	}
	
	public void run(){
		String messageType;
		
		/* When the server is up, it sends the first message,
		 * corresponding to the I_WILL_BE_PRIMARY_SERVER.
		 */
		messageType = "I_WILL_BE_PRIMARY_SERVER";

		
		/* Then, it waits for the other server to respond,
		 * acting accordingly to the answer received (or not
		 * received at all...) .
		 */

		
	}

}
