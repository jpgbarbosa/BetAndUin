import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientWriteTCP extends Thread {
	//This thread will be responsible for handling problems with the link to the server.
    DataOutputStream out;
    Socket clientSocket;
    String userInput;
    int serverSocketFirst = 6000, serverSocketSecond = 7000;
    ConnectionLock connectionLock;
    
    BufferedReader reader;
    
    public ClientWriteTCP (ConnectionLock lock) {
    	connectionLock = lock;
        this.start();
    }
    
    public void run(){
    	while (true){
	        try{
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
	            while(true){
	            	userInput = reader.readLine();
	                out.writeUTF(userInput);
	            }
	        }catch(EOFException e){
	        	System.out.println("EOF:" + e);
	        }catch(IOException e){
	        	System.out.println("IO:" + e);
	        }
    	}
    }
    
    public void setSocket(Socket s){
    	clientSocket = s;
    	try{
    		out = new DataOutputStream(clientSocket.getOutputStream());
    	    reader = new BufferedReader(new InputStreamReader(System.in));
        }catch(IOException e){System.out.println("Connection:" + e.getMessage());}
    }
	
}