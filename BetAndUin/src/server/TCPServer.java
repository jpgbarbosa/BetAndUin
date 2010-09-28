package server;
// TCPServer2.java: Multithreaded server

import java.net.*;
import java.util.StringTokenizer;
import java.io.*;

public class TCPServer{
    public static void main(String args[]){
        int numero=0;
        ActiveClients activeClients;
        BetScheduler betScheduler;
        ConnectionWithServerManager connectionWithServerManager;
        Boolean isPrimaryServer;
        int serverPort, partnerPort;
        
        /* A testing variable, used when we want to disableBets so we won't get all those messages.*/
        boolean disableBets = true;
        
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
            
            activeClients = new ActiveClients();
            System.out.println("A Escuta no Porto " + serverPort);
            ServerSocket listenSocket = new ServerSocket(serverPort);
            System.out.println("LISTEN SOCKET="+listenSocket);
            
            /* We can take this off later.*/
            if (!disableBets){
            	betScheduler = new BetScheduler(activeClients);
            }
            else{
            	betScheduler = null;
            }
            connectionWithServerManager = new ConnectionWithServerManager(serverPort, partnerPort, isPrimaryServer);
            
            while(true) {
                Socket clientSocket = listenSocket.accept(); // BLOQUEANTE
                System.out.println("CLIENT_SOCKET (created at accept())="+clientSocket);
                numero ++;
                new ConnectionChat(clientSocket, numero, activeClients, betScheduler = null);
            }
        }catch(IOException e)
        {System.out.println("Listen:" + e.getMessage());}
    }
}
/* Thread used to take care of each communication channel between the active server and a given client. */
class ConnectionChat extends Thread {
	/* The betScheduler so we can send the matches' information back to the client. */
	BetScheduler betScheduler;
	
	String user="gaia",pass="fixe";
	
	/* This two variables keep the values inserted by the user, so we can use it later. */
	String username, password;
	
	boolean loggedIn=false;
    DataInputStream in;
    Socket clientSocket;
    int thread_number;
    ActiveClients activeClients;
    
    public ConnectionChat (Socket aClientSocket, int numero, ActiveClients activeClients, BetScheduler betScheduler) {
        thread_number = numero;
        this.betScheduler=betScheduler;
        try{
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            this.activeClients = activeClients;
            this.start();
        }catch(IOException e){System.out.println("Connection:" + e.getMessage());}
    }

    //=============================
    public void run(){
        try{
        	/* Performs login authentication. */
        	while(!loggedIn){
        		
        		StringTokenizer strToken;
            	String userInfo;
                
                userInfo = in.readUTF();
                strToken = new StringTokenizer (userInfo);
                
                /* Collects the information sent by the client. */
                username = strToken.nextToken();
                password = strToken.nextToken();
                
                /* It's a valid login. */
                if(username.equals(user) && password.equals(pass)){
                	
                	/* However, the user was already validated in some other machine. */
                	if (activeClients.isClientLoggedIn(username)){
                		activeClients.sendMessageBySocket("log repeated",clientSocket);
                	}
                	/* The validation process can be concluded. */
                	else{
                		loggedIn=true;
                		activeClients.addClient(username, clientSocket);
                		activeClients.sendMessageUser("log successful",username);
                	}
                	
                }
                /* This user isn't registered in the system. */
                else{
                	activeClients.sendMessageBySocket("log error",clientSocket);
                }
        	}
            while(true){
                /* Now, the server can communicate with the client, receiving the requests
                 * and sending back the respective information.
                 */
                String data = in.readUTF();
                System.out.println("T["+thread_number + "] Recebeu: "+data);
                //TODO: parseFunction(data)...
                /*synchronized (activeClients){
                	activeClients.sendMessageUser(data, username);
                }*/
            }
        }catch(EOFException e){
        	/* The client is leaving. Consequently, we have to remove it from the list
        	 * of active clients.
        	 */
        	activeClients.removeClient(username);
        	System.out.println("EOF in here:" + e);
        }catch(IOException e){
        	/* The client is leaving. Consequently, we have to remove it from the list
        	 * of active clients.
        	 */
        	activeClients.removeClient(username);
        	System.out.println("IO:" + e);
        }
    }
    
    /* The parsing function. Given a request from a client, this thread must recognized the commands
     * and get the right information, so the thread can send it to the client.
     */
    public String parseFunction(String input){
    	String answer = "";
    	String command;
    	
    	StringTokenizer strToken;
        strToken = new StringTokenizer(input);
        command = strToken.nextToken();
        
        if(command.equals("show")){
        	command = strToken.nextToken();
        	
        	if(command.equals("matches")){
        		activeClients.sendMessageAll(betScheduler.getMatches(), clientSocket);
        	}
        	else if(command.equals("credits")){
        		//TODO: por o resultado numa string e devolver
        	}
        	else if(command.equals("users")){
        		//TODO: por o resultado numa string e devolver
        	}
        	else{
        		answer = "Unknow Command";
        	}
        }
        else if(command.equals("send")){
        	command = "";
        	
        	while(strToken.countTokens() - 1 > 0){
        		command += strToken.nextToken() + " ";
        	}
        	
        	command += strToken.nextToken();
        	
        	if(command.equals("all")){
        		activeClients.sendMessageAll(command, clientSocket);
        	}
        	else if(false/*checkUser(temp)*/){
        		//TODO: verificar se o cliente existe e devolver o socket possivelmente
        	}
        	else{
        		answer = "Invalid Command or user Unknow";
        	}
        }
        else if(command.equals("reset")){
        	//TODO: faz o reset
        	answer = "Your credits were reseted to ";//+user.credits+"Cr";
        }
        else if(command.equals("bet")){
        	//TODO: check if next token is integer, collect the remaining infos check them 
        	//if successful result="bet done!"
        }
        else {
        	answer = "Unknown command";
        }
    	
		return answer;
    }
}
