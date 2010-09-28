package server;
// TCPServer2.java: Multithreaded server

import java.net.*;
import java.util.StringTokenizer;
import java.io.*;

import client.ClientInfo;

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
	ClientInfo clientInfo; //Will be easier to access client's info. -> this is just a reference
	/*TODO: ATTENTION!!! we must initialize clientInfo once database is running.*/
	
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
                
                /*needs to be fixed: server will search at the registered clients (SQL database or in some
                 * memory struct)
                 * if the username do not exist it must be registered first. if not it will be
                 * added to the activeClients*/
                if((username = strToken.nextToken()).equals(user)
                		&& (password = strToken.nextToken()).equals(pass)){
                	
                	loggedIn=true;
                	activeClients.addClient(username, clientSocket);
                	activeClients.sendMessageUser("log successful",username);
                	
                }
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
        	/*TODO: Verify if when the closed is closed, this won't be executed too.
        	 * 		If it does in any occassion, we also have to remove the clients
        	 * 		here.
        	 */
        	System.out.println("EOF in here:" + e);
        }catch(IOException e){
        	/* The client is leaving. Consequently, we have to remove it from the list
        	 * of active clients.
        	 */
        	/*TODO: we must save the user's current state, i.e., current bets and all finished bets
        	 * during this last session.*/
        	activeClients.removeClient(username);
        	System.out.println("IO:" + e);
        	/*TODO: When user is offline, we should also record messages of finished games, 
        	 * where user made some bets, and non-delivered messages from other users so that, 
        	 * in the next session, the user can check this informations*/
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
        	
        	if(command.equals("matches")){ //show all current matches
        		activeClients.sendMessageAll(betScheduler.getMatches(), clientSocket);
        	}
        	else if(command.equals("credits")){ //show user's credits
        		activeClients.sendMessageBySocket(""+clientInfo.getCredits(), clientSocket);
        	}
        	else if(command.equals("users")){ //show all active users
        		activeClients.sendMessageBySocket(activeClients.getUsersList(), clientSocket);
        	}
        	else{
        		answer = "Unknow Command";
        	}
        }
        else if(command.equals("send")){
        	command=strToken.nextToken();
        	
        	if(command.equals("all")){ //send a message to all users
            	command = "";
            	
            	while(strToken.countTokens() - 1 > 0){
            		command += strToken.nextToken() + " ";
            	}
            	command += strToken.nextToken();
            	
        		activeClients.sendMessageAll(command, clientSocket);
        	}
        	else if(activeClients.checkUser(command)){ //send a message to a specific user
        		String user=command;
            	command = "";
            	
            	while(strToken.countTokens() - 1 > 0){
            		command += strToken.nextToken() + " ";
            	}
            	command += strToken.nextToken();
            	
            	activeClients.sendMessageUser(command, user);
        	}
        	else{
        		answer = "Invalid Command or user Unknow";
        	}
        }
        else if(command.equals("reset")){ //resets user's credits to 100Cr
        	clientInfo.setCredits(100);
        	answer = "Your credits were reseted to "+clientInfo.getCredits()+"Cr";
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
