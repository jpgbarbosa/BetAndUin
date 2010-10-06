package server;

public interface ClientOperations {
	public String clientLogin(String user, String pass) throws java.rmi.RemoteException;
	public String clientRegister(String user, String pass) throws java.rmi.RemoteException;
	
	public String clientShowMenu() throws java.rmi.RemoteException;
	public String clientShowUsers() throws java.rmi.RemoteException;
	public String clientShowMatches() throws java.rmi.RemoteException;
	public String clientShowCredits(String user) throws java.rmi.RemoteException;
	
	public String clientResetCredits(String user) throws java.rmi.RemoteException;

	public String clientSendMsgUser(String userSender, String userDest, String message) throws java.rmi.RemoteException;
	public String clientSendMsgAll(String userSender, String msg) throws java.rmi.RemoteException;
	
	public String clientMakeBet(int nGame, int bet, int credits) throws java.rmi.RemoteException;
}
