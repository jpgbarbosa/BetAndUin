package server;
// TCPServer2.java: Multithreaded server

import java.net.*;
import java.util.StringTokenizer;
import java.io.*;

public class TCPServer{
    public static void main(String args[]){
    	
    	/*Set to true if you want the program to display debugging messages.*/
		Boolean debugging = true;
		
    	/* The object responsible for dealing with the information related to the clients logged in the system. */
        ActiveClients activeClients;
        /* The object responsible for creating the matches and setting the results. */
        BetScheduler betScheduler;
        /* The object responsible for maintaining the communication with the partner server. */
        ConnectionWithServerManager connectionWithServerManager;
        /* The object responsible for maintaining the clients' database. */
        ClientsStorage database;
        /* The lock we are going to use when the connection manager wants to inform that server that its status
         * (i.e. primary or secondary server) has changed.
         */
        ChangeStatusLock changeStatusLock = new ChangeStatusLock();
        /* Variable to know whether we are the default server or not. */
        boolean isDefaultServer;
        /* The ports of the two servers. */
        int serverPort, partnerPort;
        
        /* A testing variable, used when we want to disableBets so we won't get all those messages.*/
        boolean disableBets = true;
        
        /* The user has introduced less than three options by the command line, so we can't carry on. */
        if (args.length < 3){
        	System.out.println("java TCPServer serverPort partnerPort isPrimaryServer (for this last" +
        			"option, type primary or secondary");
    	    System.exit(0);
        }
        
        /* We read from the command line the two port numbers passed. */
        serverPort = Integer.parseInt(args[0]);
        partnerPort = Integer.parseInt(args[1]);
        if (args[2].toLowerCase().equals("primary")){
        	isDefaultServer = true;
        }
        else{
        	isDefaultServer = false;
        }
        
        try{
            
            connectionWithServerManager = new ConnectionWithServerManager(serverPort, partnerPort, isDefaultServer, changeStatusLock);
    		/* Before going to wait, we have to see whether the manager has concluded its operations
    		 * or not yet. Otherwise, we may wait forever if it concluded before we entered here.
    		 * This step is used in order not to accept any clients before we know whether we are
    		 * the primary server or not.
    		 */
    		synchronized(changeStatusLock){
    			try{
            		if (!changeStatusLock.isInitialProcessConcluded()){
            			changeStatusLock.wait();
            		}
				} catch (InterruptedException e) {
					/*We have been awaken by the connection manager, keep going. */
				}
    		}
    		
    		/* Now, we have to check whether we are the primary server or not. */
    		
    		/* We are not the primary server, so we are going to sleep and not attend any clients.
    		 * We will eventually be awaken if our status changes.
    		 */
    		//TODO: We have to be assure about this part!
    		synchronized (changeStatusLock){
	    		while (!changeStatusLock.isPrimaryServer()){
	    			try{
	    				if (debugging){
							System.out.println("The server is going to sleep...");
						}
	            		changeStatusLock.wait();
					} catch (InterruptedException e) {
						/*We have been awaken by the connection manager, keep going.*/
						if (debugging){
							System.out.println("The server has been awakened!");
						}
					}
	    		}
    		}
    		
            ServerSocket listenSocket = new ServerSocket(serverPort);
            if (debugging){
            	System.out.println("Listening at port  " + serverPort);
            	System.out.println("LISTEN SOCKET=" + listenSocket);
            }
    		
    		/* We are the primary server, so we can communicate with clients. */
            
    		activeClients = new ActiveClients();
    		
            /* We can take this off later.*/
            if (!disableBets){
            	betScheduler = new BetScheduler(activeClients);
            }
            else{
            	betScheduler = null;
            }

            while(true) {
                Socket clientSocket = listenSocket.accept(); // BLOQUEANTE
                
                if (debugging){
                	System.out.println("CLIENT_SOCKET = " + clientSocket);
                }
                new ConnectionChat(clientSocket, activeClients, betScheduler = null);
            }
        }catch(IOException e){
        	System.out.println("Listen:" + e.getMessage());
        }
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
	

    DataInputStream in;
    Socket clientSocket;
    ActiveClients activeClients;

    public ConnectionChat (Socket aClientSocket, ActiveClients activeClients, BetScheduler betScheduler) {
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
    	boolean loggedIn = false;
    	
        try{
        	/* Performs login authentication. */
        	while(!loggedIn){
        		
        		StringTokenizer strToken;
            	String option, userInfo;
                
                userInfo = in.readUTF();
                strToken = new StringTokenizer (userInfo);
                	
                option = strToken.nextToken();
                System.out.println(option);
                if(option.equals("register")){
                	ClientInfo client = new ClientInfo(strToken.nextToken(),strToken.nextToken(),
                			strToken.nextToken(), 100);
                	
                	/*TODO: Adicionar à lista de clientes!!!!*/
                	
                	activeClients.addClient(client.getUsername(), clientSocket);
                	
                	activeClients.sendMessageUser("log successful",username);
                } else if(option.equals("login")){
	                /*needs to be fixed: server will search at the registered clients (SQL database or in some
	                 * memory struct)
	                 * if the username do not exist it must be registered first. if not it will be
	                 * added to the activeClients*/
	                if((username = strToken.nextToken()).equals(user)
	                		&& (password = strToken.nextToken()).equals(pass)){
	
	                	
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
	                else{
	                	activeClients.sendMessageBySocket("log error",clientSocket);
	                }
                }
        	}
            while(true){
                /* Now, the server can communicate with the client, receiving the requests
                 * and sending back the respective information.
                 */
                String data = in.readUTF();
                System.out.println("T["+ username + "] has received: "+data);
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
