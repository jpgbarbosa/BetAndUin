package clientRMI;

import java.rmi.Remote;

public interface ServerOperations extends Remote{
	public void printUserMessage(String msg) throws java.rmi.RemoteException;
}
