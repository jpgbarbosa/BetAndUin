package server;

import java.net.Socket;
import java.util.concurrent.ForkJoinPool;

public class DataBase{
	public static void main(){
		/*The variables related to the server ports available.*/
		int []serverPorts = new int[2]; //The array with the two different ports.
		int noServerPorts = serverPorts.length; //Total number of possible servers ports.
		//Places the two ports in the array.
		serverPorts[0] = 6000;
		serverPorts[1] = 7000;
	
		ForkJoinPool forkJoinPool;
		
		while(true){
			
		}
	}
}

class DataBaseClient extends Thread{
	
	public DataBaseClient(Socket aClientSocket, ClientInfo clientInfo){
		
	}
	
	
	
	
	
}