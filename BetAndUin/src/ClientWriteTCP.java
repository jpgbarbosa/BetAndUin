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
    
    String login=null;
    
    BufferedReader reader;
    
    public ClientWriteTCP (ConnectionLock lock) {
    	connectionLock = lock;
        this.start();
    }
    
    public String printMenu(){    	
    	return "\tMenu:\n-> 1- Show credits: shows the current credit of the user." +
    			"\n-> 2- Reset: users credit are defaulted to 100 cr" +
    			"\n-> 3- View Current Matches" +
    			"\n-> 4- Bet: bet [match number] [1 x 2] [credits]" +
    			"\n-> 5- Online Users: show users logged ing" +
    			"\n-> 6- Message User: send messagen to specific user" +
    			"\n-> 7- Message All: send message to all users\n";    	
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
	            while(true){
	            	System.out.println(printMenu());
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
