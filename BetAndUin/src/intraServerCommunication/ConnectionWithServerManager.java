package intraServerCommunication;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;


import common.Constants;


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

	/* Connection variables. */
	private int serverPort;
	private int partnerPort;
	private MessagesRepository msgToReceiveList;
	private ReceiveServerMessages receiveMessenger;
	
	/* The lock to synchronize with the server. */
	private ChangeStatusLock statusLock;
	/* This variables is used when both servers decide, at the same time
	 * that they should be the main server. When this happens and both of them
	 * detect this inconsistency (two main servers), the one with a 'false' value
	 * will give up from being main server.
	 */
	private boolean isDefaultServer;
	/* Variables that checks whether this is the primary server or not. */
	private boolean isPrimaryServer = false;
	
	/*Variables related to the sending action.*/
	private DatagramSocket aSocket = null;
	
	/* The port to which we must connect to simulate the STONITH situation. */
	private int partnerStonithPort;
	private int stonithPort;
	
	/* The IP addresses for the partner server */
	private String partnerIpAddress;
	
	public ConnectionWithServerManager(int sPort, int pPort, int sStonith, int pStonith, boolean isDefaultServer, ChangeStatusLock lock, String pIp){
		serverPort = sPort;
		partnerPort = pPort;
		this.isDefaultServer = isDefaultServer;
		msgToReceiveList = new MessagesRepository();
		receiveMessenger = new ReceiveServerMessages(serverPort, msgToReceiveList, this);
		statusLock = lock;
		partnerStonithPort = pStonith;
		stonithPort = sStonith;
		partnerIpAddress = pIp;
		
		/* Initializes the UDP socket to send messages to the other server. */
		try {
			aSocket = new DatagramSocket();
		} catch (SocketException e) {
			System.out.println("Socket (ConnectionWithServerManager Constructor): " + e.getMessage());
		}
		
		this.start();
	}
	
	public void run(){
		String partnerAnswer = "NOT_RECEIVED"; //By default
		int repetitions = 0; // In this variable, we count the number of times we tried to send
		                 // the first message to our partner.
		
		/* When the server is up, it sends the first message,
		 * corresponding to the I_WILL_BE_PRIMARY_SERVER.
		 */
		while (repetitions < Constants.SERVER_INIT_RETRIES){
			sendMessage("I_WILL_BE_PRIMARY_SERVER");
			
			/* Then, it waits for the other server to respond,
			 * acting accordingly to the answer received (or not
			 * received at all...) .
			 */
			try {
				Thread.sleep(Constants.FIRST_WAITING_TIME);
			} catch (InterruptedException e) {
				/* We have just received an answer.
				 * So, we can go off this cycle.
				 */
				break;
			}
			repetitions++;
		}
		
		/* This means the other server hasn't responded. */
		if (repetitions == Constants.SERVER_INIT_RETRIES){
			/* Now, we have to test the STONITH scenario. */
			Socket s = null;
			try {
				s = new Socket(partnerIpAddress, partnerStonithPort);
				/* The other server is alive. */
				partnerAnswer = "I_M_ALREADY_PRIMARY_SERVER";
				isPrimaryServer = false;
				
				if (Constants.DEBUGGING_SERVER){
					System.out.println("ConnectionWithServerManager: The STONITH link confirmed the other server is alive.");
				}
				
			} catch (Exception e) {
				/* The other server is dead, so, it's not only a link's problem. */
				partnerAnswer = "NOT_RECEIVED";
				isPrimaryServer = true;
				
				if (Constants.DEBUGGING_SERVER){
					System.out.println("ConnectionWithServerManager: The STONITH link confirmed the other server is really dead.");
				}
				
			} finally {
				/* We close the socket that simulates the STONITH link. */
				if (s != null){
					try {
					    s.close();
					} catch (IOException e) {
					    System.out.println("close:" + e.getMessage());
					}
				}
			}
			
		}
		
		/* We initialize and start the STONITH Manager, that will accept any connections
		 * in case the other server tries to connect. */
		new StonithManager(stonithPort, partnerPort, this);
		
		
		while(true){
			if (Constants.DEBUGGING_SERVER){
				System.out.println("ConnectionWithServerManager: I'm primary server?: " + isPrimaryServer);
			}
			synchronized(msgToReceiveList){
				/* We have some messages to read. */
				if (msgToReceiveList.listSize() > 0){
					partnerAnswer = msgToReceiveList.getMsg();
				}
			}
			
			/* We are now the primary server. */
			if ((partnerAnswer.equals("OK"))
					|| (partnerAnswer.equals("I_WILL_BE_PRIMARY_SERVER"))){
				
				if (Constants.DEBUGGING_SERVER){
					System.out.println("ConnectionWithServerManager: We fulfilled the first condition.");
				}
				
				isPrimaryServer = true;
				
				/* Informs that parent server about its status. */
				synchronized (statusLock){
					statusLock.setInitialProcessConcluded(true);
					statusLock.setPrimaryServer(true);
					if (statusLock.hasChangedStatus()){
						statusLock.notifyAll();
					}
				}
				
				/* We can now terminate the receiveMensager, it won't be needed any longer. */
				if (Constants.DEBUGGING_SERVER){
					System.out.println("ConnectionWithServerManager: We are going to terminate the receive messenger thread.");
				}
				receiveMessenger.terminateThread();
				sendTerminateMessage();
				
				while(true){
					/* Once we are the main server, we will only give up from
					 * that position if the server crashes.
					 * Consequently, we enter this endless cycle, always sending
					 * KEEP_ALIVE messages hoping the system does never fail.
					 */
					try {
						Thread.sleep(Constants.KEEP_ALIVE_TIME);
					} catch (InterruptedException e) {
						/* We have received a message, so keep going.
						/* However, this shouldn't happen, as we have
						 * terminate the receving thread up there.
						 * Therefore, it's like to be something else.
						 */
					}
					sendMessage("KEEP_ALIVE");
				}
			}			
			/* We are now the secondary server. If we get here. */
			else if(partnerAnswer.equals("KEEP_ALIVE") 
					|| partnerAnswer.equals("I_M_ALREADY_PRIMARY_SERVER")
					/* The partner isn't considered dead, so it is likely that we have
					 * just sent the first message. */
					|| (partnerAnswer.equals("I_WILL_BE_PRIMARY_SERVER") && !isDefaultServer)){
				
				if (Constants.DEBUGGING_SERVER){
					System.out.println("ConnectionWithServerManager: We fulfilled the third condition.");
				}
				
				isPrimaryServer = false;
				
				/* Informs that parent server about its status. */
				synchronized (statusLock){
					statusLock.setInitialProcessConcluded(true);
					statusLock.setPrimaryServer(false);
					if (statusLock.hasChangedStatus()){
						statusLock.notifyAll();
					}
				}
			}
		
			/* If we ever get here, it means that we are not the primary server.*/
			while (!isPrimaryServer){
				
				if (Constants.DEBUGGING_SERVER){
					System.out.println("ConnectionWithServerManager: We fulfilled the fourth condition.");
				}
				
				try {
					Thread.sleep(Constants.SERVER_WAITING_TIME);
				} catch (InterruptedException e) {
					/* A message has arrived before the timeout. */
					synchronized(msgToReceiveList){
						while (msgToReceiveList.listSize() > 0){
							partnerAnswer = msgToReceiveList.getMsg();
							
							/* Our partner has informed us that it is still alive. */
							if (partnerAnswer.equals("KEEP_ALIVE")){
								continue;
							}
							/* The other server has crashed but recover before the
							 * timeout occurred. So, it will still be the primary server.
							 */
							else if (partnerAnswer.equals("I_WILL_BE_PRIMARY_SERVER")){
								sendMessage("OK");
							}
						} // while
						continue;
					}// synchronized (msgToReceiveList)
				}// catch (InterruptedException e);
				
				/* If we ever get here, it means that the partner hasn't answer before
				 * the timeout occurred. Consequently, we have to test the STONITH link
				 * to know if we are really going to be the primary server.
				 */
				
				/* We have to test the STONITH scenario. */
				Socket s = null;
				try {
					s = new Socket(partnerIpAddress, partnerStonithPort);
					/* The other server is alive. */
					isPrimaryServer = false;
					
					/* We have to send a message to the other server, because
					 * when the primary server doesn't get an answer from its
					 * partner, it considers the partner dead and to stop wasting
					 * link resources, stops sending messages.
					 * Consequently, we have to send a message from this side in 
					 * case the link recovered and to make the primary server
					 * start sending messages again.
					 */
					sendMessage("I_WILL_BE_PRIMARY_SERVER");
					if (Constants.DEBUGGING_SERVER){
						System.out.println("ConnectionWithServerManager: The STONITH link confirmed the other server is alive.");
					}
					
				} catch (Exception e) {
					/* The other server is dead, so, it's not only a link's problem. */
					isPrimaryServer = true;
					/* Informs the parent thread about its status. */
					synchronized (statusLock){
						statusLock.setPrimaryServer(true);
						if (statusLock.hasChangedStatus()){
							statusLock.notifyAll();
						}
					}
					
					if (Constants.DEBUGGING_SERVER){
						System.out.println("ConnectionWithServerManager: The STONITH link confirmed the other server is really dead.");
					}
					
				} finally {
					/* We close the socket that simulates the STONITH link. */
					if (s != null){
						try {
						    s.close();
						} catch (IOException e) {
						    System.out.println("close:" + e.getMessage());
						}
					}
				}
			
				
			}// while (!isPrimaryServer)
			
			try {
				/* We will sleep till the other server sends a sign (i.e. a message)
				 * that it's alive and communicating. Meanwhile, it's pointless
				 * to waste both local and network resources by sending messages.
				 */
				
				
				if (Constants.DEBUGGING_SERVER){
					System.out.println("ConnectionWithServerManager: We are now going to stop sending messages till our partner " +
							" sends us a message.");
				}
				
				/* Informs that parent server about its status. */
				synchronized (statusLock){
					statusLock.setInitialProcessConcluded(true);
					statusLock.setPrimaryServer(true);
					if (statusLock.hasChangedStatus()){
						statusLock.notifyAll();
					}
				}
				
				synchronized(msgToReceiveList){
					msgToReceiveList.wait();
				}
			} catch (InterruptedException e) {
				/* Our partner has awaken! */
			}
		}// while(true) from high above!
	}
	
	public void sendMessage(String msgToSend){
		/* Sends a message to the partner port. */
		if (Constants.DEBUGGING_SERVER){
			System.out.println("We are sending " + msgToSend + " to the other server, in port " + partnerPort +  ".");
		}
		
		try {
			byte [] m = msgToSend.getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");

			DatagramPacket request = new DatagramPacket(m,m.length,aHost,partnerPort);
			aSocket.send(request);
			
		} catch (IOException e){
			System.out.println("IO from sendMessage (ConnectionWithServerManager): " + e.getMessage());
		} catch (Exception e){
			System.out.println("Exception from sendMessage (ConnectionWithServerManager): " + e.getMessage());
		}
	}
	
	public void sendTerminateMessage(){
		/* Sends a terminate thread message to our receive messenger. */
		String message = "TERMINATE THREAD";
		
		if (Constants.DEBUGGING_SERVER){
			System.out.println("ConnectionWithServerManager: We are sending a terminate thread message.");
		}
		
		try {
			byte [] m = message.getBytes();
			InetAddress aHost = InetAddress.getByName("localhost");

			DatagramPacket request = new DatagramPacket(m,m.length,aHost,serverPort);
			aSocket.send(request);
			
		} catch (IOException e){
			if (Constants.DEBUGGING_SERVER){
				System.out.println("ConnectionWithServerManager: IO from sendTerminateThread: " + e.getMessage());
			}
		}
	}
	
	/* This method is used when we want to simulate the STONITH scenario and cause
	 * failure on the link
	 */
	public void setPartnetPort(int port){
		partnerPort = port;
	}

}
