package clientRMI;

import java.io.BufferedReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Vector;

import server.Server;

import clientTCP.ConnectionLock;

public class RMIWriter extends Thread{

	RMIClient client;
	
	Server server;
	
	ConnectionLock connectionLock;
	Vector<String> msgBuffer;
	
	BufferedReader reader;
	
	boolean debugging = true;
	
	String userInput, serverAnswer, username;
	
	RMIWriter(ConnectionLock connectionLock, RMIClient client){
		this.connectionLock = connectionLock;
		this.client = client;
		msgBuffer = new Vector<String>();	
	}
	
	public void run(){
		
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
		
        while(true){
        	try{
            	userInput = reader.readLine();
            	
            	if(connectionLock.isConnectionDown() && userInput.split(" ")[0].equals("send")){
            		//grava cenas em ficheiro
            	} else if(!connectionLock.isConnectionDown()){
					System.out.println("\n>> ");
					serverAnswer = client.parseFunction(username, userInput, server, reader);
					System.out.println(serverAnswer);
            	}
            }catch(RemoteException e){
            	//grava em ficheiro o comando userInput;
            	connectionLock.notify();
	        } catch (IOException e) {
				e.printStackTrace();
			}
        }
	}
	
	protected void setClient(RMIClient client){
		this.client = client;
	}
	
	protected void setUserName(String username){
		this.username = username;
	}
	
	protected void setServer(Server server){
		this.server = server;
	}
}
