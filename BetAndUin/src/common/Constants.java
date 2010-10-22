package common;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class Constants {
	/* The number of default credits. */
	public static int DEFAULT_CREDITS = 100;
	
	/* The time that the client sleeps before trying again. */
	public static int CLIENT_WAITING_TIME = 1000;
	/* The number of times the client retries to connect before giving up. */
	public static int NO_RETRIES = 10;

	/* The variable that controls the time of a round. */
	public static int TIME_BETWEEN_ROUNDS = 60000;
	
	
	/* Times to trigger the message timers. */
	public static int KEEP_ALIVE_TIME = 5000; //The time between two consecutive KEEP_ALIVE's.
	public static int SERVER_WAITING_TIME = 15000; //The time needed to consider the other server dead.
	public static int FIRST_WAITING_TIME = 5000; //The time the server waits before sending the initial message again.
	
	/* Number of Games per round. */
	public static int NO_GAMES = 10;
	
	/* The ports related to each server. */
	public static int FIRST_TCP_SERVER_PORT = 6000;
	public static int SECOND_TCP_SERVER_PORT = 7000;
	public static int FIRST_RMI_SERVER_PORT = 12000;
	public static int SECOND_RMI_SERVER_PORT = 13000;
	
	public static int STONITH_FIRST_SERVER_PORT = 8000;
	public static int STONITH_SECOND_SERVER_PORT = 9000;
	
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
            SERVER_WAITING_TIME = Integer.parseInt(properties.getProperty("SERVER_WAITING_TIME"));
            FIRST_WAITING_TIME = Integer.parseInt(properties.getProperty("FIRST_WAITING_TIME"));
            NO_GAMES = Integer.parseInt(properties.getProperty("NO_GAMES"));
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
        	properties.setProperty("CLIENT_WAITING_TIME", "1000");
        	CLIENT_WAITING_TIME = 1000;
        	properties.setProperty("NO_RETRIES", "10");
        	NO_RETRIES = 10;
        	properties.setProperty("TIME_BETWEEN_ROUNDS", "60000");
        	TIME_BETWEEN_ROUNDS = 60000;
        	properties.setProperty("KEEP_ALIVE_TIME", "5000");
        	KEEP_ALIVE_TIME = 5000;
        	properties.setProperty("SERVER_WAITING_TIME", "15000");
        	SERVER_WAITING_TIME = 15000;
        	properties.setProperty("FIRST_WAITING_TIME", "5000");
        	FIRST_WAITING_TIME = 5000;
        	properties.setProperty("NO_GAMES", "10");
        	NO_GAMES = 10;
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
