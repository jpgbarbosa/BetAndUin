package server;

import java.net.*;
import java.util.StringTokenizer;
import java.io.*;

/*TODO: There's a major bug in this moment. As we are starting several threads and only then, 
 *      we are trying to create the socket, if we accidentally run two servers in the same ports,
 *      the main thread will eventually die and detect the error, raising the port already in use exception,
 *      but the other threads will keep going. We have to take a look at this.
 */

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
        ClientsStorage database = new ClientsStorage();
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

        if (debugging){
        	System.out.printf("We are server %d, our partner is %d.\n", serverPort, partnerPort);
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
            			if (debugging){
            				System.out.println("We are temporarily sleeping while the primary server is elected...");
            			}
            			/* If this server is elected as secondary server, we won't move from here till
            			 * our status changes once again.
            			 */
            			changeStatusLock.wait();
            		}
				} catch (InterruptedException e) {
					/*We have been awaken by the connection manager, keep going. */
				}
    		}
    		
    		if (debugging){
				System.out.println("We are moving to the next step...");
			}
    		
    		/* Now, we have to check whether we are the primary server or not. */
    		
    		/* We are not the primary server, so we are going to sleep and not attend any clients.
    		 * We will eventually be awaken if our status changes.
    		 */
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
    		
    		/* We now create the database. */
    		database.addClient("gaia", "fixe", "barbosa");
    		database.addClient("ivo", "fixe", "correia");
    		
    		
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
                new ConnectionChat(clientSocket, activeClients, betScheduler, database);
            }
        }catch(IOException e){
        	System.out.println("Listen:" + e.getMessage());
        }
    }
}
/* Thread used to take care of each communication channel between the active server and a given client. */
class ConnectionChat extends Thread {
	/*Set to true if you want the program to display debugging messages.*/
	Boolean debugging = false;
	
	/* The betScheduler so we can send the matches' information back to the client. */
	BetScheduler betScheduler; 
	/* A pointer to the information block related to this client. */
	ClientInfo clientInfo;
	
	/* This two variables keep the values inserted by the user, so we can use it later. */
	String username, password;
	
    DataInputStream in;
    Socket clientSocket;
    ActiveClients activeClients;
    ClientsStorage database;

    public ConnectionChat (Socket aClientSocket, ActiveClients activeClients, BetScheduler betScheduler, ClientsStorage database) {
        this.betScheduler=betScheduler;
        this.database = database;
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
                	username = strToken.nextToken();
                	password = strToken.nextToken();
                	String mail = strToken.nextToken();
                	
                	/* This username hasn't been found in the database, so we can add this new client. */
                	if (database.findClient(username) == null){
                		/* Creates the register for this new client and adds it to the database. */
                		 clientInfo = database.addClient(username, password, mail);
                		
                		/* Registers in the client as an active client and informs the success of the
                		 * operation.
                		 */
                		activeClients.addClient(clientInfo.getUsername(), clientSocket);
                		activeClients.sendMessageBySocket("log successful",clientSocket);
                		loggedIn = true;
                	}
                	/* This username is already being used. */
                	else{
                		/* Resets the variables and writes to the client. */
                		username = "";
                		password = "";
                		activeClients.sendMessageBySocket("log taken",clientSocket);
                	}
                	
                } else if(option.equals("login")){
	                /*needs to be fixed: server will search at the registered clients (SQL database or in some
	                 * memory struct)
	                 * if the username do not exist it must be registered first. if not it will be
	                 * added to the activeClients*/
                	ClientInfo client;
                	username = strToken.nextToken();
                	password = strToken.nextToken();
                	
                	client = database.findClient(username);
                	/* This username hasn't been found on the database. */
                	if (client == null){
                		activeClients.sendMessageBySocket("log error",clientSocket);
                	}
                	/* This username has been found on the database. Let's check if the password matches
                	 * with it.
                	 */
                	else{
		                if(password.equals(client.getPassword())){
		                	/* However, the user was already validated in some other machine. */
		                	if (activeClients.isClientLoggedIn(username)){
		                		activeClients.sendMessageBySocket("log repeated",clientSocket);
		                	}
		                	/* The validation process can be concluded. */
		                	else{
		                		loggedIn=true;
		                		clientInfo = client;
		                		activeClients.addClient(username, clientSocket);
		                		activeClients.sendMessageUser("log successful",username);
		                	}
		                }
                	}
                }
        	}
            while(true){
                /* Now, the server can communicate with the client, receiving the requests
                 * and sending back the respective information.
                 */
                String clientInput = in.readUTF();
                System.out.println("T[" + username + "] has received: " + clientInput);
                /* Interprets the commands sent by the client and performs the respective
                 * action.
                 */
                String data = parseFunction(clientInput);
                /* Replies to the client. */
                /* We only reply if data != "". If we received "", it means we were sending a message
                 * to all the users.
                 */
                if (!data.equals("")){
                	activeClients.sendMessageBySocket(data, clientSocket);
                }
            }
        }catch(EOFException e){
        	/* The client is leaving. Consequently, we have to remove it from the list
        	 * of active clients.
        	 */
        	activeClients.removeClient(username);
        	if (debugging){
        		System.out.println("ConnectionChat EOF:" + e);
        	}
        }catch(IOException e){
        	/* The client is leaving. Consequently, we have to remove it from the list
        	 * of active clients.
        	 */
        	/*TODO: we must save the user's current state, i.e., current bets and all finished bets
        	 * during this last session.*/
        	activeClients.removeClient(username);
        	if (debugging){
        		System.out.println("ConnectionChat IO:" + e);
        	}
        	/*TODO: When user is offline, we should also record messages of finished games, 
        	 * where user made some bets, and non-delivered messages from other users so that, 
        	 * in the next session, the user can check this informations.*/
        }
    }
    
    /* The parsing function. Given a request from a client, this thread must recognized the commands
     * and get the right information, so the thread can send it to the client.
     */
    public String parseFunction(String input){
    	String answer = "";
    	String command;
    	/* So we can keep the original String and don't destroy it with the tokenizer. */
    	String copyInput = input;
    	
    	StringTokenizer strToken;
        strToken = new StringTokenizer(copyInput);
        command = strToken.nextToken();
        
        if(command.equals("show")){
        	command = strToken.nextToken();
        	
        	if(command.equals("matches")){ //show all current matches
        		answer = betScheduler.getMatches();
        	}
        	else if(command.equals("credits")){ //show user's credits
        		answer = "" + clientInfo.getCredits();
        	}
        	else if(command.equals("users")){ //show all active users
        		answer = activeClients.getUsersList();
        	}
        	else{
        		answer = "Unknow Command";
        	}
        }
        else if(command.equals("send")){
        	command=strToken.nextToken();
        	
        	if(command.equals("all")){ //send a message to all users
            	/* "send all " occupies 9 characters. Consequently, the message goes from
            	 * input[9] to the size of the input.
            	 */
        		activeClients.sendMessageAll(input.substring(9), clientSocket);
        		answer = "";
        	}
        	/* We are sending a message to a user. */
        	else{
        		/* Send a message to a specific user. */
        		
        		/* This client is online. */
	        	if(activeClients.checkUser(command)){
	        		int size = command.length();
	            	/* "send " occupies 5 characters. Adding the size of 'command' to it, we have to cut
	            	 * that part to get the message to send.
	            	 * The token command corresponds to the user.
	            	 */
	            	activeClients.sendMessageUser(input.substring(5 + size), command);
	            	answer = "";
	        	}
	        	else if(database.findClient(command) != null){
	        		answer = "This client if offline at the moment.";
	        	}
	        	else{
	        		answer = "Username not registered.";
	        	}
        	}
        }
        else if(command.equals("reset")){ //resets user's credits to 100Cr
        	clientInfo.setCredits(100);
        	answer = "Your credits were reseted to " + clientInfo.getCredits() + "Cr";
        	database.saveToFile();
        }
        else if(command.equals("bet")){

        	String game;
        	int gameNumber,credits;
        	try {
        		gameNumber = Integer.parseInt(strToken.nextToken());
        	    game = strToken.nextToken();
        	    credits = Integer.parseInt(strToken.nextToken());
        	    
        	}
        	catch(NumberFormatException nFE) {
        	    System.out.println("Not an Integer");
        	    answer = "Invalid game number or amount of credits!";
        	    return answer;
        	}
        	        	
        	if((game.equals("1") || game.compareToIgnoreCase("x")==0 || game.equals("2"))
        			/*&& betScheduler.isValidGame(x1)*/){
        		
        		clientInfo.setCredits(clientInfo.getCredits()-credits);
        		database.betList.add(new Bet(clientInfo.getUsername(),gameNumber,game,credits));
        		database.saveToFile();
        		
        		
        		
        		
        		answer = "Bet done!";
        	}
        	else {
        		answer = "Invalid command";
        	}
        }
        else {
        	answer = "Unknown command";
        }
    	
		return answer;
    }
}
