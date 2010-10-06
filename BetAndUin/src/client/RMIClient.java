package client;

import java.rmi.registry.LocateRegistry;
import server.ClientOperations;

public class RMIClient implements ServerOperations{
	public static void main(String args[]) {

		try {
			
			ClientOperations h = (ClientOperations) LocateRegistry.getRegistry(7000).lookup("benfica");

		} catch (Exception e) {
			System.out.println("Exception in main: " + e);
			e.printStackTrace();
		}
		
		while(true){
			//OPERATIONS
		}

	}

	public String printUserMessage(String msg) {
		return msg;
	}
}
