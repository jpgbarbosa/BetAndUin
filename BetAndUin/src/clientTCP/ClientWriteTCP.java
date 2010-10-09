package clientTCP;
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
	        	
	        	/* Shows the main menu. */
	        	out.writeUTF("show menu");
	            while(true){
	            	userInput = reader.readLine();
	            	
	            	/* The user is going to reset the number of credits.
	            	 * In the case we have more than 100, we have to make sure
	            	 * the client noticed this and willingly making an action
	            	 * that will make him lost some credits.
	            	 */
	            	
	            	try{
		            	if (userInput.equals("reset")){
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
		            		else{
		            			out.writeUTF("reset");
		            		}
		            	}
		            	/* The user has selected the option to exit the program. */
		            	else if (userInput.equals("exit")){
		            		System.out.println("Thank you for using the BetAndUin serivce!\n"
		            				+ "Have a nice day!");
		            		System.exit(0);
		            	}
		            	else{
		            		/* We verify the validity of the commands' on the client side
		            		 * in order to avoid unnecessary transmission and don't push
		            		 * too much the server with this checking.
		            		 */
		            		//TODO: Check if we are going to implement this last comment or
		            		//		not.
		            		out.writeUTF(userInput);
		            	}
		            }catch(Exception e){
			        	return;
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
