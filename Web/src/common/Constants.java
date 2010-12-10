/* By:
 * 		Ivo Daniel Venhuizen Correia, no 2008110814
 * 		João Pedro Gaioso Barbosa, no 2008111830
 * 
 * Distributed Systems, October 2010 
 */

package common;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class Constants {
	/* The number of default credits. */
	public static int DEFAULT_CREDITS;
	
	/* The time that the client sleeps before trying again. */
	public static int CLIENT_WAITING_TIME;
	/* The number of times the client retries to connect before giving up. */
	public static int NO_RETRIES;

	/* The variable that controls the time of a round. */
	public static int TIME_BETWEEN_ROUNDS;
	
	/* Times to trigger the message timers. */
	public static int KEEP_ALIVE_TIME; //The time between two consecutive KEEP_ALIVE's.
	public static int SERVER_WAITING_TIME; //The time needed to consider the other server dead.
	public static int FIRST_WAITING_TIME; //The time the server waits before sending the initial message again.
	public static int SERVER_INIT_RETRIES; //The upper limit of initial retries for a server.
	
	/* Number of Games per round. */
	public static int NO_GAMES;
	
	/* The size of the buffer to save offline messages. */
	public static int BUFFER_SIZE;
	
	/* The ports related to each server. */
	public static int FIRST_TCP_SERVER_PORT;
	public static int SECOND_TCP_SERVER_PORT;
	public static int FIRST_RMI_SERVER_PORT;
	public static int SECOND_RMI_SERVER_PORT;
	
	public static int STONITH_FIRST_SERVER_PORT;
	public static int STONITH_SECOND_SERVER_PORT;
	
	/*Set to true if you want the program to display debugging messages.*/
	public static boolean DEBUGGING_SERVER = false;
	public static boolean DEBUGGING_CLIENT = false;

	public static void readProperties(String fileName){
		Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(fileName));

            DEFAULT_CREDITS = Integer.parseInt(properties.getProperty("DEFAULT_CREDITS"));
            CLIENT_WAITING_TIME = Integer.parseInt(properties.getProperty("CLIENT_WAITING_TIME"));
            NO_RETRIES = Integer.parseInt(properties.getProperty("NO_RETRIES"));
            TIME_BETWEEN_ROUNDS = Integer.parseInt(properties.getProperty("TIME_BETWEEN_ROUNDS"));
            KEEP_ALIVE_TIME = Integer.parseInt(properties.getProperty("KEEP_ALIVE_TIME"));
            SERVER_INIT_RETRIES = Integer.parseInt(properties.getProperty("SERVER_INIT_RETRIES"));
            SERVER_WAITING_TIME = Integer.parseInt(properties.getProperty("SERVER_WAITING_TIME"));
            FIRST_WAITING_TIME = Integer.parseInt(properties.getProperty("FIRST_WAITING_TIME"));
            NO_GAMES = Integer.parseInt(properties.getProperty("NO_GAMES"));
            BUFFER_SIZE = Integer.parseInt(properties.getProperty("BUFFER_SIZE"));
            FIRST_TCP_SERVER_PORT = Integer.parseInt(properties.getProperty("FIRST_TCP_SERVER_PORT"));
            SECOND_TCP_SERVER_PORT = Integer.parseInt(properties.getProperty("SECOND_TCP_SERVER_PORT"));
            FIRST_RMI_SERVER_PORT = Integer.parseInt(properties.getProperty("FIRST_RMI_SERVER_PORT"));
            SECOND_RMI_SERVER_PORT = Integer.parseInt(properties.getProperty("SECOND_RMI_SERVER_PORT"));
            STONITH_FIRST_SERVER_PORT = Integer.parseInt(properties.getProperty("STONITH_FIRST_SERVER_PORT"));
            STONITH_SECOND_SERVER_PORT = Integer.parseInt(properties.getProperty("STONITH_SECOND_SERVER_PORT"));
        
        } catch (Exception e) {
        	if (DEBUGGING_CLIENT || DEBUGGING_SERVER){
        		System.out.println("Constants Exception (readProperties): " + e.getMessage());
        	}
        	
        	properties.setProperty("DEFAULT_CREDITS", "100");
        	DEFAULT_CREDITS = 100;
        	properties.setProperty("CLIENT_WAITING_TIME", "500");
        	CLIENT_WAITING_TIME = 500;
        	properties.setProperty("NO_RETRIES", "10");
        	NO_RETRIES = 10;
        	properties.setProperty("TIME_BETWEEN_ROUNDS", "30000");
        	TIME_BETWEEN_ROUNDS = 30000;
        	properties.setProperty("KEEP_ALIVE_TIME", "250");
        	KEEP_ALIVE_TIME = 250;
        	properties.setProperty("SERVER_WAITING_TIME", "2500");
        	SERVER_WAITING_TIME = 2500;
        	properties.setProperty("SERVER_INIT_RETRIES", "5");
        	SERVER_WAITING_TIME = 5;
        	properties.setProperty("FIRST_WAITING_TIME", "1000");
        	FIRST_WAITING_TIME = 1000;
        	properties.setProperty("NO_GAMES", "10");
        	NO_GAMES = 10;
        	properties.setProperty("BUFFER_SIZE", "10");
        	BUFFER_SIZE = 10;
        	properties.setProperty("FIRST_TCP_SERVER_PORT", "6000");
        	FIRST_TCP_SERVER_PORT = 6000;
        	properties.setProperty("SECOND_TCP_SERVER_PORT", "7000");
        	SECOND_TCP_SERVER_PORT = 7000;
        	properties.setProperty("FIRST_RMI_SERVER_PORT", "12000");
        	FIRST_RMI_SERVER_PORT = 12000;
        	properties.setProperty("SECOND_RMI_SERVER_PORT", "13000");
        	SECOND_RMI_SERVER_PORT = 13000;
        	properties.setProperty("STONITH_FIRST_SERVER_PORT", "8000");
        	STONITH_FIRST_SERVER_PORT = 8000;
        	properties.setProperty("STONITH_SECOND_SERVER_PORT", "9000");
        	STONITH_SECOND_SERVER_PORT = 9000;
        	
        	try {
				properties.store(new FileOutputStream(fileName), "BetAndUin");
			} catch (Exception e2) {
				if (DEBUGGING_CLIENT || DEBUGGING_SERVER){
	        		System.out.println("Constants Exception2 (readProperties): " + e2.getMessage());
	        	}
			}
        	
        }
		
	}
	
}
