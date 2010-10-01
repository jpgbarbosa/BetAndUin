package server;
import pt.uc.dei.sd.BetManager;
import pt.uc.dei.sd.IBetManager;
import pt.uc.dei.sd.IMatch;


public class BetScheduler extends Thread{
	int TIME_BETWEEN_ROUNDS = 10000;
	String message, lastMatches="";
	ActiveClients activeClients;
	
	public BetScheduler(ActiveClients activeClients){
		this.activeClients = activeClients;
		this.start();
	}
	
	public void run(){
        IBetManager man = new BetManager();
        

        while (true){
        	message = "";
	        message += "\n========= Batch of Matches =========\n";        
	        for (IMatch m : man.getMatches()) {
	            message += m.getHomeTeam() + " vs " + m.getAwayTeam() + "\n";
	        }
	        synchronized(lastMatches){
	        	lastMatches=new String(message);
	        }
	        System.out.println(message);
	        try {
				Thread.sleep(TIME_BETWEEN_ROUNDS);
			} catch (InterruptedException e) {
				System.out.println("BetScheduler has been interrupted while sleeping.");
			}
			
			message = "";
			
	        message += "========= Results =========\n";
	        for (IMatch m : man.getMatches()) {
	        	message += (m + ": ");
	            switch (man.getResult(m)) {
	                case HOME: 
	                	message +=  "1\n";
	                    break;
	                case AWAY: 
	                	message +=  "2\n";
	                    break;
	                default: 
	                	message += "X\n";
	                    break;
	            }
	        }
	
	        /*Send the results to all the active clients.*/
	        activeClients.sendMessageAll(message, null);
	        /* Creates a new batch of games. */
	        man.refreshMatches();
        }
	}
	
	public String getMatches(){
		return lastMatches;
	}
}
