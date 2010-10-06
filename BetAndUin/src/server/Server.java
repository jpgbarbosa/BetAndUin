package server;

import java.net.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.io.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/*TODO: We still have to save the last batch of matches. In case the server goes down,
 * 		the new server will have to read these files.
 */

public class Server  extends UnicastRemoteObject implements ClientOperations{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
	public Server() throws RemoteException{
		super();
		

	}
	
    public static void main(String args[]){
    	/*Number of Games per round*/
    	int nGames = 10; 
    	
    	/*Set to true if you want the program to display debugging messages.*/
		Boolean debugging = true;
		
    	/* The object responsible for dealing with the information related to the clients logged in the system. */
        ActiveClients activeClients;
        /* The object responsible for creating the matches and setting the results. */
        BetScheduler betScheduler;
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
            
        	/* In here, we initialize the process of exchanging messages between servers. */
            new ConnectionWithServerManager(serverPort, partnerPort, isDefaultServer, changeStatusLock);
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
    		   		
    		/* We open the socket connection. */
            ServerSocket listenSocket = new ServerSocket(serverPort);
            if (debugging){
            	System.out.println("Listening at port  " + serverPort);
            	System.out.println("LISTEN SOCKET=" + listenSocket);
            }
    		
    		/* We are the primary server, so we can communicate with clients. */
            
            /* There's the active clients' list to handle, the bets and the database. */
    		activeClients = new ActiveClients();
    		betScheduler = new BetScheduler(activeClients, nGames, database);
    		database.setBetScheduler(betScheduler);
            
            /* Now, we prepare the connection to handle requests from RMI clients. */
    		try {
    			Server rmiServices = new Server();
    			Registry registry = LocateRegistry.createRegistry(12000);
    			registry.rebind("BetAndUinServer", rmiServices);
    			
    			if (debugging){
    				System.out.println("RMI Server ready.");
    			}
    		} catch (RemoteException re) {
    			System.out.println("Exception in Server RMI: " + re);
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

    /* METHODS RELATED TO THE RMI */
	@Override
	public String clientLogin(String user, String pass) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String clientRegister(String user, String pass)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String clientMakeBet(int nGame, int bet, int credits)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String clientResetCredits() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String clientSendMsgAll() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String clientSendMsgUser(String user) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String clientShowCredits() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String clientShowMatches() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String clientShowMenu() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String clientShowUsers() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
}

