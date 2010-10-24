/* By:
 * 		Ivo Daniel Venhuizen Correia, no 2008110814
 * 		João Pedro Gaioso Barbosa, no 2008111830
 * 
 * Distributed Systems, October 2010 
 */

package bets;

import java.util.Iterator;
import java.util.Vector;
import server.ActiveClients;
import server.GlobalDataBase;
import common.Constants;

public class BetScheduler extends Thread{
	/* Variables to save information related to the current matches. */
	private String message, lastMatches = "";
	
	/* References to the list of active clients and the database. */
	private ActiveClients activeClients;
	private GlobalDataBase database;
    
	/* Variables to keep track of the game results and how to manage them. */
	private IBetManager man;
	private int [] gameResults;
	private Vector<Bet> betList;
 
    
	@SuppressWarnings("unchecked")
	public BetScheduler(ActiveClients activeClients, GlobalDataBase clientsStorage){
		this.activeClients = activeClients;
		gameResults = new int [Constants.NO_GAMES];
		this.database = clientsStorage;
		
		/* Reads from a file all the bets made and verifies if there is a valid one. */
		betList = (Vector<Bet>)database.readObjectFromFile("bets.bin");
		if (betList == null){
			betList = new Vector<Bet>(0);
		}

		man = new BetManager(database);
		
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
	        	        
	        synchronized(lastMatches){
	        	lastMatches=new String(message);
	        }
	        
	        try {
				Thread.sleep(Constants.TIME_BETWEEN_ROUNDS);
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
		        
		        /* Send the results to all the active clients. */
		        activeClients.sendMessageAll(message, null, null);
		        
		        /* Creates a new batch of games. */
		        man.refreshMatches();
			}
        }
	}
	
	private void notifyBets(){
		Iterator<Bet> it = betList.iterator();
		Bet bet;
		
		while(it.hasNext()){
			bet=it.next();
			
			/* The client has won. */
			if(gameResults[(bet.getGameNumber())%Constants.NO_GAMES]==0 && bet.getBet().compareToIgnoreCase("X")==0
					|| gameResults[(bet.getGameNumber())%Constants.NO_GAMES]==1 && bet.getBet().equals("1")
					|| gameResults[(bet.getGameNumber())%Constants.NO_GAMES]==2 && bet.getBet().equals("2")){
				
				/* We first update the value in the persistent memory, by informing the database. */
				database.increaseCredits(bet.getUser(), bet.getCredits() * 3);
				
				/* Then, we inform the client. */
				activeClients.sendMessageUser("Congratulations, it looks like your guess was right about game " +
						bet.getGameNumber() + ". You won: " + bet.getCredits() * 3 + " Credits!", bet.getUser());
			}
			else {
				activeClients.sendMessageUser("Sorry, it looks like your guess wasn't right about game " +
						+ bet.getGameNumber() + "... Please try again!", bet.getUser());
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
	
	public Vector<Bet> getBetList(){
		return betList;
	}
}
