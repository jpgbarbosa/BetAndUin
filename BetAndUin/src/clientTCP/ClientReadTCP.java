package clientTCP;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;



public class ClientReadTCP extends Thread {
	DataInputStream in;
    Socket clientSocket;
    ConnectionLock connectionLock;
    ClientWriteTCP writeThread;
    /* This variable is used when the user tries to reset the number of credits.
     * The other thread will ask for the server to inform the client system about
     * the amount of credits it has in order to prevent (or better saying, inform)
     * the client from losing credits. Consequently, we can't print to the screen
     * the information related to this step that is transparent to the end user.
     */
    boolean isToPrint;
    
    public ClientReadTCP (ConnectionLock lock, ClientWriteTCP thread) {
    	connectionLock = lock;
        this.start();
        isToPrint = true;
        writeThread = thread;
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
								System.out.println("The ClientReadTCP thread has been interrupted.");
							}
		        		}
		        	}
	                //an echo server
	                String data = in.readUTF();
	                /* This operation was asked by the internals of the system
	                 * and not directly by the user.
	                 */
	                if (!isToPrint){
	                	int credits;
	                	try{
	                		credits = Integer.parseInt(data);
	                		isToPrint = true;
	                		writeThread.setUserCredtis(credits);
	                    	writeThread.interrupt();
	                	}catch(Exception e){
	                		/* This isn't the number of credits. */
	                		System.out.println(data);
	                	}
	                
	                }
	                else{
	                	System.out.println(data);
	                }
	                
	            }
	        }catch(EOFException e){
	        	System.out.println("EOF in ClientReadTCP:" + e);
	        }catch(IOException e){
	        	System.out.println("IO in ClientReadTCP:" + e);
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
        }catch(IOException e){
        	System.out.println("Connection in ClientReadTCP:" + e.getMessage());
        }
    }
    
    public void setIsToPrint(boolean value){
    	isToPrint = value;
    }
}

