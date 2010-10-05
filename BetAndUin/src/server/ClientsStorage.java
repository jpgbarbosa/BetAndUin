package server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;

/* Class that holds all the information related to the users registed in the system.
 * In here, we can save the data, read it from a file and add/remove clients to/from
 * the database.
 */
public class ClientsStorage {
	/* The hash table that will work as database. */
	Hashtable <String, ClientInfo> clientsDatabase;
	BetScheduler betScheduler;
	
	/*Set to true if you want the program to display debugging messages.*/
	Boolean debugging = false;
	
	/* The number of default credits for a user.*/
	int defaultCredits = 100;
	
	/* The number of the last game, so the BetScheduler can keep track of it
	 * even when the server goes down and is restarted. If not successful, we
	 * use the default initial number.
	 */
	//TODO: We have to save and read this variable.
	int lastGameNumber = 0;
	
	public ClientsStorage(){
		clientsDatabase = (Hashtable <String, ClientInfo>)readFromFile();
		betScheduler = null;
		/* We haven't successfully loaded the hash table from the file, so we have to create a
		 * new one.
		 */
		if (clientsDatabase == null){
			
			if (debugging){
				System.out.println("We failed loading the database from file.");
			}
			
			clientsDatabase = new Hashtable <String, ClientInfo>();
			/* Now, we save it to file so we won't have to repeat this next time. */
			saveToFile();
		}
	}
	
	/* The reading method. This method can only be used by the class. */
	synchronized private Object readFromFile(){
		ObjectInputStream iS;
		
		/* We read the value for the variable related to the last game. */
		//TODO: Implement this, as well as the saving.
		
		/* We now read the list of clients. */
		try {
			iS = new ObjectInputStream(new FileInputStream("clientsDatabase.bin"));
			return iS.readObject();
		} catch (FileNotFoundException e) {
			System.out.println("The clientsDatabase.bin file couldn't be found...");
			return null;
		} catch (ClassNotFoundException e) {
			System.out.println("ClassNotFound in readFromFile (ClientsStorage): " + e);
			return null;
		}catch (IOException e) {
			System.out.println("IO in readFromFile (ClientsStorage): " + e);
			return null;
		}

	}
	
	/* The saving method. */
	synchronized public void saveToFile(){
		ObjectOutputStream oS;
		
		try {
			oS = new ObjectOutputStream(new FileOutputStream("clientsDatabase.bin"));
			oS.writeObject(clientsDatabase);
		} catch (FileNotFoundException e) {
			System.out.println("The clientsDatabase.bin file couldn't be found...");
		} catch (IOException e) {
			System.out.println("IO in saveToFile (ClientsStorage): " + e);
		}
		
	}
	
	/* Method to add a client. If the client is already in the database, it returns
	 * false. Else, it returns true.
	 */
	public ClientInfo addClient(String user, String pass, String mail){
		ClientInfo element;
		/* We have checked already that this isn't a repeated login. */

		/* The client wasn't found in the list, so we can proceed. */
		element = new ClientInfo(user, pass, mail, defaultCredits);
		synchronized(clientsDatabase){
			clientsDatabase.put(user,element);
		}
		
		return element;
	}
	
	/* Method to remove a client from the database. Returns true if it succeeded, false otherwise. */
	public boolean removeClient(String user){
		ClientInfo element;
		synchronized(clientsDatabase){
			element = clientsDatabase.get(user);
		}

		/* The client was not found in the list, so we can't removing process fails. */
		if (element == null){
			return false;
		}
		/* The client was found in the hash table, so we can proceed. */
		synchronized(clientsDatabase){
			clientsDatabase.remove(user);
		}
		
		return true;
	}
	
	/* Find a specific client by his/her user name. */
	public ClientInfo findClient(String user){
		synchronized(clientsDatabase){
			return clientsDatabase.get(user);
		}
	}
	
	public void setBetScheduler(BetScheduler betS){
		this.betScheduler = betS;
	}
	
	public void increaseCredits(String user, int creditsWon){
		ClientInfo client;
		
		synchronized(clientsDatabase){
			client =  clientsDatabase.get(user);
			client.increaseCredits(creditsWon);
		}
		
	}
	
	public int getLastGameNumber(){
		return lastGameNumber;
	}
	
	public void setLastGameNumber(int value){
		lastGameNumber = value;
	}
}
