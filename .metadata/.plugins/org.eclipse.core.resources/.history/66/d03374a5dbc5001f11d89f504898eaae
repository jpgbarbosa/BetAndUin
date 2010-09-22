import java.net.*;
import java.io.*;


public class TCPClientChat {
    public static void main(String args[]) {
	// args[0] <- hostname of destination
	if (args.length == 0) {
	    System.out.println("java TCPClient hostname");
	    //System.exit(0);
	}

	Socket s = null;
	int serverPos = 0;
	int []serverPorts = new int[2];
	int serversocket;
	ReadChat readThread;
	WriteChat writeThread;
	int retries = 0;
	int retrying = 0;
	int WAITING_TIME = 1000;
	int NO_RETRIES = 10;
	
	serverPorts[0] = 6000;
	serverPorts[1] = 7000;
	serversocket = serverPorts[serverPos];
	
	//Five attemps to reconnect the connection.
	while (retries < NO_RETRIES){
		try {
			
			/* We haven't retried yet, so, it's useless to sleep for WAITING_TIME miliseconds. */
			if (retrying > 0){
			    try {
					Thread.sleep(WAITING_TIME);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(0);
				}
			}
		    // 1o passo
		    //s = new Socket(args[0], serversocket);
			s = new Socket("localHost", serversocket);
	
		    System.out.println("SOCKET=" + s);
		    
		    writeThread =  new WriteChat(s);
		    readThread = new ReadChat(s);
		    // 3o passo
	
		    try{
			    readThread.join();
		    	writeThread.join();
		    	System.out.println("Waited for all the threads.");
		    }catch (InterruptedException e) {
			    System.out.println("Thread Exception.\n");
		    }
		    
		    //Resets the counters.
			retries = 0;
			retrying = 0;
		    
		} catch (UnknownHostException e) {
		    System.out.println("Sock:" + e.getMessage());
		} catch (EOFException e) {
		    System.out.println("EOF:" + e.getMessage());
		} catch (IOException e) {
		    System.out.println("IO:" + e.getMessage());
		    retries++;
		    /* In this case, it's the first connection, and the server is already down.
		     * So, we will pass immediately to the next one and retry this one later.
		     */
		    if (retries == 1 && retrying == 0){
		    	serverPos = (++serverPos)%2;
		    	serversocket = serverPorts[serverPos];
		    	System.out.println("Trying to connect to server in port " + serversocket + ".");
		    }
		    /* We have retried the connection at least once.
		     * Consequently, the thread shall wait WAITING_TIME miliseconds 
		     * when restarts the while cycle.
		     */
		    else if (retrying == 0){
		    	retrying = 1;
		    }
		    /* We have completed one round of retries for one server.
		     * Therefore, we shall try now the second server.
		     */
		    else if (retries == 10 && retrying == 1){
		    	//Resets the number of retries and passes to the serverPort of the other server.
		    	retries = 0;
		    	serverPos = (++serverPos)%2;
		    	serversocket = serverPorts[serverPos];
		    	System.out.println("Trying to connect to server in port " + serversocket + ".");
		    	retrying++;
		    }
		} finally {
		    if (s != null)
			try {
			    s.close();
			} catch (IOException e) {
			    System.out.println("close:" + e.getMessage());
			}
		}
		}
	
		System.out.println("Exited");
    }
    
}

class ReadChat extends Thread {
	DataInputStream in;
    Socket clientSocket;
    
    public ReadChat (Socket aClientSocket) {
        try{
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            this.start();
        }catch(IOException e){System.out.println("Connection:" + e.getMessage());}
    }
    //=============================
    public void run(){
    	while(true){
	        try{
	            while(true){
	                //an echo server
	                String data = in.readUTF();
	                System.out.println("MENSAGEM RECEBIDA:\n   "+data + "\nFIM DA MENSAGEM\n");
	            }
	        }catch(EOFException e){
	        	System.out.println("EOF:" + e);
	        }catch(IOException e){
	        	System.out.println("IO:" + e);
	        	System.out.println("The server is down. Please press enter to retry connecting to it.");
	        	return;
	        }
    	}
    }
}

class WriteChat extends Thread {
	//This thread will be responsible for handling problems with the link to the server.
    DataOutputStream out;
    Socket clientSocket;
    String userInput;
    int serverSocketFirst = 6000, serverSocketSecond = 7000;
    
    BufferedReader reader;
    
    public WriteChat (Socket aClientSocket) {
        try{
            clientSocket = aClientSocket;
            out = new DataOutputStream(clientSocket.getOutputStream());
    	    reader = new BufferedReader(new InputStreamReader(System.in));
            this.start();
        }catch(IOException e){System.out.println("Connection:" + e.getMessage());}
    }
    //=============================
    public void run(){
    	while (true){
	        try{
	            while(true){
	            	userInput = reader.readLine();
	                out.writeUTF(userInput);
	            }
	        }catch(EOFException e){
	        	System.out.println("EOF:" + e);
	        }catch(IOException e){
	        	System.out.println("IO:" + e);
	        	return;
	        }
    	}
    }
	
}