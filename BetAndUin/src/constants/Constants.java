package constants;

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
	public static int FIRST_WAITING_TIME = 1000; //The time the server waits before sending the initial message again.
	
	/* Number of Games per round. */
	public static int NO_GAMES = 10;
	
	/* The ports related to each server. */
	public static int FIRST_TCP_SERVER_PORT = 6000;
	public static int SECOND_TCP_SERVER_PORT = 7000;
	public static int FIRST_RMI_SERVER_PORT = 12000;
	public static int SECOND_RMI_SERVER_PORT = 13000;
	
	public static int STONITH_FIRST_SERVER_PORT = 8000;
	public static int STONITH_SECOND_SERVER_PORT = 9000;
	
}
