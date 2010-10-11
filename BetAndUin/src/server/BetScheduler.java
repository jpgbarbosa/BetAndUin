package server;

import java.util.Iterator;
import java.util.Vector;

import pt.uc.dei.sd.BetManager;
import pt.uc.dei.sd.IBetManager;
import pt.uc.dei.sd.IMatch;


public class BetScheduler extends Thread{
	/* The variable that controls the time of a round. */
	int TIME_BETWEEN_ROUNDS = 60000;
	/* Variables to save information related to the current matches. */
	String message, lastMatches = "";
	
	/* References to the list of active clients and the database. */
	ActiveClients activeClients;
	GlobalDataBase database;
    
	/* Variables to keep track of the game results and how to manage them. */
	IBetManager man;
    int gamesPerRound;
    int [] gameResults;
    Vector<Bet> betList;
 
    
	public BetScheduler(ActiveClients activeClients, int gamesPerRound, GlobalDataBase clientsStorage){
		this.activeClients = activeClients;
		this.gamesPerRound=gamesPerRound;
		gameResults = new int [gamesPerRound];
		this.database = clientsStorage;
		
		/* Reads from a file all the bets made and verifies if there is a valid one. */
		betList = (Vector<Bet>)database.readObjectFromFile("bets.bin");
		if (betList == null){
			betList = new Vector<Bet>(0);
		}
		
		//TODO: save the current matches
		man = new BetManager(gamesPerRound, database);
		
		this.start();
	}
	
	public void run(){
        int nGame;
        while (true){
        	message = "";
	        message += "\n========= Batch of Matches =========\n";        
	        for (IMatch m : man.getMatches()) {
	            message += m.getCode()+" - "+m.getHomeTeam() + " vs " + m.getAwayTeam() + "\n";
	        }
	        
	        /*TODO: Saving current matches but we are reading them*/
	        database.saveObjectToFile("matches.bin", man.getMatches());
	        
	        synchronized(lastMatches){
	        	lastMatches=new String(message);
	        }
	        
	        try {
				Thread.sleep(TIME_BETWEEN_ROUNDS);
			} catch (InterruptedException e) {
				System.out.println("BetScheduler has been interrupted while sleeping.");
			}
			
			synchronized(man){
				message = "";
				nGame=0;
		        message += "========= Results =========\n";
		        for (IMatch m : man.getMatches()) {
		        	message += (m.getCode() + " - " + m + ": ");
		            switch (man.getResult(m)) {
		                case HOME: 
		                	gameResults[nGame]=1;
		                	message +=  "1\n";
		                    break;
		                case AWAY: 
		                	gameResults[nGame]=2;
		                	message +=  "2\n";
		                    break;
		                default: 
		                	gameResults[nGame]=0;
		                	message += "X\n";
		                    break;
		            }
		            nGame++;
		        }
		        
		        /*Check all the bets and sends the result to the respective user*/
		        notifyBets();
		        
		        betList.clear();
		        /* Cleans the files with the batches. */
		        database.saveObjectToFile("bets.bin", betList);
		        
		        /*Send the results to all the active clients.*/
		        //TODO: Temos de mudar esta funçao. nao pode ser igual pq depois vai dar null ptr exception!
		        activeClients.sendMessageAll(message, null, null);
		        
		        /* Creates a new batch of games. */
		        man.refreshMatches();
			}
        }
	}
	
	public void notifyBets(){
		Iterator<Bet> it = betList.iterator();
		Bet bet;
		
		while(it.hasNext()){
			bet=it.next();
			
			/* The client has won. */
			if(gameResults[(bet.getGameNumber())%gamesPerRound]==0 && bet.bet.compareToIgnoreCase("X")==0
					|| gameResults[(bet.getGameNumber())%gamesPerRound]==1 && bet.bet.equals("1")
					|| gameResults[(bet.getGameNumber())%gamesPerRound]==2 && bet.bet.equals("2")){
				
				/* We first update the value in the persistent memory, by informing the database. */
				database.increaseCredits(bet.getUser(), bet.credits * 3);
				
				/* Then, we inform the client. */
				activeClients.sendMessageUser("Congratulations, it looks like your guess was right about game " +
						bet.gameNumber + ". You won: " + bet.credits * 3 + " Credits!", bet.getUser());
			}
			else {
				activeClients.sendMessageUser("Sorry, it looks like your guess wasn't right about game " +
						+ bet.gameNumber + "... Please try again!", bet.getUser());
			}
		}
	}
	
	public boolean isValidGame(int nGame){
		 for (IMatch m : man.getMatches()) {
			 if(m.getMatchCode()==nGame){
				 return true;
			 }
		 }
		 return false;
	}
	
	public String getMatches(){
		return lastMatches;
	}
	
	public IBetManager getManager(){
		return man;
	}
	
	public void addBet(Bet bet){
		betList.add(bet);
		
		/* Saves the list of bets into a file. */
		database.saveObjectToFile("bets.bin", betList);
	}
}
