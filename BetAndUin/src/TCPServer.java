// TCPServer2.java: Multithreaded server
import java.net.*;
import java.io.*;

public class TCPServer{
    public static void main(String args[]){
        int numero=0;
        ThreadCounter threadArray;
        BetScheduler betScheduler;
        ConnectionWithServerManager connectionWithServerManager;
        Boolean isPrimaryServer;
        int serverPort, partnerPort;
        
        if (args.length < 3){
        	System.out.println("java TCPServer serverPort partnerPort isPrimaryServer (for this last" +
        			"option, type primary or secondary");
    	    System.exit(0);
        }
        
        serverPort = Integer.parseInt(args[0]);
        partnerPort = Integer.parseInt(args[1]);
        if (args[2].toLowerCase().equals("primary")){
        	isPrimaryServer = true;
        }
        else{
        	isPrimaryServer = false;
        }
        try{
            
            threadArray = new ThreadCounter(10);
            System.out.println("A Escuta no Porto " + serverPort);
            ServerSocket listenSocket = new ServerSocket(serverPort);
            System.out.println("LISTEN SOCKET="+listenSocket);
            
            //IMPORTANT: We are temporarily disabling the bets!!!
            //betScheduler = new BetScheduler(threadArray);
            connectionWithServerManager = new ConnectionWithServerManager(serverPort, partnerPort, isPrimaryServer);
            
            while(true) {
                Socket clientSocket = listenSocket.accept(); // BLOQUEANTE
                System.out.println("CLIENT_SOCKET (created at accept())="+clientSocket);
                numero ++;
                new ConnectionChat(clientSocket, numero, threadArray);
                synchronized (threadArray){
                	threadArray.insertSocket(clientSocket);
                }
            }
        }catch(IOException e)
        {System.out.println("Listen:" + e.getMessage());}
    }
}
//= Thread para tratar de cada canal de comunicação com um cliente
class ConnectionChat extends Thread {
    DataInputStream in;
    Socket clientSocket;
    int thread_number;
    ThreadCounter threadArray;
    
    public ConnectionChat (Socket aClientSocket, int numero, ThreadCounter threadArray) {
        thread_number = numero;
        try{
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            this.threadArray = threadArray;
            this.start();
        }catch(IOException e){System.out.println("Connection:" + e.getMessage());}
    }
    //=============================
    public void run(){
        try{
            while(true){
                //an echo server
                String data = in.readUTF();
                System.out.println("T["+thread_number + "] Recebeu: "+data);
                synchronized (threadArray){
                	threadArray.sendMessage(data, clientSocket);
                }
            }
        }catch(EOFException e){System.out.println("EOF:" + e);
        }catch(IOException e){System.out.println("IO:" + e);}
    }
}

class ThreadCounter {
	//We ought to create a list if we want to expand the capacity of the structure.
	Socket []threadArray;
	int counter;
	DataOutputStream out;
	
	public ThreadCounter(int no){
		threadArray = new Socket[no];
		counter = 0;
	}
	
	public void insertSocket(Socket s){
		threadArray[counter] = s;
		counter++;
	}
	
	public void sendMessage(String message, Socket clientSocket){
		int i;
		try{
			for (i = 0; i < counter; i++){
				//Verifies if we aren't forwarding the message to the sender.
				if (threadArray[i] != clientSocket){
					out = new DataOutputStream(threadArray[i].getOutputStream());
					out.writeUTF(message);
				}
			}
		}catch(Exception e){System.out.println("ERROR");}
	}
}