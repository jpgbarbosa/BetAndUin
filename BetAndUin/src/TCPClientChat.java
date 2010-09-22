import java.net.*;
import java.io.*;


public class TCPClientChat {
    public static void main(String args[]) {
	// args[0] <- hostname of destination
	if (args.length == 0) {
	    System.out.println("java TCPClient hostname");
	    //System.exit(0);
	}

	/*Set to true if you want the program to display debugging messages.*/
	Boolean debugging = false;
	
	/*The socket variable we shall use to connect to the server.*/
	Socket s = null;
	
	/*The variables related to the reconnection.*/
	int retries = 0; //The numbers of times we reconnected already to a given port.
	int retrying = 0; //Tests if we are retrying for the first or second time.
	int WAITING_TIME = 1000; //The time the thread sleeps.
	int NO_RETRIES = 10; //The maximum amount of retries for a given port.
	
	/*The thread related variables.*/
	Lock lock = new Lock();
	WriteChat writeThread;
	ReadChat readThread;
	
	/*The variables related to the server ports available.*/
	int []serverPorts = new int[2]; //The array with the two different ports.
	int serverPos = 0; //The position array, which corresponds to active port.
	//Places the two ports in the array.
	serverPorts[0] = 6000;
	serverPorts[1] = 7000;
	
	writeThread =  new WriteChat(lock);
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
			s = new Socket("localHost", serverPorts[serverPos]);
			
			if (debugging){
				System.out.println("SOCKET=" + s);
			}
		    
		    writeThread.setSocket(s);
		    readThread = new ReadChat(s,lock);
		    
		    // 3o passo
	
		    //Resets the counters and the lock flags.
		    synchronized(lock){
		    	lock.setConnectionDown(false);
		    	lock.notify();
		    }
			retries = 0;
			retrying = 0;
		    
		    try{
		    	readThread.join();
		    }catch (InterruptedException e) {
		    	if (debugging){
		    		System.out.println("Thread Exception.\n");
		    	}
		    }
		    
		} catch (UnknownHostException e) {
			if (debugging){
				System.out.println("Sock:" + e.getMessage());
			}
		} catch (EOFException e) {
			if (debugging){
				System.out.println("EOF:" + e.getMessage());
			}
		} catch (IOException e) {
			if (debugging){
				System.out.println("IO:" + e.getMessage());
			}
		    retries++;
		    /* In this case, it's the first connection, and the server is already down.
		     * So, we will pass immediately to the next one and retry this one later.
		     */
		    if (retries == 1 && retrying == 0){
		    	serverPos = (++serverPos)%2;
		    	System.out.println("Trying to connect to server in port " + serverPorts[serverPos] + ".");
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
		    	System.out.println("Trying to connect to server in port " + serverPorts[serverPos] + ".");
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
	
		/* Talvez temos de alterar isto, para deixar o programa a correr indefinidamente
		 * até que o utilizador prima o Ctr+C.
		 */
		System.out.println("Exited");
		System.exit(0);
    }
    
}

class ReadChat extends Thread {
	DataInputStream in;
    Socket clientSocket;
    Lock lock;
    
    public ReadChat (Socket aClientSocket, Lock lock) {
        try{
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            this.lock = lock;
            this.start();
        }catch(IOException e){System.out.println("Connection:" + e.getMessage());}
    }
    //=============================
    public void run(){
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
        	synchronized(lock){
        		lock.setConnectionDown(true);
        	}
        	System.out.println("The server is down. Please wait while we try to reconnect...");
        }
    }
}

class WriteChat extends Thread {
	//This thread will be responsible for handling problems with the link to the server.
    DataOutputStream out;
    Socket clientSocket;
    String userInput;
    int serverSocketFirst = 6000, serverSocketSecond = 7000;
    Lock lock;
    
    BufferedReader reader;
    
    public WriteChat (Lock lock) {
    	this.lock = lock;
        this.start();
    }
    //=============================
    public void run(){
    	while (true){
	        try{
	        	synchronized(lock){
	        		while (lock.isConnectionDown()){
	        			try {
	        				System.out.println("Here!");
							lock.wait();
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
	        	return;
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

class Lock{
	Boolean connectionDown;
	
	public Lock(){
		connectionDown = true;
	}
	
	public Boolean isConnectionDown(){
		return connectionDown;
	}
	
	public void setConnectionDown(Boolean value){
		connectionDown = value;
	}
}