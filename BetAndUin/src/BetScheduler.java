import java.net.Socket;
import pt.uc.dei.sd.BetManager;
import pt.uc.dei.sd.IBetManager;
import pt.uc.dei.sd.IMatch;


public class BetScheduler extends Thread{
	int TIME_BETWEEN_ROUNDS = 10000;
	String message;
	ThreadCounter threadCounter;
	
	public BetScheduler(ThreadCounter counter){
		threadCounter = counter;
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
	        
	        System.out.println(message);
	        try {
				Thread.sleep(TIME_BETWEEN_ROUNDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
	        threadCounter.sendMessage(message, null);
	        /* Creates a new batch of games. */
	        man.refreshMatches();
        }
	}
}
