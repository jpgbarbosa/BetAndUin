/* By:
 * 		Ivo Daniel Venhuizen Correia, no 2008110814
 * 		João Pedro Gaioso Barbosa, no 2008111830
 * 
 * Distributed Systems, October 2010 
 */

package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;

import bets.BetScheduler;

import common.Constants;


/* Class that holds all the information related to the users registed in the system.
 * In here, we can save the data, read it from a file and add/remove clients to/from
 * the database.
 */
public class GlobalDataBase {
	/* The hash table that will work as database. */
	private Hashtable <String, ClientInfo> clientsDatabase;
	protected BetScheduler betScheduler;
	
	/* The number of the last game, so the BetScheduler can keep track of it
	 * even when the server goes down and is restarted. If not successful, we
	 * use the default initial number.
	 */
	private int nextGameNumber = 0;
	private int readResult;
	
	@SuppressWarnings("unchecked")
	public GlobalDataBase(){
		clientsDatabase = (Hashtable <String, ClientInfo>)readObjectFromFile("clientsDatabase.bin");
		readResult = (int )readIntFromFile("nextGameNumber.bin");
		if (readResult != -1){
			nextGameNumber = readResult;
		}
		
		betScheduler = null;
		/* We haven't successfully loaded the hash table from the file, so we have to create a
		 * new one.
		 */
		if (clientsDatabase == null){
			
			if (Constants.DEBUGGING_SERVER){
				System.out.println("GlobalDataBase: We failed loading the database from file.");
			}
			
			clientsDatabase = new Hashtable <String, ClientInfo>();
			/* Now, we save it to file so we won't have to repeat this next time. */
			saveObjectToFile("clientsDatabase.bin", clientsDatabase);
		}
	}
	
	/* The reading method for an integer. */
	private int readIntFromFile(String filename){
		try{
	      //create FileInputStream object
	      FileInputStream fin = new FileInputStream(filename);
	 
	      /*
	       * To create DataInputStream object, use
	       * DataInputStream(InputStream in) constructor.
	       */
	 
	       DataInputStream din = new DataInputStream(fin);
	 
	       /*
	        * To read a Java integer primitive from file, use
	        * byte readInt() method of Java DataInputStream class.
	        *
	        * This method reads 4 bytes and returns it as a int value.
	        */
	 
	        int valueRead = din.readInt();
	 
	        /*
	         * To close DataInputStream, use
	         * void close() method.
	         */
	        
	        din.close();
	        
	        return valueRead;
	 
	    }
	    catch(FileNotFoundException fe){
	    	if (Constants.DEBUGGING_SERVER){
	    		System.out.println("GlobalDataBase: FileNotFoundException : " + fe);
	    	}
	    	return -1;
	    }
	    catch(IOException ioe){
	    	if (Constants.DEBUGGING_SERVER){
	    		System.out.println("GlobalDataBase: IOException(saveIntToFile) : " + ioe);
	    	}
	    	return -1;
	    }

	}
	
	/* The saving method for an integer. */
	public void saveIntToFile(String filename, int valueToSave){
		try
	    {
	      //create FileOutputStream object
	      FileOutputStream fos = new FileOutputStream(filename);
	 
	      /*
	       * To create DataOutputStream object from FileOutputStream use,
	       * DataOutputStream(OutputStream os) constructor.
	       *
	       */
	 
	       DataOutputStream dos = new DataOutputStream(fos);
	 
	       /*
	        * To write an int value to a file, use
	        * void writeInt(int i) method of Java DataOutputStream class.
	        *
	        * This method writes specified int to output stream as 4 bytes value.
	        */
	 
	        dos.writeInt(valueToSave);
	 
	        /*
	         * To close DataOutputStream use,
	         * void close() method.
	         *
	         */
	 
	         dos.close();
	 
	    }catch(IOException e){
			/* There was an error. */
			if (Constants.DEBUGGING_SERVER){
				System.out.println("GlobalDataBase IOException(saveIntToFile): " + e);
			}
		}
	}
	
	/* The reading method for an object. This method can only be used by the class. */
	public Object readObjectFromFile(String filename){
		ObjectInputStream iS;
		
		/* We now read the list of clients. */
		try {
			iS = new ObjectInputStream(new FileInputStream(filename));
			return iS.readObject();
		} catch (FileNotFoundException e) {
			if (Constants.DEBUGGING_SERVER){
				System.out.println("The " + filename + " file couldn't be found...");
			}
			return null;
		} catch (ClassNotFoundException e) {
			if (Constants.DEBUGGING_SERVER){
				System.out.println("ClassNotFound in readFromFile (GlobalDataBase): " + e);
			}
			return null;
		}catch (IOException e) {
			if (Constants.DEBUGGING_SERVER){
				System.out.println("GlobalDataBase IO in readFromFile: " + e);
			}
			return null;
		}
	}
	
	/* The saving method for an object. */
	public void saveObjectToFile(String filename, Object obj){
		ObjectOutputStream oS;
		
		try {
			oS = new ObjectOutputStream(new FileOutputStream(filename));
			oS.writeObject(obj);
		} catch (FileNotFoundException e) {
			if (Constants.DEBUGGING_SERVER){
				System.out.println("The clientsDatabase.bin file couldn't be found...");
			}
		} catch (IOException e) {
			if (Constants.DEBUGGING_SERVER){
				System.out.println("IO in saveToFile (GlobalDataBase): " + e);
			}
		}
	}
	
	/* Method to add a client. If the client is already in the database, it returns
	 * false. Else, it returns true.
	 * We have checked already that this isn't a repeated login.
	 */
	public synchronized ClientInfo addClient(String user, String pass, String mail){
		/* The client wasn't found in the list, so we can proceed. */
		ClientInfo element = new ClientInfo(user, pass, mail, Constants.DEFAULT_CREDITS);
		
		clientsDatabase.put(user,element);
		
		/* Write into a file the new version of the clients' database. */
		saveObjectToFile("clientsDatabase.bin", clientsDatabase);
		
		return element;
	}
	
	/* Method to remove a client from the database. Returns true if it succeeded, false otherwise. */
	public synchronized boolean removeClient(String user){
		ClientInfo element = clientsDatabase.get(user);

		/* The client was not found in the list, so we can't removing process fails. */
		if (element == null){
			return false;
		}
		/* The client was found in the hash table, so we can proceed. */
		clientsDatabase.remove(user);
		
		/* Write into a file the new version of the clients' database. */
		saveObjectToFile("clientsDatabase.bin", clientsDatabase);
		
		return true;
	}
	
	/* Find a specific client by his/her user name. */
	public synchronized ClientInfo findClient(String user){
		return clientsDatabase.get(user);
	}
	
	public void setBetScheduler(BetScheduler betS){
		this.betScheduler = betS;
	}
	
	public synchronized void increaseCredits(String user, int creditsWon){
		ClientInfo client =  clientsDatabase.get(user);
		client.increaseCredits(creditsWon);
		/* Saves the the actual state of the client.*/
		saveObjectToFile("clientsDatabase.bin", clientsDatabase);
	}
	
	public int getNextGameNumber(){
		return nextGameNumber;
	}
	
	public void setNextGameNumber(int value){
		nextGameNumber = value;
	}
	
	public synchronized Object getClientsDatabase(){
		return clientsDatabase;
	}
}
