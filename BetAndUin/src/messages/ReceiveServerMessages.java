package messages;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import server.ConnectionWithServerManager;


public class ReceiveServerMessages extends Thread{
	/*Set to true if you want the program to display debugging messages.*/
	Boolean debugging = true;
	
	DatagramSocket aSocket = null;
	String msg;
	int serverPort;
	MessagesRepository msgList;
	ConnectionWithServerManager parentThread;
	boolean terminateThread = false;
	
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
			
			if (debugging){
				System.out.println("Socket Datagram writing in port " + serverPort + ".");
			}
			
			while(true){
				byte[] buffer = new byte[30]; //The largest message has 26 characters, so it should work. 			
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				msg=new String(request.getData(), 0, request.getLength());
				
				if (terminateThread){
					if (debugging){
						System.out.println("We are terminating the ReceiveServerMessages thread.");
						return;
					}
				}
				if (debugging){
					System.out.println("This server has received a " + msg + ".");
				}
				
				synchronized (msgList){
					msgList.addMsg(msg);
					parentThread.interrupt();
				}
			}
			
		}catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
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
