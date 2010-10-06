package clientRMI;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.registry.LocateRegistry;

import server.ClientInfo;
import server.ClientOperations;

public class RMIClient implements ServerOperations{
	public static void main(String args[]) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));;
		String [] stringSplitted = null;
		String command = "";
		boolean loggedIn = false;
		String username = "", password = "";
		
		try {
			
			ClientOperations server = (ClientOperations) LocateRegistry.getRegistry(12000).lookup("BetAndUinServer");

		} catch (Exception e) {
			System.out.println("Exception in main: " + e);
			e.printStackTrace();
		}
		
		while(true){
			//OPERATIONS
			while (!loggedIn){
				/* The user hasn't made a successful login yet. */
				if (username.equals("") && password.equals("")){
	                /* Reads and splits the input. */
	                try {
						stringSplitted = reader.readLine().split(" ");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	                	
	                if(stringSplitted.length == 4 && stringSplitted[0].equals("register")){
	                	username = stringSplitted[1];
	                	password = stringSplitted[2];
	                	String mail = stringSplitted[3];
	                	
	                	
	                	
	                } else if(stringSplitted.length == 3 && stringSplitted[0].equals("login")){
	                	ClientInfo client;
	                	username = stringSplitted[1];
	                	password = stringSplitted[2];
	                	
	                	
	                }
				}
				/* The client is logged in already. */
				else{
					
				}
			}
		}

	}

	public String printUserMessage(String msg) {
		return msg;
	}
}
