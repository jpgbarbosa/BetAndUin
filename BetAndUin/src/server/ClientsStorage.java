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
	
	/*Set to true if you want the program to display debugging messages.*/
	Boolean debugging = false;
	
	public ClientsStorage(){
		clientsDatabase = (Hashtable <String, ClientInfo>)readFromFile();
		
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
	
	/* The reading method. */
	public Object readFromFile(){
		ObjectInputStream iS;
		
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
	public void saveToFile(){
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
	public Boolean addClient(String user, String pass, String mail){
		ClientInfo element;
		element = clientsDatabase.get(user);
		
		/* The client was found in the list, so we can't add a client with the same
		 * username.
		 */
		if (element != null){
			return false;
		}
		/* The client wasn't found in the list, so we can proceed. */
		element = new ClientInfo(user, pass, mail, 100); //By default, we give 100 credits to a user.
		clientsDatabase.put(user,element);
		
		return true;
	}
	
	/* Method to remove a client from the database. Returns true if it succeeded, false otherwise. */
	public Boolean removeClient(String user){
		ClientInfo element;
		element = clientsDatabase.get(user);
		
		/* The client was not found in the list, so we can't removing process fails. */
		if (element == null){
			return false;
		}
		/* The client was found in the hash table, so we can proceed. */
		clientsDatabase.remove(user);
		
		return true;
	}
	
	/* Find a specific client by his/her user name. */
	public ClientInfo findClient(String user){
		return clientsDatabase.get(user);
	}
}
