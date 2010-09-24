import java.net.*;
import java.io.*;
public class UDPServer{
	public static void main(String args[]){ 
		DatagramSocket aSocket = null;
		String s;
		try{
			aSocket = new DatagramSocket(6789);
			System.out.println("Socket Datagram à escuta no porto 6789");
			while(true){
				byte[] buffer = new byte[1000]; 			
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				s=new String(request.getData(), 0, request.getLength());	
				System.out.println("Server Recebeu: " + s);	

				DatagramPacket reply = new DatagramPacket(request.getData(), 
						request.getLength(), request.getAddress(), request.getPort());
				aSocket.send(reply);
			}
		}catch (SocketException e){System.out.println("Socket: " + e.getMessage());
		}catch (IOException e) {System.out.println("IO: " + e.getMessage());
		}finally {if(aSocket != null) aSocket.close();}
	}
}