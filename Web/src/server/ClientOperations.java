/* By:
 * 		Ivo Daniel Venhuizen Correia, no 2008110814
 * 		João Pedro Gaioso Barbosa, no 2008111830
 * 
 * Distributed Systems, October 2010 
 */

package server;

import java.rmi.Remote;

import clientRMI.ServerOperations;


public interface ClientOperations extends Remote{
	public String clientLogin(String user, String pass, ServerOperations client) throws java.rmi.RemoteException;
	public String clientRegister(String user, String pass, String email, ServerOperations client) throws java.rmi.RemoteException;
	
	public String clientShowMenu() throws java.rmi.RemoteException;
	public String clientShowUsers() throws java.rmi.RemoteException;
	public String clientShowMatches() throws java.rmi.RemoteException;
	public String clientShowCredits(String user) throws java.rmi.RemoteException;
	
	public String clientResetCredits(String user) throws java.rmi.RemoteException;

	public String clientSendMsgUser(String userSender, String userDest, String message) throws java.rmi.RemoteException;
	public String clientSendMsgAll(String userSender, String msg) throws java.rmi.RemoteException;
	
	public String clientMakeBet(String username, int gameNumber, String bet, int credits) throws java.rmi.RemoteException;
	public void clientLeave(String username) throws java.rmi.RemoteException;
	
}
