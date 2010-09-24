import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ReceiveServerMessages extends Thread{
	/*Set to true if you want the program to display debugging messages.*/
	Boolean debugging = false;
	
	DatagramSocket aSocket = null;
	String msg;
	int partnerPort;
	
	public ReceiveServerMessages(int pPort){
		partnerPort = pPort;
		
	}
	
	public void run(){
		try{
			/* Opens the Datagram socket. */
			aSocket = new DatagramSocket(partnerPort);
			
			if (debugging){
				System.out.println("Socket Datagram writing in port " + partnerPort + ".");
			}
			
			while(true){
				byte[] buffer = new byte[30]; //The largest message has 26 characters, so it should work. 			
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				msg=new String(request.getData(), 0, request.getLength());
				
				if (debugging){
					System.out.println("This server has received a " + msg + ".");
				}
			}
			
		}catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		}catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if (aSocket != null)
				aSocket.close();
			
		}
	}


}
