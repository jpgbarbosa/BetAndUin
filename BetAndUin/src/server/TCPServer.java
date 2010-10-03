package server;

import java.net.*;
import java.io.*;

/*TODO: There's a major bug in this moment. As we are starting several threads and only then, 
 *      we are trying to create the socket, if we accidentally run two servers in the same ports,
 *      the main thread will eventually die and detect the error, raising the port already in use exception,
 *      but the other threads will keep going. We have to take a look at this.
 */

public class TCPServer{
    public static void main(String args[]){
    	/*Number of Games per round*/
    	int nGames = 10; 
    	
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
        boolean disableBets = false;
        
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
    		//TODO: Remove these when the project is over!
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
            	betScheduler = new BetScheduler(activeClients, nGames);
            }
            else{
            	betScheduler = null;
            }

            database.setBetScheduler(betScheduler);
            
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
    
    int defaultCredits = 100;

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
        		String [] stringSplitted;

                /* Reads and splits the input. */
                stringSplitted = in.readUTF().split(" ");
                	
                if(stringSplitted.length == 4 && stringSplitted[0].equals("register")){
                	username = stringSplitted[1];
                	password = stringSplitted[2];
                	String mail = stringSplitted[3];
                	
                	/* We don't permit that a user registers under the name of 'all',
                	 * because it would interfere with the analysis of the commands
                	 * sent to the server.
                	 */
                	if (username.equals("all")){
                		activeClients.sendMessageBySocket("username all",clientSocket);
                	}
                	/* This username hasn't been found in the database, so we can add this new client. */
                	else if (database.findClient(username) == null){
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
                	
                } else if(stringSplitted.length == 3 && stringSplitted[0].equals("login")){
	                /*TODO: needs to be fixed: server will search at the registered clients (SQL database or in some
	                 * memory struct)
	                 * if the username do not exist it must be registered first. if not it will be
	                 * added to the activeClients*/
                	ClientInfo client;
                	username = stringSplitted[1];
                	password = stringSplitted[2];
                	
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
		                else {
	                		activeClients.sendMessageBySocket("log error",clientSocket);
	                	}

                	}
                }
        	}
            while(true){
                /* Now, the server can communicate with the client, receiving the requests
                 * and sending back the respective information.
                 */
                String clientInput = in.readUTF();
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
        	 * during this last session???*/
        	activeClients.removeClient(username);
        	if (debugging){
        		System.out.println("ConnectionChat IO:" + e);
        	}
        }
    }
    
    /* The parsing function. Given a request from a client, this thread must recognized the commands
     * and get the right information, so the thread can send it to the client.
     */
    public String parseFunction(String input){
    	/* The answer from the server to the client. */
    	String answer = "";
    	
    	/* Splits the input. */
    	String [] stringSplitted = input.split(" ");
        
        /* The client has sent two keywords and the first is "show".*/
        if(stringSplitted.length == 2 && stringSplitted[0].equals("show")){        	
        	if(stringSplitted[1].equals("matches")){ //show all current matches
        		answer = betScheduler.getMatches();
        	}
        	else if(stringSplitted[1].equals("credits")){ //show user's credits
        		answer = "" + clientInfo.getCredits();
        	}
        	else if(stringSplitted[1].equals("users")){ //show all active users
        		answer = activeClients.getUsersList();
        	}
        	else{
        		answer = "Unknow Command";
        	}
        }
        else if(stringSplitted.length == 3 && stringSplitted[0].equals("send")){
        	if(stringSplitted[1].equals("all")){ //send a message to all users
        		activeClients.sendMessageAll(clientInfo.getUsername() + " says to everyone: " + stringSplitted[2], clientSocket);
        		answer = "";
        	}
        	/* We are sending a message to a user. */
        	else{
        		/* This client is online. */
	        	if(activeClients.checkUser(stringSplitted[1])){
	        		/* Checks if client isn't sending a message to himself/herself. */
	        		if (stringSplitted[1].equals(clientInfo.getUsername())){
	        			activeClients.sendMessageUser("What's the point of sending messages to yourself?", stringSplitted[1]);
	        		}
	        		else{
	        			activeClients.sendMessageUser(clientInfo.getUsername() + " says: " + stringSplitted[2], stringSplitted[1]);
	        		}
	        		answer = "";
	        	}
	        	else if(database.findClient(stringSplitted[1]) != null){
	        		answer = "This client if offline at the moment.";
	        	}
	        	else{
	        		answer = "Username not registered.";
	        	}
        	}
        }
        else if(input.equals("reset")){ //resets user's credits to 100Cr
        	System.out.println("Here");
        	clientInfo.setCredits(defaultCredits);
        	answer = "Your credits were reseted to " + clientInfo.getCredits() + "Cr";
        	database.saveToFile();
        }
        else if(stringSplitted.length == 4 && stringSplitted[0].equals("bet")){
        	/* Variables to save the values inserted by the client. */
        	String resultBet;
        	int gameNumber,credits;
        	
        	try {
        		gameNumber = Integer.parseInt(stringSplitted[1]);
        		resultBet = stringSplitted[2];
        	    credits = Integer.parseInt(stringSplitted[3]);
        	    
        	}
        	catch(NumberFormatException nFE) {
        		/* The user hasn't inserted a number for one of the required arguments. */
        		if (debugging){
        			System.out.println("Not an integer.");
        		}
        		
        	    answer = "Invalid game number or amount of credits!";
        	    return answer;
        	}
        	
        	synchronized(betScheduler.getManager()){
	        	if((resultBet.equals("1") || resultBet.compareToIgnoreCase("x")==0 || resultBet.equals("2"))
	        			&& betScheduler.isValidGame(gameNumber)){
	        		//TODO: WARNING!!!!!!!! betScheduler isn't being saved in file;
	        		/* Takes the credits bet from the client's account. */
	        		clientInfo.setCredits(clientInfo.getCredits() - credits);
	        		/* Creates the new bet and saves the new database into file. */
	        		betScheduler.addBet(new Bet(clientInfo.getUsername(),gameNumber,resultBet,credits));
	        		database.saveToFile();
	
	        		answer = "Bet done!";
	        	}
	        	else {
	        		answer = "Invalid command or the game number that you entered isn't available.";
	        	}
        	}
        }
        else if(stringSplitted.length > 0 && stringSplitted[0].equals("bet")){
        	answer =  "Wrong number of arguments: bet [game number] [bet] [credits]";
        }
        else {
        	answer = "Unknown command";
        }
    	
		return answer;
    }
}
