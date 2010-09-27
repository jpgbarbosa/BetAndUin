package client;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

import server.ConnectionLock;


public class ClientReadTCP extends Thread {
	DataInputStream in;
    Socket clientSocket;
    ConnectionLock connectionLock;
    
    public ClientReadTCP (ConnectionLock lock) {
    	connectionLock = lock;
        this.start();
    }
    //=============================
    public void run(){
    	while (true){
	        try{
	            while(true){
	            	synchronized(connectionLock){
		        		while (connectionLock.isConnectionDown()){
		        			try {
		        				connectionLock.wait();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		        		}
		        	}
	                //an echo server
	                String data = in.readUTF();
	                System.out.println("MENSAGEM RECEBIDA:\n   "+data + "\nFIM DA MENSAGEM\n");
	            }
	        }catch(EOFException e){
	        	System.out.println("EOF:" + e);
	        }catch(IOException e){
	        	System.out.println("IO:" + e);
	        	synchronized(connectionLock){
	        		connectionLock.setConnectionDown(true);
	        		connectionLock.notify();
	        	}
	        	System.out.println("The server is down. Please wait while we try to reconnect...");
	        }
    	}
    }
    
    public void setSocket(Socket s){
    	clientSocket = s;
    	try{
            clientSocket = s;
            in = new DataInputStream(clientSocket.getInputStream());
        }catch(IOException e){System.out.println("Connection:" + e.getMessage());}
    }
}

