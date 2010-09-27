import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientWriteTCP extends Thread {
	//This thread will be responsible for handling problems with the link to the server.
	String user=null,pass=null;
	boolean loggedIn=false;
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

    public String printMenu(){    	
    	return "\tMenu:\n-> Show the current credit of the user: show credits" +
    			"\n-> Reset user credits to 100Cr: reset" +
    			"\n-> View Current Matches: show matches" +
    			"\n-> Make a Bet: bet [match number] [1 x 2] [credits]" +
    			"\n-> Show Online Users: show users" +
    			"\n-> Send messagen to specific user: send [user] '[message]'" +
    			"\n-> Send message to all users: send all '[message]'";    	
    }
    
    //=============================
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
	
	        	System.out.println(printMenu());
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
