/* By:
 * 		Ivo Daniel Venhuizen Correia, no 2008110814
 * 		João Pedro Gaioso Barbosa, no 2008111830
 * 
 * Distributed Systems, October 2010 
 */

package clientRMI;

import java.rmi.Remote;

public interface ServerOperations extends Remote{
	public void printUserMessage(String username, String msg) throws java.rmi.RemoteException;
	
	/* This method is just to test whether the user is still active or not.
	 * This steps is necessary because the RMIClient may fall and the server
	 * doesn't detect it and consequently, it won't remove the entry from
	 * the active clients' list and hash table.
	 */
	public boolean testUser() throws java.rmi.RemoteException;
}
