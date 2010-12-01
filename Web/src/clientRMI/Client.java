package clientRMI;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import chatJSP.ChatServlet;

import server.ClientOperations;

public class Client extends UnicastRemoteObject implements ServerOperations{
	ClientOperations mainServer=null;
	Registry registry = null;

	public Client(Registry registry, ClientOperations mainServer) throws RemoteException {
		super();
		this.registry = registry;
		this.mainServer = mainServer;
	}

	
	@Override
	public void printUserMessage(String msg, String user) throws RemoteException {
		ChatServlet.sendMessage(msg, user);
	}

	@Override
	public boolean testUser() throws RemoteException {
		return true;
	}
	
	public ClientOperations getMainServer(){
		return mainServer;
	}
}
