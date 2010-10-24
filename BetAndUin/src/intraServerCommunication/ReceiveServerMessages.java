/* By:
 * 		Ivo Daniel Venhuizen Correia, no 2008110814
 * 		João Pedro Gaioso Barbosa, no 2008111830
 * 
 * Distributed Systems, October 2010 
 */

package intraServerCommunication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import common.Constants;

public class ReceiveServerMessages extends Thread{
	
	private DatagramSocket aSocket = null;
	private String msg;
	private int serverPort;
	private MessagesRepository msgList;
	private ConnectionWithServerManager parentThread;
	private boolean terminateThread = false;
	
	public ReceiveServerMessages(int sPort, MessagesRepository list, ConnectionWithServerManager thread){
		serverPort = sPort;
		msgList = list;
		parentThread = thread;
		this.start();
	}
	
	public void run(){
		try{
			/* Opens the Datagram socket. */
			aSocket = new DatagramSocket(serverPort);
			
			if (Constants.DEBUGGING_SERVER){
				System.out.println("ReceiveServerMessages: Socket Datagram writing in port " + serverPort + ".");
			}
			
			while(true){
				byte[] buffer = new byte[30]; //The largest message has 26 characters, so it should work. 			
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				msg=new String(request.getData(), 0, request.getLength());
				
				if (terminateThread){
					if (Constants.DEBUGGING_SERVER){
						System.out.println("ReceiveServerMessages: We are terminating the ReceiveServerMessages thread.");
						return;
					}
				}
				if (Constants.DEBUGGING_SERVER){
					System.out.println("ReceiveServerMessages: This server has received a " + msg + ".");
				}
				
				synchronized (msgList){
					msgList.addMsg(msg);
					parentThread.interrupt();
				}
			}
			
		}catch (SocketException e) {
			System.out.println("Socket in ReceiveServerMessages: " + e.getMessage());
			/* If we cannot bind, we terminate the program. */
			System.out.println("The system will shutdown.");
			System.exit(0);
		}catch (UnknownHostException e) {
			System.out.println("UnknownHostException in ReceiveServerMessages: " + e);
		} catch (IOException e) {
			System.out.println("IOException in ReceiveServerMessages: " + e);
		}finally {
			if (aSocket != null)
				aSocket.close();
			
		}
	}
	
	/* This thread is no longer needed, so we can safely terminate it
	 * and stop using more resources.
	 */
	public void terminateThread(){
		terminateThread = true;
	}

}
