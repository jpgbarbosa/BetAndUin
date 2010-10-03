package client;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;



public class ClientWriteTCP extends Thread {
	/*Set to true if you want the program to display debugging messages.*/
	boolean debugging = true;
	
	//This thread will be responsible for handling problems with the link to the server.
	String user = null,pass = null;
	boolean loggedIn = false;
    DataOutputStream out;
    Socket clientSocket;
    String userInput;
    int serverSocketFirst = 6000, serverSocketSecond = 7000;
    ConnectionLock connectionLock;
    ClientReadTCP readThread;
    int userCredits;
    int defaultCredits = 100;
    
    BufferedReader reader;
    
    public ClientWriteTCP (ConnectionLock lock) {
    	connectionLock = lock;
    	userCredits = 0;
        this.start();
    }

    public String printMenu(){    	
    	return "\nMAIN MENU:" +
    			"\n1. Show the current credit of the user: show credits" +
    			"\n2. Reset user credits to 100Cr:\n\treset" +
    			"\n3. View Current Matches:\n\tshow matches" +
    			"\n4. Make a Bet:\n\tbet [match number] [1 x 2] [credits]" +
    			"\n5. Show Online Users:\n\tshow users" +
    			"\n6. Send messagen to specific user:\n\tsend [user] '[message]'" +
    			"\n7. Send message to all users:\n\tsend all '[message]'" + 
    			"\n8. Print the menu options:\n\tmenu";    	
    }
    
    //=============================
    public void run(){
    	while (true){
	        try{
	        	synchronized(connectionLock){
	        		while (connectionLock.isConnectionDown()){
	        			try {
	        				connectionLock.wait();
						} catch (InterruptedException e) {
							if (debugging){
								System.out.println("ClientWriteTCP Thread interrupted.");
							}
						}
	        		}
	        	}
	
	        	System.out.println(printMenu());
	            while(true){
	            	System.out.println(" >>> ");
	            	userInput = reader.readLine();
	            	
	            	/* The user is going to reset the number of credits.
	            	 * In the case we have more than 100, we have to make sure
	            	 * the client noticed this and willingly making an action
	            	 * that will make him lost some credits.
	            	 */
	            	
	            	if (userInput.equals("reset credits")){
	            		readThread.setIsToPrint(false);
	            		out.writeUTF("show credits");
	            		try{
	            			synchronized(this){
	            				this.wait();
	            			}
	            		}catch (InterruptedException e){
	            			/* Continues the work. */
	            		}

	            		if (userCredits > defaultCredits){
	            			String finalAnswer = "";
	            			System.out.printf("In this moment, you have %d, which means you are going to lose %d credits.\n" +
	            					"Are you sure you want to continue with the process (Y/N)?\n", userCredits, userCredits - defaultCredits);
	            			do{
	            				try{
	            					finalAnswer = reader.readLine().toUpperCase();
	            				}catch (Exception e){
	            					return;
	            				}
	            			}
	            			while (!finalAnswer.equals("Y") && !finalAnswer.equals("N"));
	            			
	            			if (finalAnswer.equals("Y")){
	            				out.writeUTF("reset");
	            			}
	            			else{
	            				System.out.printf("Operation cancelled. You still have %d credits.\n", userCredits);
	            			}
	            		}
	            	}
	            	else if (userInput.equals("menu")){
	            		printMenu();
	            	}
	            	else{
	            		out.writeUTF(userInput);
	            	}
	            }
	        }catch(EOFException e){
	        	if (debugging){
	        		System.out.println("ClientWriteTCP EOF:" + e);
				}
	        	
	        }catch(IOException e){
	        	if (debugging){
	        		System.out.println("ClientWriteTCP IO:" + e);
				}
	        	
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
    
    public void setReadThread(ClientReadTCP thread){
    	readThread = thread;
    }
    
    public void setUserCredtis(int credits){
    	userCredits = credits;
    }
	
}
